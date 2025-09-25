package com.sep.mmms_backend.repository;

import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.exceptions.CommitteeDoesNotExistException;
import com.sep.mmms_backend.exceptions.ExceptionMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommitteeRepository extends JpaRepository<Committee,Integer> {

    /**
     * if the committee is not found, the caller is responsible to check it and handle it
     */
    default Optional<Committee> findByIdNoException(int committeeId) {
        return this.findById(committeeId);
    }

    /**
     *
     * @param committeeId the id of the committee to be loaded from the database
     */
    default Committee findCommitteeById(int committeeId) {
        Optional<Committee> committee =  this.findById(committeeId);
        if(committee.isEmpty()){
            throw new CommitteeDoesNotExistException(ExceptionMessages.COMMITTEE_DOES_NOT_EXIST, committeeId);
        }

        return committee.get();
    }
}
