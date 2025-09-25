package com.sep.mmms_backend.exceptions;


public class CommitteeNotAccessibleException extends RuntimeException {
    private final String committeeName;

    public CommitteeNotAccessibleException(ExceptionMessages message, String committeeName) {
        super(message.toString());
        this.committeeName = committeeName;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "[Committee Name: " + committeeName + "]";
    }
}
