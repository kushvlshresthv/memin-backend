package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.enums.CommitteeStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommitteeUpdationDto {
    private Integer id;

    private String name;

    private String description;

    private CommitteeStatus status = CommitteeStatus.ACTIVE;

    Integer maximumNumberOfMeetings;
}
