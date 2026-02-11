package org.example.tackit.domain.qnaBoard.QnA_post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateQnARequestDto {
    private String title;
    private String content;
    private List<Long> tagIds;

    private Boolean removeImage; // true면 삭제
    private MultipartFile image; // 업로드 파일
}
