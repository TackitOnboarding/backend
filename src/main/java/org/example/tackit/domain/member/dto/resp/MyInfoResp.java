package org.example.tackit.domain.member.dto.resp;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MyInfoResp {
    private Long memberId;
    private String email;


    private List<MemberProfileResp> profiles;

}
