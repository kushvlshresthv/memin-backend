package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.*;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommitteeController {

    private final CommitteeService committeeService;
    private final MemberService memberService;

    public CommitteeController(CommitteeService committeeService, MemberService memberService) {
        this.committeeService = committeeService;
        this.memberService = memberService;
    }


    //TODO: Create Tests
    //TODO: Fix this route should check if there is exactly one coordinator
    @PostMapping("/createCommittee")
    public ResponseEntity<Response> createCommittee(@RequestBody CommitteeCreationDto committeeCreationDto, Authentication authentication) {
        Committee savedCommittee = committeeService.saveNewCommittee(committeeCreationDto, authentication.getName());
        CommitteeSummaryDto committeeSummaryDto = new CommitteeSummaryDto(savedCommittee);
        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEE_CREATION_SUCCESS, committeeSummaryDto));
    }



    //TODO: Create Tests
    @PostMapping("/updateCommitteeDetails")
    public ResponseEntity<Response> updateExistingCommittee(@RequestBody CommitteeUpdationDto committeeUpdationDto, Authentication authentication) {
        if(committeeUpdationDto.getId() == null) {
            throw new IllegalOperationException("TODO: Handle this");
            //TODO: Exception (handle this)
        }
        Committee committee = committeeService.findCommitteeById(committeeUpdationDto.getId());
        Committee updatedCommittee = committeeService.updateExistingCommittee(committeeUpdationDto,committee, authentication.getName());
        CommitteeSummaryDto committeeSummaryDto = new CommitteeSummaryDto(updatedCommittee);
        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEE_UPDATION_SUCCESS, committeeSummaryDto));
    }

    //TODO: Create Tests
    @GetMapping("/getCommittees")
    public ResponseEntity<Response> getCommittees(Authentication authentication) {
        List<Committee> committees =  committeeService.getCommittees(authentication.getName());
        List<CommitteeSummaryDto> committeeSummaryDtos = new ArrayList<>();
        committees.forEach(committee-> {
            committeeSummaryDtos.add(new CommitteeSummaryDto(committee));
        });
        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEES_RETRIEVED_SUCCESSFULLY, committeeSummaryDtos));
    }


    //TODO: Create Tests
    @PostMapping("/deleteCommittee")
    public ResponseEntity<Response> deleteCommittee(@RequestParam(required = true) int  committeeId, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        committeeService.deleteCommittee(committee, authentication.getName());
        return ResponseEntity.ok(new Response(ResponseMessages.COMMITTEE_DELETED_SUCCESSFULLY));
    }




    /**
     * this route fetches the committee from the database, checks if the committee is accessible by the current user
     *
     * then fetches all the members associated with the committee and sends both of them as response
     */


    //TODO: Create Tests
    @GetMapping("/getCommitteeDetails")
    public ResponseEntity<Response> getCommitteeDetails(@RequestParam int committeeId, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        CommitteeDetailsDto committeeDetails = committeeService.getCommitteeDetails(committee, authentication.getName());
        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEES_RETRIEVED_SUCCESSFULLY,committeeDetails));
    }

    @PostMapping("/addMembersToCommittee")
    //NOTE: LinkedHashSet is made LinkedHashSet to preserve order and avoid duplicate memberIds
    //TODO: Fix (this route should not add more coordinator to the committee)
    //TODO: Create Tests
    public ResponseEntity<Response> addMembershipsToCommittee(@RequestParam int committeeId, @RequestBody LinkedHashSet<NewMembershipRequest> newMemberships, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        committeeService.addMembershipsToCommittee(committee, newMemberships, authentication.getName());
        return ResponseEntity.ok(new Response(ResponseMessages.COMMITTEE_MEMBER_ADDITION_SUCCESS));
    }



    @DeleteMapping("/removeCommitteeMembership")
    //TODO: Create Tests
    public ResponseEntity<Response> removeCommitteeMembership(@RequestParam int committeeId, @RequestParam int memberId, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        Member member = memberService.findMemberById(memberId);
        committeeService.removeCommitteeMembership(committee, member, authentication.getName());
        return ResponseEntity.ok(new Response(ResponseMessages.MEMBERSHIP_REMOVED_SUCCESSFULLY));
    }
}
