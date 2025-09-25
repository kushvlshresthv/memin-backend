package com.sep.mmms_backend.dto;


import com.sep.mmms_backend.entity.Agenda;
import com.sep.mmms_backend.entity.Decision;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class MeetingDetailsDto {
    private final int id;
    private final String title;
    private final String description;
    private final LocalDate heldDate;
    private final LocalTime heldTime;
    private final String heldPlace;
    private final LocalDate createdDate;
    private final LocalDate updatedDate;
    private final Set<MemberSummaryDto> invitees = new HashSet<>();
    private final List<DecisionDto> decisions = new ArrayList<>();
    private final List<AgendaDto> agendas = new ArrayList<>();

    public MeetingDetailsDto(Meeting meeting) {
        this.id = meeting.getId();
        this.title = meeting.getTitle();
        this.description = meeting.getDescription();
        this.heldDate = meeting.getHeldDate();
        this.heldTime = meeting.getHeldTime();
        this.heldPlace = meeting.getHeldPlace();
        this.createdDate = meeting.getCreatedDate();
        this.updatedDate = meeting.getUpdatedDate();

        for(Member invitee: meeting.getInvitees()) {
            this.invitees.add(new MemberSummaryDto(invitee, meeting.getCommittee().getId()));
        }

        for(Decision decision: meeting.getDecisions()) {
            this.decisions.add(new DecisionDto(decision));
        }

        for(Agenda agenda: meeting.getAgendas()) {
            this.agendas.add(new AgendaDto(agenda));
        }
    }
}
