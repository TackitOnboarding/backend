package org.example.tackit.domain.noticeBoard.Notice_post.dto.request;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;


@Getter
public class UpdateNoticeReqDto {

    private String title;
    private String content;
    private MultipartFile image;   // 새 이미지 업로드 (선택)
    private boolean removeImage;   // true면 기존 이미지 삭제
    private boolean commentEnabled;

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
