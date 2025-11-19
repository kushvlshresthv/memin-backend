package com.sep.mmms_backend.dto;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sep.mmms_backend.entity.AppUser;
import com.sep.mmms_backend.enums.CommitteeStatus;
import com.sep.mmms_backend.enums.MinuteLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class CommitteeCreationDto {
    @NotBlank(message ="committee name is required")
    private String name;

    @NotBlank(message="committee description/aim is required")
    private String description;

    private CommitteeStatus status = CommitteeStatus.ACTIVE;

    Integer maximumNumberOfMeetings;

    //TODO implement a non blank validator for this
//    @NotBlank(message="minute language is required")
    private MinuteLanguage minuteLanguage;

    //member id and role
    //this annotation causes the json's 'key' to be used as the 'key' of the map
    @JsonDeserialize(keyUsing = IntegerKeyDeserializer.class)
    Map<Integer, String> members = new HashMap<>();

    @NotNull(message="committee coordinator is missing")
    Integer coordinatorId;

    private static class IntegerKeyDeserializer extends KeyDeserializer {
        @Override
        public Integer deserializeKey(String key, DeserializationContext ctxt) {
            return Integer.valueOf(key);
        }
    }
}
