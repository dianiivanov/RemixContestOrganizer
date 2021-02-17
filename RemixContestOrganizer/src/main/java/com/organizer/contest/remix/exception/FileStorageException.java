package com.organizer.contest.remix.exception;

import java.io.IOException;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message, IOException ex) {
        super(message,ex);
    }

    public FileStorageException(String message) {
        super(message);
    }
}
