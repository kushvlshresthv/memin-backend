package com.sep.mmms_backend.repository;

import com.sep.mmms_backend.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
    public Meeting findMeetingById(Integer id);
}
