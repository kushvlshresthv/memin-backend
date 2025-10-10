package com.sep.mmms_backend.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="members_new")
@EntityListeners(AuditingEntityListener.class)
@Builder
@Entity

//TODO: since validations are handled by CreationDto objects, consider removing teh validations from here later after confirming no entities are validated in our code.
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="member_id")
    private Integer id;

    @Column(name = "member_uuid", nullable = false, unique = true, updatable = false)
    private String uuid;


    @Column(name="member_first_name", nullable=false)
    private String firstName;

    @Column(name="member_last_name", nullable=false)
    private String lastName;

    @Column(name="member_username", nullable = false)
    private String username;

    @Column(name="member_institution")
    private String institution;  //example: Pulchowk Campus, IOE

    @Column(name="member_post")
    private String post; //example: professor

    @Column(name="member_title", nullable = false)
    private String title;

    //consider removing this field, as it is redundant to 'post'
    @Deprecated
    private String qualification; //example: Dr, Prof, Mr


    @Column(name="member_email", nullable = false)
    private String email;

    @Column(name = "member_created_by", updatable = false, nullable = false)
    @CreatedBy
    private String createdBy;

    @Column(name = "member_created_date", updatable = false, nullable = false)
    @CreatedDate
    private LocalDate createdDate;

    @Column(name = "member_modified_by",  nullable = false)
    @CreatedBy
    private String modifiedBy;

    @Column(name = "member_modified_date", nullable = false)
    @CreatedDate
    private LocalDate modifiedDate;

    @OneToMany(mappedBy="member", cascade=CascadeType.PERSIST)
    private List<CommitteeMembership> memberships = new LinkedList<>();

    @Deprecated
    @ManyToMany(mappedBy = "attendees", fetch = FetchType.LAZY)
    Set<Meeting> attendedMeetings = new HashSet<>();


    @ManyToMany(mappedBy = "invitees", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    Set<Meeting> invitedMeetings = new HashSet<>();

    @PrePersist
    public void initUUID() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Member that = (Member) o;
        return Objects.equals(uuid, that.uuid);
    }

    public void addMembership(CommitteeMembership membership) {
        this.memberships.add(membership);
        membership.setMember(this);
    }
}