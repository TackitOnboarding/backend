package org.example.tackit.domain.Free_board.Free_tag.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.example.tackit.domain.Free_board.Free_tag.dto.response.FreeTagPostResponseDto;
import org.example.tackit.domain.entity.FreePost;
import org.example.tackit.domain.entity.FreePostImage;
import org.example.tackit.domain.entity.FreeTagMap;
import org.example.tackit.domain.entity.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.example.tackit.domain.entity.QFreePostImage.freePostImage;
import static org.example.tackit.domain.entity.QMember.member;
import static org.example.tackit.domain.entity.QFreePost.freePost;
import static org.example.tackit.domain.entity.QFreeTag.freeTag;
import static org.example.tackit.domain.entity.QFreeTagMap.freeTagMap;

public class FreeTagCustomRepositoryImpl implements FreeTagCustomRepository{
    private final JPAQueryFactory jpaQueryFactory;

    // 스프링이 JPAQueryFactory를 주입
    public FreeTagCustomRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<FreeTagPostResponseDto> findPostsByTagId(Long tagId, String organization, Pageable pageable){
        // 1. 태그 ID로 해당 게시글 ID 조회
        List<Long> postIds = jpaQueryFactory
                .select(freeTagMap.freePost.id)
                .from(freeTagMap)
                .where(freeTagMap.tag.id.eq(tagId))
                .fetch();

        if (postIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        // 2. 게시글 + 작성자 정보 조회 (페이징 적용)
        List<FreePost> posts = jpaQueryFactory
                .selectFrom(freePost)
                .join(freePost.writer).fetchJoin()
                .where(freePost.id.in(postIds),
                        freePost.accountStatus.eq(AccountStatus.ACTIVE)
                        // freePost.writer.organization.eq(organization)
                )
                .orderBy(freePost.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> pagedPostIds = posts.stream().map(FreePost::getId).toList();


        // 3. 태그 일괄 조회
        List<FreeTagMap> tagMappings = jpaQueryFactory
                .selectFrom(freeTagMap)
                .join(freeTagMap.tag, freeTag).fetchJoin()
                .where(freeTagMap.freePost.id.in(pagedPostIds))
                .fetch();

        Map<Long, List<String>> tagMap = tagMappings.stream()
                .collect(Collectors.groupingBy(
                        map -> map.getFreePost().getId(),
                        Collectors.mapping(m -> m.getTag().getTagName(), Collectors.toList())
                ));

        // 이미지 조회
        List<FreePostImage> images = jpaQueryFactory
                .selectFrom(freePostImage)
                .where(freePostImage.freePost.id.in(pagedPostIds))
                .fetch();

        Map<Long, String> imageMap = images.stream()
                .collect(Collectors.toMap(
                        img -> img.getFreePost().getId(),
                        FreePostImage::getImageUrl,
                        (existing, duplicate) -> existing // 중복 시 첫 번째만 유지
                ));

        // 4. DTO 변환
        List<FreeTagPostResponseDto> content = posts.stream()
                .map(post -> new FreeTagPostResponseDto(
                        post.getId(),
                        post.getWriter().getNickname(),
                        post.getWriter().getProfileImageUrl(),
                        post.getTitle(),
                        post.getContent(),
                        tagMap.getOrDefault(post.getId(), List.of()),
                        post.getCreatedAt(),
                        imageMap.get(post.getId())
                ))
                .toList();

        // 전체 개수 조회 (총 몇 개 있는지)
        Long total = jpaQueryFactory
                .select(freePost.countDistinct())
                .from(freePost)
                .join(freePost.tagMaps, freeTagMap)
                .join(freeTagMap.tag, freeTag)
                .where(
                        freeTag.id.eq(tagId),
                        freePost.accountStatus.eq(AccountStatus.ACTIVE)
                )
                .fetchOne();
        return new PageImpl<>(content, pageable, total);
    }

}
