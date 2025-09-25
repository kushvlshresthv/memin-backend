package com.sep.mmms_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdationDto {
    Integer id;
    private String firstName;
    private String lastName;
    private String firstNameNepali;
    private String lastNameNepali;
    private String institution;  //example: Pulchowk Campus, IOE
    private String post;
    private String email;
}
