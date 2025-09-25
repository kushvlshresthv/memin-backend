package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.Member;
import lombok.Getter;

@Getter
public class MemberSearchResultDto {
    private final int memberId;
    private final String firstName;
    private final String lastName;
    private final String post;
    private final String institution;
    public MemberSearchResultDto(Member member) {
        this.memberId = member.getId();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.post = member.getPost();
        this.institution = member.getInstitution();
   }
}
