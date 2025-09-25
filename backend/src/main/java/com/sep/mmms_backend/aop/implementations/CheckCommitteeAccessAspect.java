package com.sep.mmms_backend.aop.implementations;

import com.sep.mmms_backend.aop.interfaces.CheckCommitteeAccess;
import com.sep.mmms_backend.entity.Committee;
import com.sep.mmms_backend.entity.Meeting;
import com.sep.mmms_backend.exceptions.*;
import com.sep.mmms_backend.service.CommitteeService;
import com.sep.mmms_backend.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CheckCommitteeAccessAspect {
    @Autowired
    private CommitteeService committeeService;

    @Autowired
    private MeetingService meetingService;

    @Before("@annotation(checkCommitteeAccess)")
    public void checkCommitteeAccess(JoinPoint joinPoint, CheckCommitteeAccess checkCommitteeAccess) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        Committee committee = null;
        Meeting meeting = null;
        String username = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Committee) {
                committee = (Committee) args[i];
            }

            if (args[i] instanceof Meeting) {
                meeting = (Meeting) args[i];
            }

            if ("username".equals(parameterNames[i]) && args[i] instanceof String) {
                username = (String) args[i];
            }
        }

        if (committee == null || username == null || (checkCommitteeAccess.shouldValidateMeeting() && meeting == null)) {
            throw new IllegalOperationException("Access of meeting or committee could not be verified");
        }

        //checking access for the committee
        if (!committee.getCreatedBy().getUsername().equals(username)) {
            throw new CommitteeNotAccessibleException(ExceptionMessages.COMMITTEE_NOT_ACCESSIBLE, committee.getName());
        }


        if (checkCommitteeAccess.shouldValidateMeeting()) {
            if(!meeting.getCreatedBy().equals(username)) {
                throw new MeetingNotAccessibleException(ExceptionMessages.MEETING_NOT_ACCESSIBLE, meeting.getTitle());
            }
        }
    }
}
