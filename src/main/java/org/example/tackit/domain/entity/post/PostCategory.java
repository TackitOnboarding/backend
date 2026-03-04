package org.example.tackit.domain.entity.post;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PostCategory {

  // TIP 게시판 카테고리
  EXPERIENCE("경험담 공유", PostType.TIP),
  MENTORING("교육&멘토링", PostType.TIP),
  ONBOARDING("온보딩", PostType.TIP),
  USEFUL_TIP("유용한 팁", PostType.TIP),
  TEAM_CULTURE("팀 문화", PostType.TIP),

  // QNA 게시판 카테고리
  CULTURE_ADAPT("문화적응", PostType.QNA),
  COMMUNICATION("소통고민", PostType.QNA),
  JUNIOR_CONCERN("신입고민", PostType.QNA),
  SYSTEM("운영&제도", PostType.QNA),
  ACTIVITY_QUESTION("활동질문", PostType.QNA),

  // FREE 게시판 카테고리
  TASTY_RESTAURANT("맛집추천", PostType.FREE),
  RESOURCE_SHARE("자료공유", PostType.FREE),
  DISCUSSION("자유토론", PostType.FREE),
  HOBBY("취미생활", PostType.FREE),
  DAILY_ACTIVITY("활동일상", PostType.FREE);

  private final String description; // 프론트 한글
  private final PostType parentType; // 연관 게시판

  // 특정 게시판 타입에 해당하는 카테고리 목록만 반환
  public static List<PostCategory> getCategoriesByPostType(PostType postType) {
    return Arrays.stream(PostCategory.values())
        .filter(category -> category.getParentType() == postType)
        .toList();
  }
}