package org.example.tackit.domain.admin.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.entity.org.MemberOrg;
import org.example.tackit.domain.entity.post.Post;

@Getter
@AllArgsConstructor
public class ReportedPostDTO {

  private long id;
  private String title;
  private String nickname;
  private LocalDateTime createdAt;
  private int reportCount;


  public static ReportedPostDTO from(Post post) {
    MemberOrg writer = post.getWriter();

    return new ReportedPostDTO(
        post.getId(),
        post.getTitle(),
        writer.getNickname(),
        post.getCreatedAt(),
        post.getReportCnt()
    );
  }
}
