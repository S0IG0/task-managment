package org.soigo.task.user.service;

import org.soigo.task.task.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.soigo.task.user.model.User;

import java.util.UUID;

public interface UserService extends UserDetailsService {
    User create(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    User findById(UUID id);
    User changePassword(String username, String newRawPassword);
    boolean existsById(UUID id);
    boolean existsById(String id);
    Page<User> getFilteredUsers(
            Integer page,
            Integer size,
            String sortBy,
            Boolean reverse
    );
}
