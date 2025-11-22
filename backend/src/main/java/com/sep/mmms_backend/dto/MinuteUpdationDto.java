package com.sep.mmms_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class MinuteUpdationDto {
    String committeeName;
    String committeeDescription;
    LocalDate meetingHeldDate;
    LocalTime meetingHeldTime;
    String meetingHeldPlace;
    List<DecisionDto> decisions;
    List<AgendaDto> agendas;
}
