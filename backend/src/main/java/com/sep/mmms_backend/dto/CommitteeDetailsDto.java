package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CommitteeDetailsDto {
    private final int id;
    private final String name;
    private final String description;
    private final LocalDate  createdDate;
    private final String status;
    private final Integer maxNoOfMeetings;
    private List<MeetingSummaryDto> meetings = new ArrayList<>();
    private List<MemberSummaryDto> members = new ArrayList<>();

    public CommitteeDetailsDto(Committee committee) {
        this.id = committee.getId();
        this.name = committee.getName();
        this.description = committee.getDescription();
        this.createdDate = committee.getCreatedDate();
        this.status = committee.getStatus().toString();
        this.maxNoOfMeetings = committee.getMaxNoOfMeetings();

        for(Meeting meeting: committee.getMeetings()) {
            meetings.add(new MeetingSummaryDto(meeting));
        }

        for(CommitteeMembership membership: committee.getMemberships()) {
            members.add(new MemberSummaryDto(membership.getMember(), this.id));
        }

    }
}
