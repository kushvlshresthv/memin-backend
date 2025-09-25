package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.enums.CommitteeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CommitteeCreationDto {
    @NotBlank(message ="committee name is required")
    private String name;

    @NotBlank(message="committee description/aim is required")
    private String description;

    private CommitteeStatus status = CommitteeStatus.ACTIVE;

    Integer maximumNumberOfMeetings;

    Map<Integer, String> members = new HashMap<>();

    @NotNull(message="committee coordinator is missing")
    Integer coordinatorId;
}
