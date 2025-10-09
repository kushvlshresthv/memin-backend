package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.Agenda;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Decision;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class MinuteDataDto {
    String meetingHeldDateNepali;
    LocalDate meetingHeldDate;
    String meetingHeldDay;
    String partOfDay;
    String meetingHeldTime;
    String meetingHeldPlace;
    String committeeName;
    String committeeDescription;
    String coordinatorFullName;
    List<DecisionDto> decisions;
    List<AgendaDto> agendas;
    List<CommitteeMembershipDto> committeeMemberships;
}
