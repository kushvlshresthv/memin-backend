package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.*;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.repository.CommitteeMembershipRepository;
import com.sep.mmms_backend.repository.CommitteeRepository;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;
    private final EntityValidator entityValidator;
    private final AppUserService appUserService;
    private final MemberRepository memberRepository;
    private final CommitteeMembershipRepository committeeMembershipRepository;

    public CommitteeService(CommitteeRepository committeeRepository, AppUserService appUserService, EntityValidator entityValidator, MemberRepository memberRepository, CommitteeMembershipRepository committeeMembershipRepository) {
        this.committeeRepository = committeeRepository;
        this.appUserService = appUserService;
        this.entityValidator = entityValidator;
        this.memberRepository = memberRepository;
        this.committeeMembershipRepository = committeeMembershipRepository;
    }


    private Committee prepareCommitteeFromCommitteeCreationDto(CommitteeCreationDto committeeCreationDto, String username) {
        entityValidator.validate(committeeCreationDto);

        // Convert to LinkedHashMap to preserve order of memberIds
        Map<Integer, String> memberIdAndRoleMap = committeeCreationDto.getMembers().stream()
                .collect(Collectors.toMap(
                        MemberIdWithRoleDto::getMemberId, // key mapper
                        MemberIdWithRoleDto::getRole,     // value mapper
                        (oldValue, newValue) -> oldValue, // merge function (if duplicate keys)
                        LinkedHashMap::new                 // supplier: use LinkedHashMap
                ));


        //remove the coordinator id from the member ids if present
        memberIdAndRoleMap.keySet().removeIf(memberId -> memberId.equals(committeeCreationDto.getCoordinatorId()));

        //check if all the members have role
        if (!memberIdAndRoleMap.values().stream().allMatch(Objects::nonNull)) {
            throw new InvalidMembershipException(ExceptionMessages.MEMBERSHIP_ROLE_MISSING);
        }

        List<Integer> requestedMemberIds = memberIdAndRoleMap.keySet().stream().toList();

        List<Member> foundMembers = memberRepository.findAccessibleMembersByIds(requestedMemberIds, username);


        memberRepository.validateWhetherAllMembersAreFound(requestedMemberIds, foundMembers);

        //sorting the foundMembers in the same order as the requestedMemberIds
        foundMembers.sort(Comparator.comparingInt(member -> requestedMemberIds.indexOf(member.getId())));


        Committee committee = new Committee();
        committee.setCreatedBy(username);

        committee.setName(committeeCreationDto.getName());
        committee.setDescription(committeeCreationDto.getDescription());
        committee.setStatus(committeeCreationDto.getStatus());
        committee.setMinuteLanguage(committeeCreationDto.getMinuteLanguage());

        if (committeeCreationDto.getMaximumNumberOfMeetings() != null)
            committee.setMaxNoOfMeetings(committeeCreationDto.getMaximumNumberOfMeetings());

        //we also set the order for the members in the same sequence as the orderedFoundMembers
        foundMembers.forEach(member -> {
            CommitteeMembership membership = new CommitteeMembership();
            membership.setMember(member);
            membership.setRole(memberIdAndRoleMap.get(member.getId()));
            membership.setOrder(committee.getMemberships().size());
            committee.addMembership(membership);
        });

        Optional<Member> coordinatorOptional = memberRepository.findMemberByIdNoException(committeeCreationDto.getCoordinatorId());

        if (coordinatorOptional.isEmpty()) {
            throw new CoordinatorDoesNotExistException(ExceptionMessages.COORDINATOR_DOES_NOT_EXIST);
        }
        committee.setCoordinator(coordinatorOptional.get());
        return committee;
    }


    @Transactional
    public Committee saveNewCommittee(CommitteeCreationDto committeeCreationDto, String username) {
        Committee committee = prepareCommitteeFromCommitteeCreationDto(committeeCreationDto, username);
        return committeeRepository.save(committee);
    }


    //TODO: Create Tests
    @Transactional
    public Committee updateExistingCommittee(CommitteeCreationDto committeeCreationDto, int committeeId, String username) {
        Committee existingCommittee = this.findCommitteeById(committeeId);

        if (!existingCommittee.getCreatedBy().equals(username)) {
            throw new CommitteeNotAccessibleException(ExceptionMessages.COMMITTEE_NOT_ACCESSIBLE);
        }

        Committee committee = prepareCommitteeFromCommitteeCreationDto(committeeCreationDto, username);

        existingCommittee.setName(committee.getName());
        existingCommittee.setDescription(committee.getDescription());
        existingCommittee.setStatus(committee.getStatus());
        existingCommittee.setMinuteLanguage(committee.getMinuteLanguage());
        existingCommittee.setMaxNoOfMeetings(committee.getMaxNoOfMeetings());
        existingCommittee.setCoordinator(committee.getCoordinator());

        List<CommitteeMembership> newMemberships = committee.getMemberships();

        List<CommitteeMembership> currentMemberships = existingCommittee.getMemberships();

        // Remove memberships that are NOT in the new list
        currentMemberships.removeIf(existing ->
                newMemberships.stream().noneMatch(newMem ->
                        newMem.getMember().getId() == existing.getMember().getId()
                )
        );

        //the newMemberships are already sorted, so assign the order for the membership in the same sequence as the newMemberships

        int order = 1;

        // Add new ones OR Update existing ones
        for (CommitteeMembership newMem : newMemberships) {

            // Check if this member is already in the current list
            CommitteeMembership existingMem = currentMemberships.stream()
                    .filter(e -> e.getMember().getId() == newMem.getMember().getId())
                    .findFirst()
                    .orElse(null);

            if (existingMem == null) {
                // CASE: It's a new member -> Add it
                existingCommittee.addMembership(newMem);
                newMem.setOrder(order++);

            } else {
                // CASE: Member exists -> Update metadata (e.g., Role)
                // Do NOT replace the object, just update the fields
                existingMem.setRole(newMem.getRole());
                existingMem.setOrder(order++);
            }
        }

        return committeeRepository.save(existingCommittee);
    }

    @Transactional
    @CheckCommitteeAccess
    public CommitteeOverviewDto getCommitteeOverview(Committee committee, String username) {
        CommitteeOverviewDto committeeOverview = new CommitteeOverviewDto();
        committeeOverview.setName(committee.getName());
        committeeOverview.setDescription(committee.getDescription());
        committeeOverview.setCreatedDate(committee.getCreatedDate());
        committeeOverview.setMemberCount(committee.getMemberships().size());
        committeeOverview.setMeetingCount(committee.getMeetings().size());
        committeeOverview.setLanguage(committee.getMinuteLanguage());
        int decisionCount = 0;
        for (Meeting meeting : committee.getMeetings()) {
            decisionCount = +meeting.getDecisions().size();
        }
        committeeOverview.setDecisionCount(decisionCount);

        committeeOverview.setCoordinatorName(committee.getCoordinator().getFirstName() + " " + committee.getCoordinator().getLastName());

        if (!committee.getMeetings().isEmpty()) {
            List<LocalDate> meetingDates = committee.getMeetings().stream().map(Meeting::getHeldDate).collect(Collectors.toList());

            Comparator<LocalDate> comparator = LocalDate::compareTo;
            meetingDates.sort(comparator);

            committeeOverview.setFirstMeetingDate(meetingDates.getFirst());
            committeeOverview.setLastMeetingDate(meetingDates.getLast());
            committeeOverview.setMeetingDates(meetingDates);
        }
        return committeeOverview;
    }


    public List<Committee> getAllActiveCommittees(String username) {
        List<Committee> activeCommittees = committeeRepository.getAllActiveCommittees(username);
        return activeCommittees;
    }

    public List<Committee> getAllInactiveCommittees(String username) {
        List<Committee> activeCommittees = committeeRepository.getAllInActiveCommittees(username);
        return activeCommittees;
    }


    @Transactional
    @CheckCommitteeAccess
    public List<MemberOfCommitteeDto> getMembersOfCommittee(Committee committee, String username) {

        List<MemberOfCommitteeDto> membersOfCommittee = committee.getSortedMemberships().stream().map(membership -> {
            Member member = membership.getMember();
            String memberFullName = member.getFirstName() + " " + member.getLastName();
            return new MemberOfCommitteeDto(member.getId(), memberFullName, membership.getRole());
        }).toList();

        return membersOfCommittee;
    }


