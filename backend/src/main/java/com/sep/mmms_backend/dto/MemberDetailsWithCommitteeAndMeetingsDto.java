package com.sep.mmms_backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sep.mmms_backend.entity.Member;
import lombok.Getter;

import java.util.List;



/*

//This DTO represents the data of the following format:

{
  "memberId": 101,
  "firstName": "Jane",
  "lastName": "Doe",
  "institution": "University of Science",
  "post": "Senior Researcher",
  "qualification": "PhD",
  "email": "jane.doe@example.com",
  "committees": [
    {
      "committee": {
        "id": 20,
        "name": "Ethics Committee",
        "description": "Reviews research proposals for ethical considerations.",
        "role": "Chairperson"
      },
      "meetings": [
        {
          "id": 501,
          "meetingTitle": "Q1 Review",
          "meetingDescription": "Review of first-quarter proposals.",
          "attended": true
        },
        {
          "id": 502,
          "meetingTitle": "Q2 Review",
          "meetingDescription": "Review of second-quarter proposals.",
          "attended": false
        }
      ]
    },
    {
      "committee": {
        "id": 22,
        "name": "Advisory Board",
        "description": "Provides strategic guidance.",
        "role": "Member"
      },
      "meetings": [
        {
          "id": 601,
          "meetingTitle": "Annual Strategy Session",
          "meetingDescription": "Planning for the next fiscal year.",
          "attended": true
        }
      ]
    }
  ]
}
 */

@Deprecated
@Getter
public class MemberDetailsWithCommitteeAndMeetingsDto {
    private final int memberId;
    private final String firstName;
    private final String lastName;
    private final String post;
    private final List<CommitteeWithMeetings> committeeWithMeetings;


    public record CommitteeInfo(int id, String committeeName, String committeeDescription, String role) {}

    public record MeetingInfo(int id, String meetingTitle, String meetingDescription, boolean hasAttendedMeeting) {}

    /**
     * This object contains all the committee and the meetings belonging to that committee
     */
    public record CommitteeWithMeetings(CommitteeInfo committeeInfo, List<MeetingInfo> meetingInfos) {}

    public MemberDetailsWithCommitteeAndMeetingsDto(Member member, List<CommitteeWithMeetings> committeeWithMeetings) {
        this.memberId = member.getId();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.post = member.getPost();
        this.committeeWithMeetings = List.copyOf(committeeWithMeetings);
    }



    //this constructor is for testing purposes only
    //with this constructor, ObjectMapper can reconstruct a MemberDetailsDto from json
    //used in MemberControllerTests
    @JsonCreator
    public MemberDetailsWithCommitteeAndMeetingsDto(
            @JsonProperty("memberId") int memberId,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
//            @JsonProperty("firstNameNepali") String firstNameNepali,
//            @JsonProperty("lastNameNepali") String lastNameNepali,
            @JsonProperty("institution") String institution,
            @JsonProperty("post") String post,
            @JsonProperty("qualification") String qualification,
            @JsonProperty("committeeWithMeetings") List<CommitteeWithMeetings> committeeWithMeetings,
            @JsonProperty("email") String email
    ) {
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.post = post;
//        this.qualification = qualification;
        this.committeeWithMeetings = committeeWithMeetings;
//        this.firstNameNepali = firstNameNepali;
//        this.lastNameNepali = lastNameNepali;
    }
}
