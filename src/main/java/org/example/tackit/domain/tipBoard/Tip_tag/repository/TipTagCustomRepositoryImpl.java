package org.example.tackit.domain.tipBoard.Tip_tag.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.tackit.domain.tipBoard.Tip_tag.dto.response.TipTagPostResponseDto;
import org.example.tackit.domain.entity.AccountStatus;
import org.example.tackit.domain.entity.TipPost;
import org.example.tackit.domain.entity.TipPostImage;
import org.example.tackit.domain.entity.TipTagMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


// import static org.example.tackit.domain.entity.QMemberOrg.memberOrg;
import static org.example.tackit.domain.entity.QTipPost.tipPost;
import static org.example.tackit.domain.entity.QTipPostImage.tipPostImage;
import static org.example.tackit.domain.entity.QTipTagMap.tipTagMap;
import static org.example.tackit.domain.entity.QTipTag.tipTag;


@Repository
public class TipTagCustomRepositoryImpl implements TipTagCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    // 스프링이 JPAQueryFactory를 주입
    public TipTagCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    // @Override
    public Page<TipTagPostResponseDto> findPostsByTagId(Long tagId, Long orgId, Pageable pageable) {
        // 1. 태그 ID로 해당 게시글 ID 조회

        List<Long> postIds = jpaQueryFactory
                .select(tipTagMap.tipPost.id)
                .from(tipTagMap)
                .where(tipTagMap.tag.id.eq(tagId))
                .fetch();

        if (postIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2. 게시글 + 작성자 정보 조회 (페이징 적용)
        List<TipPost> posts = jpaQueryFactory
                .selectFrom(tipPost)
                .join(tipPost.writer).fetchJoin()
                .where(tipPost.id.in(postIds),
                        tipPost.accountStatus.eq(AccountStatus.ACTIVE)
                        // memberOrg.id.eq(orgId)
                )
                .orderBy(tipPost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> pagedPostIds = posts.stream().map(TipPost::getId).toList();


        // 3. 태그 일괄 조회
        List<TipTagMap> tagMappings = jpaQueryFactory
                .selectFrom(tipTagMap)
                .join(tipTagMap.tag, tipTag).fetchJoin()
                .where(tipTagMap.tipPost.id.in(pagedPostIds))
                .fetch();

        Map<Long, List<String>> tagMap = tagMappings.stream()
                .collect(Collectors.groupingBy(
                        map -> map.getTipPost().getId(),
                        Collectors.mapping(m -> m.getTag().getTagName(), Collectors.toList())
                ));

        // 각 게시글의 대표 이미지 조회
        List<TipPostImage> images = jpaQueryFactory
                .selectFrom(tipPostImage)
                .where(tipPostImage.tipPost.id.in(pagedPostIds))
                .fetch();

        Map<Long, String> imageMap = images.stream()
                .collect(Collectors.toMap(
                        img -> img.getTipPost().getId(),
                        TipPostImage::getImageUrl,
                        (existing, duplicate) -> existing // 중복 시 첫 번째만
                ));

        // 4. DTO 변환
        List<TipTagPostResponseDto> content = posts.stream()
                .map(post -> new TipTagPostResponseDto(
                        post.getId(),
                        post.getWriter().getNickname(),
                        post.getWriter().getProfileImageUrl(),
                        post.getTitle(),
                        post.getContent(),
                        post.getCreatedAt(),
                        tagMap.getOrDefault(post.getId(), List.of()),
                        imageMap.get(post.getId())
                ))
                .toList();

        // 전체 개수 조회 (총 몇 개 있는지)
        Long total = jpaQueryFactory
                .select(tipPost.countDistinct())
                .from(tipPost)
                .join(tipPost.tagMaps, tipTagMap)
                .join(tipTagMap.tag, tipTag)
                .where(
                        tipTag.id.eq(tagId),
                        tipPost.accountStatus.eq(AccountStatus.ACTIVE)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);

    }
}




