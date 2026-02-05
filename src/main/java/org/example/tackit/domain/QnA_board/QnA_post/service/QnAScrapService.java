package org.example.tackit.domain.QnA_board.QnA_post.service;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.QnA_board.QnA_post.dto.response.QnACheckScrapResponseDto;
import org.example.tackit.domain.QnA_board.QnA_post.dto.response.QnAScrapResponseDto;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAMemberRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAPostRepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAScrapRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.notification.service.NotificationService;
import org.example.tackit.common.dto.PageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class QnAScrapService {
    private final QnAScrapRepository qnAScrapRepository;
    private final QnAPostRepository qnAPostRepository;
    private final QnAMemberRepository qnAMemberRepository;
    private final QnAPostTagService tagService;
    private final NotificationService notificationService;


    /*
    // 스크랩한적 있으면 스크랩 취소, 없으면 저장
    @Transactional
    public QnAScrapResponseDto toggleScrap(long postId, String email, String userOrg){
        Member member = qnAMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        QnAPost post = qnAPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        // 작성글의 소속과 찜하는 사람 간 소속(organization)이 일치하지 않으면 예외
        if (!post.getWriter().getOrganization().equals(userOrg)) {
            throw new AccessDeniedException("해당 조직 게시글이 아닙니다.");
        }

        Optional<QnAScrap> existing = qnAScrapRepository.findByMemberAndQnaPost(member, post);

        if (existing.isPresent()) {
            qnAScrapRepository.delete(existing.get());
            post.decreaseScrapCount();
            return new QnAScrapResponseDto(false, null);
        }
        QnAScrap scrap = QnAScrap.builder()
                .member(member)
                .qnaPost(post)
                .savedAt(LocalDateTime.now())
                .build();
        qnAScrapRepository.save(scrap);
        post.increaseScrapCount();

        // 1. 알림 전송
        if(!post.getWriter().getId().equals(member.getId())){
            Member postWriter = post.getWriter();
            String message = member.getNickname() + "님이 글을 스크랩하였습니다.";
            String url = "/api/qna-post/" + post.getId();

            // 2. 알림 엔티티 생성
            Notification notification = Notification.builder()
                    .member(postWriter)
                    .type(NotificationType.SCRAP)
                    .message(message)
                    .relatedUrl(url)
                    .fromMemberId(member.getId())
                    .build();

            //3. 알림 저장 및 전송을 위해 NotificationService 호출
            notificationService.send(notification);
        }
        return new QnAScrapResponseDto(true, scrap.getSavedAt());
    }

    // 내가 찜한 글 불러오기
    // 찜은 사용자 개인이 찜한 글 + 찜할때 이미 소속 검사함 -> 소속확인 필요 x
    @Transactional(readOnly = true)
    public PageResponseDTO<QnACheckScrapResponseDto> getMyQnAScraps(String email, Pageable pageable) {
        Member user = qnAMemberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<QnAScrap> page = qnAScrapRepository.findByMemberAndQnaPost_Status(user, AccountStatus.ACTIVE, pageable);
        List<QnAPost> posts = page.getContent().stream() // 실제 스크랩들 꺼냄
                .map(QnAScrap::getQnaPost) // 각 스크랩에서 게시글 꺼냄
                .toList(); // 게시글 리스트들 만들기

        Map<Long, List<String>> tagMap = tagService.getTagNamesByPosts(posts);

        return PageResponseDTO.from(page, scrap -> {
            QnAPost post = scrap.getQnaPost();
            List<String> tagNames = tagMap.getOrDefault(post.getId(), List.of());
            return QnACheckScrapResponseDto.fromEntity(post, Post.QnA, tagNames);
        });
    }


     */
}
