package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.MemberCreationDto;
import com.sep.mmms_backend.dto.MemberDetailsDto;
import com.sep.mmms_backend.dto.MemberUpdationDto;
import com.sep.mmms_backend.dto.MemberWithoutCommitteeDto;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.IllegalOperationException;
import com.sep.mmms_backend.exceptions.InvalidRequestException;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final EntityValidator entityValidator;

    public MemberService(MemberRepository memberRepository,EntityValidator entityValidator)  {
        this.memberRepository = memberRepository;
        this.entityValidator = entityValidator;
    }

    /**
     * Searches members based on a keyword.
     * - If the keyword is one word, it searches both first and last names.
     * - If the keyword is two or more words, it uses the first two words to search
     * for a match in the first and last name fields.
     */
    //TODO: Create Tests
    //TODO: Optimization This fetches all data of members, but only memberId, firstName, lastName, and post is required
    public List<Member> searchMemberByName(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }

        String[] parts = keyword.trim().split("\\s+");

        if (parts.length == 1) {
            return memberRepository.findByFirstNameOrLastName(parts[0]);
        } else {
            return memberRepository.findByFullName(parts[0], parts[1]);
        }
    }

    /**
     *
     * @param memberDto member data to be persisted
     * @param committee committee to which the member is assocated with
     * @param username username that created committee
     * NOTE: This method only establishes a single membership for the new Member ie with the committeeId provided as the second argument.
     * Even if there are other memberships in the Member parameter, they are discarded
     */

    @Transactional
    @CheckCommitteeAccess
    public Member saveNewMember(MemberCreationDto memberDto,Committee committee,String username) {
        entityValidator.validate(memberDto);

        Member member = new Member();
        member.setFirstName(memberDto.getFirstName());
        member.setLastName(memberDto.getLastName());
        member.setPost(memberDto.getPost());
        if(memberDto.getFirstNameNepali() != null && !memberDto.getFirstNameNepali().isEmpty())
            member.setFirstNameNepali(memberDto.getFirstNameNepali());
        if(memberDto.getLastNameNepali() != null && !memberDto.getLastNameNepali().isEmpty())
            member.setLastNameNepali(memberDto.getLastNameNepali());
        if(memberDto.getFirstNameNepali() != null && !memberDto.getFirstNameNepali().isEmpty())
            member.setEmail(memberDto.getEmail());
        if(memberDto.getEmail() != null && !memberDto.getEmail().isEmpty())
            member.setInstitution(memberDto.getInstitution());

        CommitteeMembership membership = new CommitteeMembership();
        membership.setRole(memberDto.getRole());
        membership.setCommittee(committee);

        member.addMembership(membership);

        //persists the membershp as well
        return memberRepository.save(member);
    }



    //similar to saveNewMember, but just does not save the membership with the committee
    public Member saveNewInvitee(MemberCreationDto memberDto){
        //TODO: do something about this workaround
        memberDto.setRole("toPassValidation");
        entityValidator.validate(memberDto);

        Member member = new Member();
        member.setFirstName(memberDto.getFirstName());
        member.setLastName(memberDto.getLastName());
        member.setFirstNameNepali(memberDto.getFirstNameNepali());
        member.setLastNameNepali(memberDto.getLastNameNepali());

        if(memberDto.getEmail() != null && !memberDto.getEmail().isEmpty())
            member.setEmail(memberDto.getEmail());
        member.setInstitution(memberDto.getInstitution());
        member.setPost(memberDto.getPost());


        return memberRepository.save(member);
    }

    @Transactional
    public Member updateExistingMemberDetails(MemberUpdationDto newMemberData, String username) {

        if(newMemberData.getId() == null) {
            throw new IllegalOperationException("TODO:(Exception) handle this exception");
        }

        Member existingMember = memberRepository.findMemberById(newMemberData.getId());
        if(!existingMember.getCreatedBy().equals(username)) {
            throw new IllegalOperationException("TODO: (Exception) handle this exception");
            //maybe create MemberNotAccessible exception
        }

        if(newMemberData.getFirstName() != null && !newMemberData.getFirstName().isBlank())
            existingMember.setFirstName(newMemberData.getFirstName());
        if(newMemberData.getLastName() != null && !newMemberData.getLastName().isBlank())
            existingMember.setLastName(newMemberData.getLastName());
        if(newMemberData.getEmail() != null && !newMemberData.getEmail().isBlank())
            existingMember.setEmail(newMemberData.getEmail());
        if(newMemberData.getPost() != null && !newMemberData.getPost().isBlank())
            existingMember.setPost(newMemberData.getPost());
        if(newMemberData.getFirstNameNepali() != null && !newMemberData.getFirstNameNepali().isBlank())
            existingMember.setFirstNameNepali(newMemberData.getFirstNameNepali());
        if(newMemberData.getLastNameNepali() != null && !newMemberData.getLastNameNepali().isBlank())
            existingMember.setLastNameNepali(newMemberData.getLastNameNepali());
        if(newMemberData.getInstitution() != null && !newMemberData.getInstitution().isBlank())
            existingMember.setInstitution(newMemberData.getInstitution());

        return memberRepository.save(existingMember);
    }


    public boolean existsById(int memberId) {
        return memberRepository.existsById(memberId);
    }


    public Member findById(int memberId) {
        return memberRepository.findById(memberId).orElseThrow(()->
                new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId));
    }

    /**

     * @param member: shouldn't be null
     * @param committeeId: committeeId for which the member's role is required
     * @return returns role of the user in the committee if found, else returns null

     * NOTE: this method iterates through all the memberships for a particular member to get the membership associated with a particular committeeId
     */
    public CommitteeMembership getMembership(Member member, int committeeId) {
        List <CommitteeMembership> memberships = member.getMemberships();
        for(CommitteeMembership membership : memberships) {
            if(membership.getId().getCommitteeId() == committeeId) {
                return membership;
            }
        }
        return null;
    }



    @Transactional(readOnly = true)  //this makes the session/connection stays open for lazy loading, disables auto commmit mode and optimizes for reads
    public MemberDetailsDto getMemberDetails(int memberId, String username) {
        Member member = memberRepository.findMemberById(memberId);
        if(!member.getCreatedBy().equals(username)) {
            throw new IllegalOperationException(ExceptionMessages.MEMBER_NOT_ACCESSIBLE);
        }

        Set<Committee> committees = member.getMemberships().stream().map(CommitteeMembership::getCommittee).collect(Collectors.toSet());


        Set<Meeting> attendedMeetings = member.getAttendedMeetings();
        List<MemberDetailsDto.CommitteeWithMeetings> committeeWithMeetings = new ArrayList<>();

        Map<Integer, String> committeeIdToRoleMap = member.getMemberships().stream().collect(Collectors.toMap(
                membership-> membership.getCommittee().getId(),
                CommitteeMembership::getRole
        ));

        //all committee(with role) the member has joined + all meetings in the committee with information of attended or not
        for(Committee committee : committees) {
            List<Meeting> allMeetingsOfCommittee = committee.getMeetings();

            String role = committeeIdToRoleMap.get(committee.getId());
            MemberDetailsDto.CommitteeInfo committeeInfo = new MemberDetailsDto.CommitteeInfo(committee.getId(), committee.getName(), committee.getDescription(), role);


            List<MemberDetailsDto.MeetingInfo> meetingInfos = new ArrayList<>();
            committee.getMeetings().forEach(meeting-> {

                boolean hasAttendedMeeting = false;
                if(attendedMeetings != null && attendedMeetings.contains(meeting)) {
                    hasAttendedMeeting = true;
                }

                MemberDetailsDto.MeetingInfo meetingInfo = new MemberDetailsDto.MeetingInfo(meeting.getId(), meeting.getTitle(), meeting.getDescription(),hasAttendedMeeting );

                meetingInfos.add(meetingInfo);
            });

            committeeWithMeetings.add(new MemberDetailsDto.CommitteeWithMeetings(committeeInfo, meetingInfos));
        }
        return new MemberDetailsDto(member, committeeWithMeetings);
    }


    public List<Member> getAllMembers(String username) {
        List<MemberWithoutCommitteeDto> allMembersDto = new ArrayList<>();
        List<Member> allMembers = memberRepository.findAllMembersByCreatedBy(username);
        return allMembers;
    }

    public Member findMemberById(int memberId) {
        return memberRepository.findMemberById(memberId);
    }
}
