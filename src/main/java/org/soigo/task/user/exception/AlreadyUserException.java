package org.soigo.task.user.exception;


public class AlreadyUserException extends RuntimeException {

    public AlreadyUserException(String message) {
        super(message);
    }
}
