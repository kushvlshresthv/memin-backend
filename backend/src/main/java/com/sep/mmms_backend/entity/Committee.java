package com.sep.mmms_backend.entity;

import com.sep.mmms_backend.enums.CommitteeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="committees")
@EntityListeners(AuditingEntityListener.class)
public class Committee {
    @Id
    @Column(name="committee_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * A universally unique identifier (UUID) that serves as the business key.
     * This is the field used for equals() and hashCode().
     */
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

    @Column(name="committee_name", nullable = false)
    private String name;

    @Column(name="committee_description", nullable = false)
    private String description;

    @Column(name="max_no_of_meetings")
    private Integer maxNoOfMeetings;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private CommitteeStatus status;

    @ManyToOne
    @JoinColumn(name="created_by", referencedColumnName="uid", nullable=false)
    //@CreatedBy is not used because then Audit will inquire the database
    private AppUser createdBy;

    @Column(name = "created_date")
    @CreatedDate
    private LocalDate createdDate;

    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;

    @Column(name = "modified_date")
    @LastModifiedDate
    private LocalDate modifiedDate;

    @OneToMany(mappedBy="committee", cascade = CascadeType.REMOVE)
    private List<Meeting> meetings = new ArrayList<>();

    @OneToMany(mappedBy = "committee", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<CommitteeMembership> memberships = new ArrayList<>();

    /**
        automatically sets membership->committee to 'this' as well
     */
    public void addMembership(CommitteeMembership membership) {
        memberships.add(membership);
        membership.setCommittee(this);
    }

    @PrePersist
    public void prePersist() {
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

        Committee that = (Committee) o;
        return Objects.equals(uuid, that.uuid);
    }
}
