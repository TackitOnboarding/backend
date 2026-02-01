package org.example.tackit.domain.Free_board.Free_comment.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Free_board.Free_comment.dto.req.FreeCommentCreateDto;
import org.example.tackit.domain.Free_board.Free_comment.dto.req.FreeCommentUpdateDto;
import org.example.tackit.domain.Free_board.Free_comment.dto.resp.FreeCommentRespDto;
import org.example.tackit.domain.Free_board.Free_comment.repository.FreeCommentRepository;
import org.example.tackit.domain.Free_board.Free_post.repository.FreePostJPARepository;
import org.example.tackit.domain.auth.login.repository.MemberOrgRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.notification.service.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FreeCommentService {
    private final FreeCommentRepository freeCommentRepository;
    private final FreePostJPARepository freePostJPARepository;
    private final MemberOrgRepository memberOrgRepository;
    private final NotificationService notificationService;

    // [ 댓글 생성 ]
    @Transactional
    public FreeCommentRespDto createComment(FreeCommentCreateDto dto, String email, Long orgId){
        MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        FreePost post = freePostJPARepository.findById(dto.getFreePostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        FreeComment comment = FreeComment.builder()
                .writer(member)
                .freePost(post)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        // 1. 댓글 DB 저장
        FreeComment savedComment = freeCommentRepository.save(comment);

        // 2. 알림 전송
        if(!post.getWriter().getId().equals(member.getId())){
            MemberOrg postWriter = post.getWriter(); // 알림 받을 대상(게시글 작성자)
            String message = member.getNickname() + "님이 글에 댓글을 남겼습니다.";
            String url = "/api/free-posts/" + post.getId();

            // 3. 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .member(postWriter.getMember())
                    .type(NotificationType.COMMENT)
                    .message(message)
                    .relatedUrl(url)
                    .fromMemberOrgId(member.getId())
                    .build();

            // 4. 알림 저장 및 전송을 위해 NotificationService 호출
            notificationService.send(notification);
        }

        return new FreeCommentRespDto(savedComment);
    }

    // [ 게시글 댓글 조회 ]
    @Transactional
    public List<FreeCommentRespDto> getCommentByPost(Long postId, Long orgId){
        FreePost post = freePostJPARepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getWriter().getId().equals(orgId)) {
            throw new AccessDeniedException("해당 조직의 게시글만 조회할 수 있습니다.");
        }

        return freeCommentRepository.findByFreePost(post)
                .stream()
                .map(FreeCommentRespDto::new)
                .toList();
    }

    // [ 댓글 수정 ] : 작성자만 가능
    @Transactional
    public FreeCommentRespDto updateComment(Long commentId, FreeCommentUpdateDto dto, String email, Long orgId){
        MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        FreeComment comment = freeCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        boolean isWriter = comment.getWriter().getId().equals(member.getId());

        if (!isWriter) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        comment.updateContent(dto.getContent());

        return new FreeCommentRespDto(comment);
    }

    // [ 댓글 삭제 ] : 작성자, 관리자만 가능
    @Transactional
    public void deleteComment(Long commentId, String email, Long orgId){
        MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        FreeComment comment = freeCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        boolean isWriter = comment.getWriter().getId().equals(member.getId());
        boolean isAdmin = (member.getMemberRole() == MemberRole.ADMIN)
                && (member.getMemberType() == MemberType.ADMIN);

        if (!isWriter && !isAdmin) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        // Hard Delete
        freeCommentRepository.delete(comment);
    }

    // [ 댓글 신고 ]
    @Transactional
    public void increaseCommentReportCount(Long id, Long orgId){
        FreeComment comment = freeCommentRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!comment.getWriter().getId().equals(orgId)) {
            throw new AccessDeniedException("해당 조직의 댓글만 신고할 수 있습니다.");
        }
        comment.increaseReportCount();

        if (comment.getReportCount() >= 3) {
            freeCommentRepository.delete(comment); // Hard Delete
        }
    }
}
