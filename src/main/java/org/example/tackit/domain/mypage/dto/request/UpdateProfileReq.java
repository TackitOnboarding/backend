package org.example.tackit.domain.mypage.dto.request;

import lombok.Getter;

@Getter
public class UpdateProfileReq {
    private String nickname;
    private String password;
    private String profileImageUrl;
}
