package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.MeetingCreationDto;
import com.sep.mmms_backend.dto.MeetingDetailsDto;
import com.sep.mmms_backend.dto.MeetingSummaryDto;
import com.sep.mmms_backend.dto.MeetingUpdationDto;
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
            @RequestParam(required=true) int committeeId,
            Authentication authentication) {

        Committee committee = committeeService.findCommitteeById(committeeId);

        Meeting savedMeeting =  meetingService.saveNewMeeting(meetingCreationDto, committee, authentication.getName());

        MeetingSummaryDto savedMeetingSummary = new MeetingSummaryDto(savedMeeting);
        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_CREATION_SUCCESSFUL, savedMeetingSummary));
    }


    //TODO: Create Tests
    @PostMapping("/updateMeetingDetails")
    public ResponseEntity<Response> updateMeetingDetails(
            @RequestBody(required = true) MeetingUpdationDto meetingUpdationDto,
            Authentication authentication) {

        Meeting savedMeeting =  meetingService.updateExistingMeetingDetails(meetingUpdationDto, authentication.getName());

        MeetingDetailsDto meetingDetailsDto = new MeetingDetailsDto(savedMeeting);
        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_UPDATION_SUCCESS, meetingDetailsDto));
    }


    //TODO: Create Tests
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
    public ResponseEntity<Response> removeInviteeFromMeeting(@RequestParam int committeeId, @RequestParam int meetingId, @RequestParam("memberId") int inviteeToBeRemoved, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        Meeting meeting = meetingService.findMeetingById(meetingId);

        meetingService.removeInviteeFromMeetig(inviteeToBeRemoved, committee, meeting, authentication.getName());

        return ResponseEntity.ok(new Response(ResponseMessages.MEETING_INVITEE_REMOVAL_SUCCESS));
    }

    //TODO: Create Tests
    @GetMapping("getMeetingDetails")
    public ResponseEntity<Response> getMeetingDetails(@RequestParam int committeeId, @RequestParam int meetingId, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        Meeting meeting = meetingService.findMeetingById(meetingId);
        Meeting meetingDetails = meetingService.getMeetingDetails(committee, meeting, authentication.getName());

        MeetingDetailsDto meetingDto = new MeetingDetailsDto(meetingDetails);
        return ResponseEntity.ok(new Response("Requested meeting details: ", meetingDto));
    }
}
