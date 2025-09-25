package com.sep.mmms_backend.dto;


import java.util.Objects;

/**
 * This DTO is being used in /addMembersToCommitteeDto
 */
public record NewMembershipRequest(Integer memberId, String role) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewMembershipRequest other)) return false;
        return Objects.equals(this.memberId, other.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}
