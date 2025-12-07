package com.sep.mmms_backend.dto;

import lombok.Getter;

@Getter
public class MemberIdWithNameDto {
    Integer memberId;
    String fullName;

    public MemberIdWithNameDto(Integer memberId, String name) {
        this.memberId = memberId;
        this.fullName = name;
    }
}
