package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.*;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.response.Response;
import com.sep.mmms_backend.response.ResponseMessages;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommitteeController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService, MemberService memberService) {
        this.committeeService = committeeService;
    }


    //TODO: Create Tests
    //TODO: Fix this route should check if there is exactly one coordinator
    @PostMapping("/createCommittee")
    public ResponseEntity<Response> createCommittee(@RequestBody CommitteeCreationDto committeeCreationDto, Authentication authentication) {
        Committee savedCommittee = committeeService.saveNewCommittee(committeeCreationDto, authentication.getName());
        CommitteeSummaryDto committeeSummaryDto = new CommitteeSummaryDto(savedCommittee);
        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEE_CREATION_SUCCESS, committeeSummaryDto));
    }


    @GetMapping("/getCommitteeDetailsForEditPage")
    public ResponseEntity<Response> getCommitteeDetailsForEditPage(@RequestParam int committeeId, Authentication authentication) {
        Committee committee = this.committeeService.getCommitteeIfAccessible(committeeId, authentication.getName());

        CommitteeDetailsForEditDto committeeDetailsForEditDto = new CommitteeDetailsForEditDto(committee);

        return ResponseEntity.ok(new Response(committeeDetailsForEditDto));
    }


    //TODO: Create Tests
    @PostMapping("/updateCommitteeDetails")
    public ResponseEntity<Response> updateExistingCommittee(@RequestBody CommitteeCreationDto committeeCreationDto, @RequestParam Integer committeeId, Authentication authentication) {
        committeeService.updateExistingCommittee(committeeCreationDto, committeeId, authentication.getName());
        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEE_UPDATION_SUCCESS));
    }


    @GetMapping("/getMyActiveCommitteeNamesAndIds")
    //returns the name and id of 'ACTIVE' all committes for create meeting page
    public ResponseEntity<Response> getMyCommitteeNamesAndIds(Authentication authentication) {
        List<Committee> allCommittees = committeeService.getAllActiveCommittees(authentication.getName());

        List<CommitteeIdAndNameDto> committeeIdsAndNames = new ArrayList<>();
        allCommittees.forEach(committee -> {
            committeeIdsAndNames.add(new CommitteeIdAndNameDto(committee.getId(), committee.getName()));
        });


        return ResponseEntity.ok(new Response(committeeIdsAndNames));
    }

    //TODO: Create Tests
    @GetMapping("/getMyActiveCommittees")
    //returns all the ACTIVE committees for the user
    public ResponseEntity<Response> getMyActiveCommittees(Authentication authentication) {
        List<Committee> committees = committeeService.getAllActiveCommittees(authentication.getName());
        List<CommitteeSummaryDto> committeeSummaryDtos = new ArrayList<>();
        committees.forEach(committee -> {
            committeeSummaryDtos.add(new CommitteeSummaryDto(committee));
        });

        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEES_RETRIEVED_SUCCESSFULLY, committeeSummaryDtos));
    }


    @GetMapping("/getMyInactiveCommittees")
    //returns all the ACTIVE committees for the user
    public ResponseEntity<Response> getMyInactiveCommittees(Authentication authentication) {
        List<Committee> committees = committeeService.getAllInactiveCommittees(authentication.getName());
        List<CommitteeSummaryDto> committeeSummaryDtos = new ArrayList<>();
        committees.forEach(committee -> {
            committeeSummaryDtos.add(new CommitteeSummaryDto(committee));
        });

        return ResponseEntity.ok().body(new Response(ResponseMessages.COMMITTEES_RETRIEVED_SUCCESSFULLY, committeeSummaryDtos));
    }


    @PatchMapping("/toggleCommitteeStatus")
    public ResponseEntity<Response> toggleCommitteeStatus(@RequestParam int committeeId, Authentication authentication) {
        boolean result = committeeService.toggleCommitteeStatus(committeeId, authentication.getName());

        if (result) {
            return ResponseEntity.ok(new Response("Committee status changed to Inactive"));
        } else {
            return ResponseEntity.ok().body(new Response("Committee status changed to Active"));
        }
    }



    //TODO: Create Tests
//    @Deprecated
//    @PostMapping("/deleteCommittee")
//    public ResponseEntity<Response> deleteCommittee(@RequestParam(required = true) int committeeId, Authentication authentication) {
//        Committee committee = committeeService.findCommitteeById(committeeId);
//        committeeService.deleteCommittee(committee, authentication.getName());
//        return ResponseEntity.ok(new Response(ResponseMessages.COMMITTEE_DELETED_SUCCESSFULLY));
//    }


    /**
     * this route fetches the committee from the database, checks if the committee is accessible by the current user
     * <p>
     * then fetches all the members associated with the committee and sends both of them as response
     */


    @GetMapping("/getCommitteeOverview")
    public ResponseEntity<Response> getCommitteeOverview(@RequestParam(required = true) int committeeId, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);
        CommitteeOverviewDto overview = committeeService.getCommitteeOverview(committee, authentication.getName());

        return ResponseEntity.ok(new Response(ResponseMessages.COMMITTEE_OVERVIEW_RETRIEVED_SUCCESSFULLY, overview));
    }

    @GetMapping("/getAllMembersOfCommittee")
    public ResponseEntity<Response> getAllMembersOfCommittee(@RequestParam(required = true) int committeeId, Authentication authentication) {
        Committee committee = committeeService.findCommitteeById(committeeId);

        List<MemberOfCommitteeDto> membersOfCommittee = committeeService.getMembersOfCommittee(committee, authentication.getName());

        return ResponseEntity.ok(new Response(ResponseMessages.COMMITTEE_MEMBERS_RETRIEVED_SUCCESSFULLY, membersOfCommittee));
    }

    @GetMapping("/committee-summary")
    public ResponseEntity<Response> getCommitteeSummary(@RequestParam(required=true) int committeeId, Authentication authentication) {
        return ResponseEntity.ok(new Response(committeeService.getCommitteeExtendedSummary(committeeId, authentication.getName())));
    }


//    @Deprecated
//    @PostMapping("/addMembersToCommittee")
//    //NOTE: LinkedHashSet is made LinkedHashSet to preserve order and avoid duplicate memberIds
//    //TODO: Fix (this route should not add more coordinator to the committee)
//    //TODO: Create Tests
//    public ResponseEntity<Response> addMembershipsToCommittee(@RequestParam int committeeId, @RequestBody LinkedHashSet<NewMembershipRequest> newMemberships, Authentication authentication) {
//        Committee committee = committeeService.findCommitteeById(committeeId);
//        committeeService.addMembershipsToCommittee(committee, newMemberships, authentication.getName());
//        return ResponseEntity.ok(new Response(ResponseMessages.COMMITTEE_MEMBER_ADDITION_SUCCESS));
//    }


//    @Deprecated
//    @DeleteMapping("/removeCommitteeMembership")
//    //TODO: Create Tests
//    public ResponseEntity<Response> removeCommitteeMembership(@RequestParam int committeeId, @RequestParam int memberId, Authentication authentication) {
//        Committee committee = committeeService.findCommitteeById(committeeId);
//        Member member = memberService.findMemberById(memberId);
//        committeeService.removeCommitteeMembership(committee, member, authentication.getName());
//        return ResponseEntity.ok(new Response(ResponseMessages.MEMBERSHIP_REMOVED_SUCCESSFULLY));
//    }
}
