package com.sep.mmms_backend.controller;

import com.sep.mmms_backend.dto.*;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.repository.CommitteeRepository;
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
public class MemberController {
    private final MemberService memberService;
    private final CommitteeService committeeService;

    public MemberController(MemberService memberService, CommitteeService committeeService){
        this.memberService = memberService;
        this.committeeService = committeeService;
    }

//    @Deprecated
//    //TODO: Create Tests
//    @GetMapping("/searchMembersByName")
//    public ResponseEntity<Response> getMembersByName(@RequestParam(required=true) String name) {
//        List<Member> fetchedMembers = memberService.searchMemberByName(name);
//        List<MemberSearchResultDto> memberSearchResultDtos = new ArrayList<>();
//        fetchedMembers.forEach(member-> memberSearchResultDtos.add(new MemberSearchResultDto(member)));
//        return ResponseEntity.ok(new Response("Results: ", memberSearchResultDtos));
//    }



    @PostMapping("/createMember")
    public ResponseEntity<Response> createNewMember(@RequestBody(required=true) MemberCreationDto memberDto , Authentication authentication ) {
       Member member = memberService.saveNewMember(memberDto, authentication.getName());
       return ResponseEntity.ok(new Response(ResponseMessages.MEMBER_CREATION_SUCCESS, member));
    }

    @PatchMapping("/updateMember")
    public ResponseEntity<Response> updateMember(@RequestBody(required=true) MemberCreationDto memberCreationDto, @RequestParam Integer memberId, Authentication authentication) {
        memberService.updateMember(memberId, memberCreationDto, authentication.getName());
        return ResponseEntity.ok(new Response(ResponseMessages.MEMBER_UPDATION_SUCCESS));
    }

    @GetMapping("/getPossibleInvitees")
    public ResponseEntity<Response> getPossibleInvitees(@RequestParam(required=true) int committeeId, Authentication authentication) {

        Committee committee = committeeService.findCommitteeById(committeeId);
        List<MemberSearchResultDto> possibleInvitees = memberService.getPossibleInvitees(committee, authentication.getName());

        return ResponseEntity.ok(new Response("Possible Invitees: ", possibleInvitees));
    }




    //This route is simular to 'createMember' but does not create a membership for the member

//    @PostMapping("/createInvitee")
//    public ResponseEntity<Response> createInvitee(@RequestBody(required=true) MemberCreationDtoDeprecated memberDto) {
//        Member member = memberService.saveNewInvitee(memberDto);
//        MemberSearchResultDto searchResultDto = new MemberSearchResultDto(member);
//        //returning search result dto, because frontend adds this newly created member below the search bar as search result
//        return ResponseEntity.ok(new Response(ResponseMessages.MEMBER_CREATION_SUCCESS, searchResultDto));
//    }


//    @Deprecated
//    @PostMapping("/updateMemberDetails")
//    public ResponseEntity<Response> updateMemberDetails(@RequestBody(required=true) MemberUpdationDto memberUpdationDto, Authentication authentication) {
//        Member updatedMember = memberService.updateExistingMemberDetails(memberUpdationDto, authentication.getName());
//
//        MemberWithoutCommitteeDto updatedMemberWithoutCommitteeDto = new MemberWithoutCommitteeDto(updatedMember);
//
//        return ResponseEntity.ok(new Response(ResponseMessages.MEMBER_UPDATION_SUCCESS, updatedMemberWithoutCommitteeDto));
//    }

    /** <h2>IMPORTANT NOTE</h2>
     * whether a particular member is acceesbile by a particular user is checked by com  aring username
       <br> <br>
     * if the username is made changeable in the future, this has to be updated as well
       <br> <br>
     * the resason the 'created_by' section was not choosen to be 'id' is because, to retreive the 'id' of the current user, a database operation is required, which flushes the context to save the entity before the 'created_by' section is populated in the entity causing an error
     */

//    @Deprecated
//    @GetMapping("/getMemberDetails")
//    public ResponseEntity<Response> getMemberDetails(@RequestParam(required=true) int memberId, Authentication authentication) {
//        MemberDetailsDto memberDetailsDto = memberService.getMemberDetails(memberId, authentication.getName());
//        return ResponseEntity.ok(new Response(ResponseMessages.MEMBER_DETAIL_RETRIEVED_SUCCESSFULLY, memberDetailsDto));
//    }


    //TODO: Create Tests
    @GetMapping("/getAllMembers")
    public ResponseEntity<Response> getAllMembers(Authentication    authentication) {
        List<Member> members =  memberService.getAllMembers(authentication.getName());
       List<MemberDetailsDto> allMembers = new ArrayList<>();
       members.forEach(member-> allMembers.add(new MemberDetailsDto(member)));
       return ResponseEntity.ok(new Response("Found Members: ", allMembers));
    }
}
