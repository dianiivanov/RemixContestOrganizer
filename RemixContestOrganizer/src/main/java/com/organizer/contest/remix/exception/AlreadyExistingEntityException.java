package com.organizer.contest.remix.exception;

public class AlreadyExistingEntityException extends RuntimeException{
    public AlreadyExistingEntityException() {
    }

    public AlreadyExistingEntityException(String message) {
        super(message);
    }
}