//    @Transactional
//    @CheckCommitteeAccess
//    @Deprecated
//    public void deleteCommittee(Committee committeeToBeDeleted, String username) {
//        committeeRepository.delete(committeeToBeDeleted);
//    }


    public Optional<Committee> findCommitteeByIdNoException(int committeeId) {
        return committeeRepository.findById(committeeId);
    }

    public Committee getCommitteeIfAccessible(int committeeId, String username) {
        Committee committee = this.findCommitteeByIdNoException(committeeId).orElseThrow(() -> new CommitteeDoesNotExistException(ExceptionMessages.COMMITTEE_DOES_NOT_EXIST, committeeId));
        if (!committee.getCreatedBy().equals(username)) {
            //TODO: handle error properly
            throw new IllegalOperationException("Committee not accessible");
        }
        return committee;
    }

    /**
     * returns all the members belonging to the committee
     */

    public List<MemberSummaryDto> getMembersOfCommittee(Committee committee) {
        List<Integer> memberIds = committee.getMemberships().stream()
                .map(membership -> membership.getMember().getId())
                .collect(Collectors.toList());

        List<Member> members = memberRepository.findAllById(memberIds);

        return members.stream().map(member -> new MemberSummaryDto(member, committee.getId()))
                .collect(Collectors.toList());
    }


    /**
     * returns both Committee and Members associated with the committee
     * <br>
     * NOTE: membership is populated in the Members object, not in the committee object
     */


    public boolean existsById(int committeeId) {
        return committeeRepository.existsById(committeeId);
    }


    /**
     * BUG FIX: This method won't work IF we try to populate the 'committee' with the new 'memberships' because  when trying to save memberships from cascading, JPA will decide whether the membership is either new or not by checking whether membership's primary key value is null or not(which it isn't.
     * <br><br>
     * Since, not null, JPA will try to merge(), but since the row is not present in the database, it will throw EntityNotExistException.
     * <br><br>
     * Here, since we are using CommitteeMembershipRepository, JPA will decide whether the membership is either new or not by calling isNew() method since CommmiteeMembership implements Presistable interface
     */
