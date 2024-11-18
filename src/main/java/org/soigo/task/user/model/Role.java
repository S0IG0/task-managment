package org.soigo.task.user.model;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    @Contract(pure = true)
    @Override
    public @NotNull String getAuthority() {
        return name();
    }
}