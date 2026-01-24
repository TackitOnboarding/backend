package org.example.tackit.common.homePopular;

import lombok.RequiredArgsConstructor;
import org.example.tackit.domain.Free_board.Free_post.repository.FreePostJPARepository;
import org.example.tackit.domain.QnA_board.QnA_post.repository.QnAPostRepository;
import org.example.tackit.domain.Tip_board.Tip_post.repository.TipPostRepository;
import org.example.tackit.domain.entity.Status;
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
                                Status.ACTIVE, startOfWeek, now)
                        .stream()
                        .filter(post -> post.getWriter().getOrganization().equals(organization))
                        .map(HomePopularPostRespDto::fromFree)
                        .toList()
        );

        // QnA
        combined.addAll(
                qnaPostRepository
                        .findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
                                Status.ACTIVE, startOfWeek, now)
                        .stream()
                        .filter(post -> post.getWriter().getOrganization().equals(organization))
                        .map(HomePopularPostRespDto::fromQna)
                        .toList()
        );

        // Tip
        combined.addAll(
                tipPostRepository
                        .findTop3ByStatusAndCreatedAtBetweenOrderByViewCountDescScrapCountDesc(
                                Status.ACTIVE, startOfWeek, now)
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
}

