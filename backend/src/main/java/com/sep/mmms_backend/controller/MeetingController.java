package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.*;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashSet;
import java.util.List;

@RestController
@RequestMapping("api")
public class MeetingController {
    private final MeetingService meetingService;
    private final CommitteeService committeeService;

    MeetingController(MeetingService meetingService, CommitteeService committeeService) {
        this.meetingService = meetingService;
        this.committeeService = committeeService;
    }


    //TODO: Create Tests
    @PostMapping("/createMeeting")
    public ResponseEntity<Response> createMeeting(
            @RequestBody(required = true) MeetingCreationDto meetingCreationDto,
            Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(meetingCreationDto.getCommitteeId());
        Meeting savedMeeting = meetingService.saveNewMeeting(meetingCreationDto, committee, authentication.getName());
        MeetingSummaryDto savedMeetingSummary = new MeetingSummaryDto(savedMeeting);
        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_CREATION_SUCCESSFUL, savedMeetingSummary));
    }


    @PostMapping("/updateMinute")
    public ResponseEntity<Response> createMeeting(@RequestBody MinuteUpdationDto meetingUpdationDto, @RequestParam int committeeId, @RequestParam int meetingId, Authentication authentication) {
        meetingService.updateExistingMeetingMinute(meetingUpdationDto, committeeId, meetingId, authentication.getName());

        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_UPDATION_SUCCESS));
    }

    @GetMapping("/getMeetingsOfCommittee")
    public ResponseEntity<Response> getMeetingsOfCommittee(@RequestParam(required = true) int committeeId, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);

        List<MeetingSummaryDto> meetings = meetingService.getMeetingOfCommittee(committee, authentication.getName());

        return ResponseEntity.ok(new Response(ResponseMessages.MEETINGS_OF_COMMITTEE, meetings));
    }


    //TODO: Create Tests
//    @Deprecated
//    @PostMapping("/updateMeetingDetails")
//    public ResponseEntity<Response> updateMeetingDetails(
//            @RequestBody(required = true) MeetingUpdationDto meetingUpdationDto,
//            Authentication authentication) {
//
//        Meeting savedMeeting = meetingService.updateExistingMeetingDetails(meetingUpdationDto, authentication.getName());
//
//        MeetingDetailsForEditDto meetingDetailsForEditDto = new MeetingDetailsForEditDto(savedMeeting);
//        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_UPDATION_SUCCESS, meetingDetailsForEditDto));
//    }


    //TODO: Create Tests
    @Deprecated
    @PostMapping("addInviteesToMeeting")
    public ResponseEntity<Response> addInviteesToMeeting(@RequestParam int committeeId, @RequestParam int meetingId, @RequestBody LinkedHashSet<Integer> newInviteeIds, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        Meeting meeting = meetingService.findMeetingById(meetingId);

        meetingService.addInviteesToMeeting(newInviteeIds, committee, meeting, authentication.getName());

        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_INVITEES_ADDITION_SUCCESS));
    }


    //TODO: Create Tests
    //TODO: Figure out the appropriate HTTP verb for this
    @PostMapping("removeInviteeFromMeeting")
    @Deprecated
    public ResponseEntity<Response> removeInviteeFromMeeting(@RequestParam int committeeId, @RequestParam int meetingId, @RequestParam("memberId") int inviteeToBeRemoved, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        Meeting meeting = meetingService.findMeetingById(meetingId);

        meetingService.removeInviteeFromMeetig(inviteeToBeRemoved, committee, meeting, authentication.getName());

        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_INVITEE_REMOVAL_SUCCESS));
    }

    @GetMapping("getMeetingDetailsForEdit")
    public ResponseEntity<Response> getMeetingDetailsForEdit(@RequestParam int meetingId, Authentication authentication) {
        MeetingDetailsForEditDto meetingDetailsForEditDto = meetingService.getMeetingDetails(meetingId, authentication.getName());
        return ResponseEntity.ok(new Response("Requested meeting details: ", meetingDetailsForEditDto));
    }

}
