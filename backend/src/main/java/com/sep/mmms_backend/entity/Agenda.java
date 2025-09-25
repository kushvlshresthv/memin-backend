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
@Table(name = "agendas")
public class Agenda {
    @Id
    @Column(name="agenda_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer agendaId;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private String uuid;

    @ManyToOne
    @JoinColumn(name="meeting_id", referencedColumnName = "meeting_id", nullable=false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Meeting meeting;

    @Column(name =  "agenda")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String agenda;

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

        Agenda that = (Agenda) o;
        return Objects.equals(uuid, that.uuid);
    }
}
