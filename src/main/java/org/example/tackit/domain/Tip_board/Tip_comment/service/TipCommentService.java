package org.example.tackit.domain.Tip_board.Tip_comment.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Tip_board.Tip_comment.dto.req.TipCommentCreateDto;
import org.example.tackit.domain.Tip_board.Tip_comment.dto.req.TipCommentUpdateDto;
import org.example.tackit.domain.Tip_board.Tip_comment.dto.resp.TipCommentResponseDto;
import org.example.tackit.domain.Tip_board.Tip_comment.repository.TipCommentRepository;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipPostRepository;
import org.example.tackit.domain.auth.login.repository.MemberOrgRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.notification.service.NotificationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TipCommentService {
    private final TipCommentRepository tipCommentRepository;
    private final TipPostRepository tipPostRepository;
    private final NotificationService notificationService;
    private final MemberOrgRepository memberOrgRepository;

    // 댓글 생성 (SENIOR + NEWBIE 둘 다)
    @Transactional
    public TipCommentResponseDto createComment(TipCommentCreateDto dto, String email, Long orgId){
        MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        TipPost post = tipPostRepository.findById(dto.getTipPostId())
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        TipComment comment = TipComment.builder()
                .writer(member)
                .accountStatus(AccountStatus.ACTIVE)
                .tipPost(post)
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        // 댓글 DB 저장
        TipComment savedComment = tipCommentRepository.save(comment);

        // 알림 전송
        if(!post.getWriter().getId().equals(member.getId())) {
            MemberOrg postWriter = post.getWriter();

            String message = member.getNickname() + "님이 글에 댓글을 남겼습니다.";
            String url = "/api/tip-post/" + post.getId();

            // 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .member(postWriter.getMember())
                    .type(NotificationType.COMMENT)
                    .message(message)
                    .relatedUrl(url)
                    .fromMemberOrgId(member.getId())
                    .build();

            // 알림 저장 및 전송
            notificationService.send(notification);
        }
        return new TipCommentResponseDto(savedComment);
    }


    // 전체 댓글 조회
    @Transactional (readOnly = true)
    public List<TipCommentResponseDto> getCommentByPost(Long postId, Long orgId) {
        TipPost post = tipPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        if (!post.getWriter().getId().equals(orgId)) {
            throw new AccessDeniedException("해당 조직의 게시글만 조회할 수 있습니다.");
        }

        return tipCommentRepository.findByTipPost(post)
                .stream()
                .map(TipCommentResponseDto::new)
                .toList();
    }

    // 댓글 수정 (작성자만 가능)
    @Transactional
    public TipCommentResponseDto updateComment(Long commentId, TipCommentUpdateDto dto, String email, Long orgId){
        MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        TipComment comment = tipCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        if( !comment.getWriter().getId().equals(member.getId())) {
            throw new AccessDeniedException("작성자만 수정할 수 있습니다.");
        }

        comment.updateContent(dto.getContent());

        return new TipCommentResponseDto(comment);
    }

    // 댓글 삭제 (작성자, 관리자만 가능)
    @Transactional
    public void deleteComment(Long commentId, String email, Long orgId) {
        MemberOrg member = memberOrgRepository.findByMemberEmailAndId(email, orgId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        TipComment comment = tipCommentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("댓글이 존재하지 않습니다."));

        boolean isWriter = comment.getWriter().getId().equals(member.getId());
        boolean isAdmin = (member.getMemberRole() == MemberRole.ADMIN)
                            && (member.getMemberType() == MemberType.ADMIN);

        if (!isWriter && !isAdmin) {
            throw new AccessDeniedException("작성자 또는 관리자만 삭제할 수 있습니다.");
        }

        tipCommentRepository.delete(comment); // hard delete
    }

    // 댓글 신고하기
    @Transactional
    public void increaseCommentReportCount(Long commentId, Long orgId) {
        TipComment comment = tipCommentRepository.findById(commentId)
                .orElseThrow( () -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        // 소속 검증
        if (!comment.getWriter().getId().equals(orgId)) {
            throw new AccessDeniedException("해당 조직의 댓글만 신고할 수 있습니다.");
        }
        comment.increaseReportCount();

        if (comment.getReportCount() >= 3) {
            tipCommentRepository.delete(comment); // hard delete
        }
    }

}