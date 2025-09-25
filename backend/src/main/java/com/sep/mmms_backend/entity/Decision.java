package com.sep.mmms_backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "decisions")
public class Decision {
    @Id
    @Column(name="decision_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer decisionId;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

    @ManyToOne
    @JoinColumn(name="meeting_id", referencedColumnName = "meeting_id", nullable=false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Meeting meeting;

    @Column(name =  "decision")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String decision;

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

        Decision that = (Decision) o;
        return Objects.equals(uuid, that.uuid);
    }
}
