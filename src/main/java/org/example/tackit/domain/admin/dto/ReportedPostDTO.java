package org.example.tackit.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.tackit.domain.admin.model.ReportablePost;
import org.example.tackit.domain.entity.MemberOrg;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReportedPostDTO {
    private long id;
    private String title;
    private String nickname;
    private LocalDateTime createdAt;
    private int reportCount;


    public static ReportedPostDTO fromEntity(ReportablePost post){
        MemberOrg writer = post.getWriter();

        return new ReportedPostDTO(
                post.getId(),
                post.getTitle(),
                writer.getNickname(),
                post.getCreatedAt(),
                post.getReportCount()
        );
    }
}
