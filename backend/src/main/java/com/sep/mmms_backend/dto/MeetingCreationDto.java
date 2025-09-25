package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.global_constants.ValidationErrorMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Getter
@Setter
public class MeetingCreationDto {
    @NotBlank(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    private String title;

    private String description;

    @NotNull(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    private LocalDate heldDate;

    @NotNull(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    private LocalTime heldTime;

    @NotBlank(message = ValidationErrorMessages.FIELD_CANNOT_BE_EMPTY)
    private String heldPlace;

    private LinkedHashSet<Integer> inviteeIds = new LinkedHashSet<>();

    private List<String> decisions = new ArrayList<>();

    private List<String> agendas = new ArrayList<>();
}
