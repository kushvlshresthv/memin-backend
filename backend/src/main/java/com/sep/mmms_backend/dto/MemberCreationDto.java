package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.validators.annotations.CheckUsernameAvailability;
import com.sep.mmms_backend.validators.annotations.UsernameFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberCreationDto {
    @NotBlank(message = "member's first name can't be blank")
    private String firstName;

    @NotBlank(message = "member's last name can't be blank")
    private String lastName;

    @UsernameFormat
    @CheckUsernameAvailability
    @NotBlank(message="member's username can't be blank")
    private String username;

    private String institution;  //example: Pulchowk Campus, IOE

    @NotBlank(message = "member's title can't be blank")
    private String title;  //example: Mr, Dr

    private String post;  //example: IMO Cheif, Campus Chief


    @NotBlank(message="member's email can't be blank")
    @Email(message="email should be valid")
    private String email;
}
