package com.organizer.contest.remix.exception;

public class NonExistingEntityException extends RuntimeException{
    public NonExistingEntityException() {
    }

    public NonExistingEntityException(String message) {
        super(message);
    }
}
