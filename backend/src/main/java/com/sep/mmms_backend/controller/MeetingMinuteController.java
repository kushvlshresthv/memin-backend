package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.MinuteDataDto;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MeetingMinutePreparationService;
import com.sep.mmms_backend.service.MeetingService;
import com.sep.mmms_backend.utils.NumberUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Controller
public class MeetingMinuteController {

        private final MeetingMinutePreparationService meetingMinutePreparationService;
        private final MeetingService meetingService;
        private final CommitteeService committeeService;
        public MeetingMinuteController(MeetingMinutePreparationService meetingMinutePreparationService, MeetingService meetingService, CommitteeService committeeService) {
            this.meetingMinutePreparationService = meetingMinutePreparationService;
            this.meetingService = meetingService;
            this.committeeService = committeeService;
        }

        @GetMapping("api/getDataForMinute")
        public ResponseEntity<Response> getDataForMinute(@RequestParam int committeeId, @RequestParam int meetingId, Authentication authentication) {
            Committee committee = committeeService.findCommitteeById(committeeId);
            Meeting meeting =  meetingService.findMeetingById(meetingId);

            MinuteDataDto minuteData = this.meetingMinutePreparationService.prepareDataForMinute(committee, meeting, authentication.getName());

            return ResponseEntity.ok(new Response("Meeting Minute Data: ", minuteData));
        }


}
