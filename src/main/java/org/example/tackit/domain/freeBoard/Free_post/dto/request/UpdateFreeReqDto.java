package org.example.tackit.domain.freeBoard.Free_post.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateFreeReqDto {
    private String title;
    private String content;
    private List<Long> tagIds;
    private MultipartFile image;   // 새 이미지 업로드 (선택)
    private boolean removeImage;   // true면 기존 이미지 삭제

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
