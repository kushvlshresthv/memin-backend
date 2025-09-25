package com.sep.mmms_backend.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
//Setter is used for preparing test cases
@Setter
public class MemberCreationDto {
    @NotBlank(message = "member's first name can't be blank")
    private String firstName;

    @NotBlank(message = "member's last name can't be blank")
    private String lastName;

    //NOTE: The following field is not made 'required' because later the application can be evolved to support english-based minutes as well which does not need the following fields
    private String firstNameNepali;

    private String lastNameNepali;

    private String institution;  //example: Pulchowk Campus, IOE

    @NotBlank(message = "member's post can't be blank")
    private String post;

    @Email(message="email should be valid")
    private String email;

    //Role in the committee
    @NotBlank(message="member's role should be specified in the committee")
    private String role;
}