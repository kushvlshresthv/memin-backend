package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.*;
import com.sep.mmms_backend.entity.*;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.CommitteeMembershipRepository;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;
    private final EntityValidator entityValidator;
    private final AppUserService appUserService;
    private final MemberRepository memberRepository;
    private final CommitteeMembershipRepository committeeMembershipRepository;

    public CommitteeService(CommitteeRepository committeeRepository,  AppUserService appUserService, EntityValidator entityValidator, MemberRepository memberRepository, CommitteeMembershipRepository committeeMembershipRepository)  {
       this.committeeRepository = committeeRepository;
       this.appUserService = appUserService;
       this.entityValidator = entityValidator;
       this.memberRepository = memberRepository;
       this.committeeMembershipRepository = committeeMembershipRepository;
    }

    /**
     *
     * @param committeeCreationDto committee that is to be persisted
     * @param username username that creates the committee
     *
     */
    //TODO: Create Tests
    @Transactional
    public Committee saveNewCommittee(CommitteeCreationDto committeeCreationDto, String username) {
        entityValidator.validate(committeeCreationDto);

        if(!committeeCreationDto.getMembers().values().stream().allMatch(Objects::nonNull)) {
            throw new InvalidMembershipException(ExceptionMessages.MEMBERSHIP_ROLE_MISSING);
        }

        //adding coordinator to members as well
        committeeCreationDto.getMembers().put(committeeCreationDto.getCoordinatorId(), "Coordinator");

        List<Integer> requestedMemberIds = committeeCreationDto.getMembers().keySet().stream().toList();

        List<Member> foundMembers = memberRepository.findAccessibleMembersByIds(requestedMemberIds, username);
        memberRepository.validateWhetherAllMembersAreFound(requestedMemberIds, foundMembers);

        Committee committee = new Committee();
        committee.setCreatedBy(appUserService.loadUserByUsername(username));
        committee.setName(committeeCreationDto.getName());
        committee.setDescription(committeeCreationDto.getDescription());
        committee.setStatus(committeeCreationDto.getStatus());

        if(committeeCreationDto.getMaximumNumberOfMeetings() != null)
            committee.setMaxNoOfMeetings(committeeCreationDto.getMaximumNumberOfMeetings());

        foundMembers.forEach(member-> {
            CommitteeMembership membership = new CommitteeMembership();
            membership.setMember(member);
            membership.setRole(committeeCreationDto.getMembers().get(member.getId()));
            committee.addMembership(membership);
        });

        return committeeRepository.save(committee);
    }



    //TODO: Create Tests
    @Transactional
    @CheckCommitteeAccess
    public Committee updateExistingCommittee(CommitteeUpdationDto newCommitteeData, Committee existingCommittee, String username) {
        if(newCommitteeData.getName() != null && !newCommitteeData.getName().isBlank())
            existingCommittee.setName(newCommitteeData.getName());
        if(newCommitteeData.getDescription() != null && !newCommitteeData.getDescription().isBlank())
            existingCommittee.setDescription(newCommitteeData.getDescription());
        if(newCommitteeData.getStatus() != null)
            existingCommittee.setStatus(newCommitteeData.getStatus());
        if(newCommitteeData.getMaximumNumberOfMeetings() != null)
            existingCommittee.setMaxNoOfMeetings(newCommitteeData.getMaximumNumberOfMeetings());

        return committeeRepository.save(existingCommittee);
    }

    @Transactional
    @CheckCommitteeAccess
    public void deleteCommittee(Committee committeeToBeDeleted, String username) {
        committeeRepository.delete(committeeToBeDeleted);
    }


    public Optional<Committee> findCommitteeByIdNoException(int committeeId) {
        return committeeRepository.findById(committeeId);
    }

    /**
     * returns all the members belonging to the committee
     */

    public List<MemberSummaryDto> getMembersOfCommittee(Committee committee) {
        List<Integer> memberIds = committee.getMemberships().stream()
                .map(membership->membership.getMember().getId())
                .collect(Collectors.toList());

        List<Member> members = memberRepository.findAllById(memberIds);

        return members.stream().map(member-> new MemberSummaryDto(member, committee.getId()))
                .collect(Collectors.toList());
    }



    /**
     * returns both Committee and Members associated with the committee
     * <br>
     * NOTE: membership is populated in the Members object, not in the committee object
     */

    //TODO: Create Tests
    @CheckCommitteeAccess
    public CommitteeDetailsDto getCommitteeDetails(Committee committee, String username) {
        return new CommitteeDetailsDto(committee);
    }

    //TODO: This method also loads all the meetings associated with a committee which isn't required(probably lazy loaded though) or maybe write a custom query
    //TODO: Create Tests
    public List<Committee> getCommittees(String username) {
        AppUser currentUser = appUserService.loadUserByUsername(username);
        return currentUser.getMyCommittees();
    }


    public boolean existsById(int committeeId) {
        return committeeRepository.existsById(committeeId);
    }


    /**
        BUG FIX: This method won't work IF we try to populate the 'committee' with the new 'memberships' because  when trying to save memberships from cascading, JPA will decide whether the membership is either new or not by checking whether membership's primary key value is null or not(which it isn't.
        <br><br>
        Since, not null, JPA will try to merge(), but since the row is not present in the database, it will throw EntityNotExistException.
         <br><br>
        Here, since we are using CommitteeMembershipRepository, JPA will decide whether the membership is either new or not by calling isNew() method since CommmiteeMembership implements Presistable interface
     */
    //TODO: Create Tests
    @CheckCommitteeAccess
    @Transactional
    public void addMembershipsToCommittee(Committee committee, LinkedHashSet<NewMembershipRequest> newMembershipRequests, String username) {
        List<Integer> newMemberIds = newMembershipRequests.stream().map(NewMembershipRequest::memberId).collect(Collectors.toList());

        List<Member> validNewMembers = memberRepository.findAccessibleMembersByIds(newMemberIds, username);
        memberRepository.validateWhetherAllMembersAreFound(newMemberIds, validNewMembers);

        //check if the membership for the member and committee already exists
        List<CommitteeMembership> alreadyExistingMemberships = committeeMembershipRepository.findExistingMemberships(newMemberIds, committee.getId());
        if(!alreadyExistingMemberships.isEmpty()) {
            Map<Integer, String> memberIdAndRole = alreadyExistingMemberships.stream().collect(Collectors.toMap(membership->membership.getMember().getId(), CommitteeMembership::getRole));
            throw new MembershipAlreadyExistsException(ExceptionMessages.MEMBERSHIP_ALREADY_EXISTS, memberIdAndRole);
        }


        Map<Integer, String> rolesMap = newMembershipRequests.stream().collect(Collectors.toMap(NewMembershipRequest::memberId, NewMembershipRequest::role));
        List<CommitteeMembership> newMemberships = new ArrayList<>();

        for(Member member: validNewMembers) {
            CommitteeMembership newMembership = new CommitteeMembership();
            newMembership.setCommittee(committee);
            newMembership.setMember(member);
            newMembership.setRole(rolesMap.get(member.getId()));
            newMemberships.add(newMembership);
        }

        committeeMembershipRepository.saveAll(newMemberships);
    }

    public Committee findCommitteeById(int committeeId) {
        return committeeRepository.findCommitteeById(committeeId);
    }

    //TODO: Check this implementation properly as this is not checked carefully
    @CheckCommitteeAccess
    public void removeCommitteeMembership(Committee committee, Member member, String username) {
        if(!member.getCreatedBy().equals(username)) {
            throw new IllegalOperationException("Membership not accessible");
            //TODO: Error (handle this properly by creating a custom exception)
        }

       CommitteeMembership membershipToBeDeleted =  committeeMembershipRepository.findMembershipBetweenCommitteeAndMember(committee.getId(), member.getId()).orElseThrow(()-> new MembershipDoesNotExistException(ExceptionMessages.MEMBERSHIP_DOES_NOT_EXIST));

        if(membershipToBeDeleted.getRole().equalsIgnoreCase("coordinator"))
            throw new IllegalOperationException("Coordinator can't be removed from the committee");
        //TODO: Error (handle this properly)

        committeeMembershipRepository.delete(membershipToBeDeleted);
    }
}

