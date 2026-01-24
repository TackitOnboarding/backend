package org.example.tackit.domain.Notice_board.Notice_post.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NoticePostReqDto {
    private String title;
    private String content;
    private MultipartFile image;

    private boolean commentEnabled;

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
