package com.sep.mmms_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Deprecated
public class MeetingUpdationDto {
    private Integer meetingId;

    private String title;

    private String description;

    private LocalDate heldDate;

    private LocalTime heldTime;

    private String heldPlace;

    private List<DecisionDto> decisions = new ArrayList<>();

    private List<AgendaDto> agendas = new ArrayList<>();
}
