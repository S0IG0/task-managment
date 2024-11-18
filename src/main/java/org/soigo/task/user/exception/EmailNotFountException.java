package org.soigo.task.user.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailNotFountException extends AuthenticationException {
    public EmailNotFountException(String msg) {
        super(msg);
    }
}
