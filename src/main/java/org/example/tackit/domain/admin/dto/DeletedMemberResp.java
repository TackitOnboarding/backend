package org.example.tackit.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DeletedMemberResp {
    private long totalCount;
    private List<DeletedMemberDto> members;

    public DeletedMemberResp(List<DeletedMemberDto> members, long totalCount) {
        this.members = members;
        this.totalCount = totalCount;
    }
}
