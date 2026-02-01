package org.example.tackit.domain.Notice_board.Notice_comment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Notice_board.Notice_comment.dto.req.NoticeCommentCreateDto;
import org.example.tackit.domain.Notice_board.Notice_comment.dto.req.NoticeCommentUpdateDto;
import org.example.tackit.domain.Notice_board.Notice_comment.dto.resp.NoticeCommentRespDto;
import org.example.tackit.domain.Notice_board.Notice_comment.repository.NoticeCommentRepository;
import org.example.tackit.domain.Notice_board.Notice_post.repository.NoticePostRepository;
import org.example.tackit.domain.auth.login.repository.MemberOrgRepository;
import org.example.tackit.domain.auth.login.repository.MemberRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.notification.service.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeCommentService {
    private final NoticeCommentRepository noticeCommentRepository;
    private final NoticePostRepository noticePostRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final MemberOrgRepository memberOrgRepository;

    // [ 댓글 생성 ]
    @Transactional
    public NoticeCommentRespDto createComment(NoticeCommentCreateDto dto, String email, Long orgId){
        // 현재 접속한 소속의 프로필 조회
        MemberOrg memberProfile = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        NoticePost post = noticePostRepository.findById(dto.getNoticePostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // 댓글 엔티티 생성
        NoticeComment comment = NoticeComment.builder()
                .writer(memberProfile)
                .noticePost(post)
                .content(dto.getContent())
                .build();

        NoticeComment savedComment = noticeCommentRepository.save(comment);

        // 알림 전송
        if(!post.getWriter().getId().equals(memberProfile.getId())){
            // 알림 받을 대상
            MemberOrg postWriterProfile = post.getWriter();

            String message = memberProfile.getNickname() + "님이 글에 댓글을 남겼습니다.";
            String url = "/api/notice-posts/" + post.getId();

            // 3. 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .member(postWriterProfile.getMember())
                    .memberOrgId(postWriterProfile.getId())
                    .type(NotificationType.COMMENT)
                    .message(message)
                    .relatedUrl(url)
                    .fromMemberOrgId(memberProfile.getId())
                    .build();

            // 4. 알림 저장 및 전송을 위해 NotificationService 호출
            notificationService.send(notification);
        }

        return new NoticeCommentRespDto(savedComment);
    }

    // [ 게시글 댓글 조회 ]
    @Transactional
    public List<NoticeCommentRespDto> getCommentByPost(Long postId, Long orgId){
        NoticePost post = noticePostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getWriter().getId().equals(orgId)) {
            throw new AccessDeniedException("해당 조직의 게시글만 조회할 수 있습니다.");
        }

        return noticeCommentRepository.findByNoticePost(post)
                .stream()
                .map(NoticeCommentRespDto::new)
                .toList();
    }

    // [ 댓글 수정 ] : 작성자만 가능
    @Transactional
    public NoticeCommentRespDto updateComment(Long commentId, NoticeCommentUpdateDto dto, String email, Long orgId){
        MemberOrg memberProfile = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        NoticeComment comment = noticeCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if (!comment.getWriter().getId().equals(memberProfile.getId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        comment.updateContent(dto.getContent());

        return new NoticeCommentRespDto(comment);
    }

    // [ 댓글 삭제 ] : 작성자, 관리자만 가능
    @Transactional
    public void deleteComment(Long commentId, String email, Long orgId){
        MemberOrg memberProfile = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        NoticeComment comment = noticeCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        boolean isWriter = comment.getWriter().getId().equals(memberProfile.getId());
        boolean isAdmin = (memberProfile.getMemberRole() == MemberRole.ADMIN)
                && (memberProfile.getMemberType() == MemberType.ADMIN);

        if (!isWriter && !isAdmin) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        // Hard Delete
        noticeCommentRepository.delete(comment);
    }

    // [ 댓글 신고 ]
    @Transactional
    public void increaseCommentReportCount(long commmentId, Long orgId){

        NoticeComment comment = noticeCommentRepository.findById(commmentId)
                .orElseThrow( () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));

        if (!comment.getWriter().getId().equals(orgId)) {
            throw new AccessDeniedException("해당 조직의 댓글만 신고할 수 있습니다.");
        }
        comment.increaseReportCount();

        if (comment.getReportCount() >= 3) {
            noticeCommentRepository.delete(comment); // Hard Delete
        }
    }
}


