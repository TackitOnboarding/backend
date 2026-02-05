package org.example.tackit.common.homePopular;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Free_board.Free_post.repository.FreePostJPARepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAPostRepository;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipPostRepository;
import org.example.tackit.domain.entity.*;
import org.example.tackit.domain.entity.Org.OrgType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomePopularPostService {

    private final FreePostJPARepository freePostRepository;
    private final QnAPostRepository qnaPostRepository;
    private final TipPostRepository tipPostRepository;


    public List<HomePopularPostRespDto> getPopularPosts(Long orgId) {
        LocalDateTime startOfWeek = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        Pageable top3 = PageRequest.of(0, 3);

        List<HomePopularPostRespDto> combined = new ArrayList<>();

        // Free 게시판
        List<FreePost> freePosts = freePostRepository.findTop3PopularByOrg(
                AccountStatus.ACTIVE,
                startOfWeek,
                now,
                orgId,
                top3
        );
        combined.addAll(freePosts.stream().map(HomePopularPostRespDto::fromFree).toList());

        // QnA 게시판
        List<QnAPost> qnaPosts = qnaPostRepository.findTop3PopularByOrg(
                AccountStatus.ACTIVE,
                startOfWeek,
                now,
                orgId,
                top3
        );

        combined.addAll(qnaPosts.stream().map(HomePopularPostRespDto::fromQna).toList());

        // Tip 게시판
        List<TipPost> tipPosts = tipPostRepository.findTop3PopularByOrg(
                AccountStatus.ACTIVE,
                startOfWeek,
                now,
                orgId,
                top3
        );

        combined.addAll(tipPosts.stream().map(HomePopularPostRespDto::fromTip).toList());

        // 최종 인기글 3개
        return combined.stream()
                .sorted(Comparator.comparing(HomePopularPostRespDto::getViewCount, Comparator.reverseOrder())
                .thenComparing(HomePopularPostRespDto::getScrapCount, Comparator.reverseOrder()))
                .limit(3)
                .toList();
    }
    /*
    public List<HomePopularPostRespDto> getPopularPosts(String organization) {
        LocalDateTime startOfWeek = LocalDate.now()
                .with(DayOfWeek.MONDAY)
                .atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<HomePopularPostRespDto> combined = new ArrayList<>();

        // Free
        combined.addAll(
                freePostRepository
                        .findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
                                AccountStatus.ACTIVE, startOfWeek, now)
                        .stream()
                        .filter(post -> post.getWriter().get.equals(organization))
                        .map(HomePopularPostRespDto::fromFree)
                        .toList()
        );

        // QnA
        combined.addAll(
                qnaPostRepository
                        .findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
                                AccountStatus.ACTIVE, startOfWeek, now)
                        .stream()
                        .filter(post -> post.getWriter().getOrganization().equals(organization))
                        .map(HomePopularPostRespDto::fromQna)
                        .toList()
        );

        // Tip
        combined.addAll(
                tipPostRepository
                        .findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
                                AccountStatus.ACTIVE, startOfWeek, now)
                        .stream()
                        .filter(post -> post.getWriter().getOrganization().equals(organization))
                        .map(HomePopularPostRespDto::fromTip)
                        .toList()
        );

        // 정렬 시 null-safe 처리
        return combined.stream()
                .sorted(Comparator
                        .comparing(
                                (HomePopularPostRespDto dto) -> dto.getViewCount() == null ? 0L : dto.getViewCount(),
                                Comparator.reverseOrder()
                        )
                        .thenComparing(
                                dto -> dto.getScrapCount() == null ? 0L : dto.getScrapCount(),
                                Comparator.reverseOrder()
                        )
                )
                .limit(3)
                .toList();
    }
     */
}

