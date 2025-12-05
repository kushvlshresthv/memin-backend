package com.sep.mmms_backend.service;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.dto.MemberCreationDto;
import com.sep.mmms_backend.dto.MemberSearchResultDto;
import com.sep.mmms_backend.dto.MemberWithoutCommitteeDto;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.CommitteeMembership;
import com.sep.mmms_backend.entity.Member;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import com.sep.mmms_backend.exceptions.MemberDoesNotExistException;
import com.sep.mmms_backend.repository.MemberRepository;
import com.sep.mmms_backend.validators.EntityValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final EntityValidator entityValidator;

    public MemberService(MemberRepository memberRepository, EntityValidator entityValidator) {
        this.memberRepository = memberRepository;
        this.entityValidator = entityValidator;
    }

//    @Deprecated
//    /**
//     * Searches members based on a keyword.
//     * - If the keyword is one word, it searches both first and last names.
//     * - If the keyword is two or more words, it uses the first two words to search
//     * for a match in the first and last name fields.
//     */
//    //TODO: Create Tests
//    //TODO: Optimization This fetches all data of members, but only memberId, firstName, lastName, and post is required
//    public List<Member> searchMemberByName(String keyword) {
//        if (!StringUtils.hasText(keyword)) {
//            return Collections.emptyList();
//        }
//
//        String[] parts = keyword.trim().split("\\s+");
//
//        if (parts.length == 1) {
//            return memberRepository.findByFirstNameOrLastName(parts[0]);
//        } else {
//            return memberRepository.findByFullName(parts[0], parts[1]);
//        }
//    }


    //NEW IMPLEMENTATION
    @Transactional
    public Member saveNewMember(MemberCreationDto memberDto, String username) {

        entityValidator.validate(memberDto);

        Member member = new Member();
        member.setFirstName(memberDto.getFirstName());
        member.setLastName(memberDto.getLastName());
        member.setTitle(memberDto.getTitle());
        if (member.getPost() != null)
            member.setPost(memberDto.getPost());
        return memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(int memberId, MemberCreationDto memberCreationDto, String username) {
        entityValidator.validate(memberCreationDto);
        Member member = getMemberIfAccesssible(memberId, username);

        member.setFirstName(memberCreationDto.getFirstName());
        member.setLastName(memberCreationDto.getLastName());
        member.setTitle(memberCreationDto.getTitle());
        member.setPost(memberCreationDto.getPost());
        return memberRepository.save(member);
    }

    private Member getMemberIfAccesssible( int memberId, String username) {
        Optional<Member> optionalMember = memberRepository.getMemberIfAccessible(memberId, username);

        if(optionalMember.isEmpty()) {
            throw new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId);
        }

        return optionalMember.get();
    }

    //NEW IMPLEMENTATION
    @Transactional
    @CheckCommitteeAccess
    public List<MemberSearchResultDto> getPossibleInvitees(Committee committee, String username) {
        List<Member> committeeMembers = committee.getMemberships().stream().map(CommitteeMembership::getMember).toList();

        List<Member> allAccessibleMembers = memberRepository.findAccessibleMembers(username);

        allAccessibleMembers.removeAll(committeeMembers);
        allAccessibleMembers.remove(committee.getCoordinator());

        List<MemberSearchResultDto> possibleInvitees = allAccessibleMembers.stream().map(MemberSearchResultDto::new).toList();

        return possibleInvitees;
    }


    public boolean existsById(int memberId) {
        return memberRepository.existsById(memberId);
    }


    public Member findById(int memberId) {
        return memberRepository.findById(memberId).orElseThrow(() ->
                new MemberDoesNotExistException(ExceptionMessages.MEMBER_DOES_NOT_EXIST, memberId));
    }

    /**
     * @param member:      shouldn't be null
     * @param committeeId: committeeId for which the member's role is required
     * @return returns role of the user in the committee if found, else returns null
     * <p>
     * NOTE: this method iterates through all the memberships for a particular member to get the membership associated with a particular committeeId
     */
    @Deprecated
    public CommitteeMembership getMembership(Member member, int committeeId) {
        List<CommitteeMembership> memberships = member.getMemberships();
        for (CommitteeMembership membership : memberships) {
            if (membership.getId().getCommitteeId() == committeeId) {
                return membership;
            }
        }
        return null;
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