//    //TODO: Create Tests
//    @CheckCommitteeAccess
//    @Transactional
//    @Deprecated
//    public void addMembershipsToCommittee(Committee committee, LinkedHashSet<NewMembershipRequest> newMembershipRequests, String username) {
//        List<Integer> newMemberIds = newMembershipRequests.stream().map(NewMembershipRequest::memberId).collect(Collectors.toList());
//
//        List<Member> validNewMembers = memberRepository.findAccessibleMembersByIds(newMemberIds, username);
//        memberRepository.validateWhetherAllMembersAreFound(newMemberIds, validNewMembers);
//
//        //check if the membership for the member and committee already exists
//        List<CommitteeMembership> alreadyExistingMemberships = committeeMembershipRepository.findExistingMemberships(newMemberIds, committee.getId());
//        if (!alreadyExistingMemberships.isEmpty()) {
//            Map<Integer, String> memberIdAndRole = alreadyExistingMemberships.stream().collect(Collectors.toMap(membership -> membership.getMember().getId(), CommitteeMembership::getRole));
//            throw new MembershipAlreadyExistsException(ExceptionMessages.MEMBERSHIP_ALREADY_EXISTS, memberIdAndRole);
//        }
//
//
//        Map<Integer, String> rolesMap = newMembershipRequests.stream().collect(Collectors.toMap(NewMembershipRequest::memberId, NewMembershipRequest::role));
//        List<CommitteeMembership> newMemberships = new ArrayList<>();
//
//        for (Member member : validNewMembers) {
//            CommitteeMembership newMembership = new CommitteeMembership();
//            newMembership.setCommittee(committee);
//            newMembership.setMember(member);
//            newMembership.setRole(rolesMap.get(member.getId()));
//            newMemberships.add(newMembership);
//        }
//
//        committeeMembershipRepository.saveAll(newMemberships);
//    }
    public Committee findCommitteeById(int committeeId) {
        return committeeRepository.findCommitteeById(committeeId);
    }

//    //TODO: Check this implementation properly as this is not checked carefully
//    @CheckCommitteeAccess
//    @Deprecated
//    public void removeCommitteeMembership(Committee committee, Member member, String username) {
//        if (!member.getCreatedBy().equals(username)) {
//            throw new IllegalOperationException("Membership not accessible");
//            //TODO: Error (handle this properly by creating a custom exception)
//        }
//
//        CommitteeMembership membershipToBeDeleted = committeeMembershipRepository.findMembershipBetweenCommitteeAndMember(committee.getId(), member.getId()).orElseThrow(() -> new MembershipDoesNotExistException(ExceptionMessages.MEMBERSHIP_DOES_NOT_EXIST));
//
//        if (membershipToBeDeleted.getRole().equalsIgnoreCase("coordinator"))
//            throw new IllegalOperationException("Coordinator can't be removed from the committee");
//        //TODO: Error (handle this properly)
//
//        committeeMembershipRepository.delete(membershipToBeDeleted);
//    }
}
