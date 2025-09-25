package com.sep.mmms_backend.dto;

import com.sep.mmms_backend.entity.Agenda;
import com.sep.mmms_backend.entity.Decision;
import com.sep.mmms_backend.global_constants.ValidationErrorMessages;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
