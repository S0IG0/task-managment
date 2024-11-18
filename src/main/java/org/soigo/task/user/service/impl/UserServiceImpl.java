package org.soigo.task.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.soigo.task.user.exception.EmailNotFountException;

import org.soigo.task.task.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.soigo.task.user.model.Role;
import org.soigo.task.user.model.User;
import org.soigo.task.user.repository.UserRepository;
import org.soigo.task.user.service.UserService;

import java.util.Set;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    static final Set<Role> defaultRoles = Set.of(Role.ROLE_USER);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        User user = findByUsername(username);
        log.debug("Loaded user details by username: {}", user.getUsername());
        return user;
    }

    @Override
    public User create(@NotNull User user) {
        log.info("Creating new user with username: {}", user.getUsername());
        user.setRoles(defaultRoles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = userRepository.save(user);
        log.debug("Created user with username: {}", createdUser.getUsername());
        return createdUser;
    }

    @Override
    public User findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Username not found: {}", username);
                    return new UsernameNotFoundException("Not found username: " + username);
                });
        log.debug("Found user with username: {}", user.getUsername());
        return user;
    }

    @Override
    public User findByEmail(String email) {
        log.info("Finding user by email: {}", email);
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Email not found: {}", email);
                    return new EmailNotFountException("Not found email: " + email);
                });
        log.debug("Found user with email: {}", user.getEmail());
        return user;
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User with id: " + id + " not found")
        );
    }

    @Override
    public User changePassword(String username, String newRawPassword) {
        log.info("Changing password for user: {}", username);
        User user = findByUsername(username);
        user.setPassword(passwordEncoder.encode(newRawPassword));
        User updatedUser = userRepository.save(user);
        log.debug("Password changed for user: {}", updatedUser.getUsername());
        return updatedUser;
    }

    @Override
    public boolean existsById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        return true;
    }

    @Override
    public boolean existsById(String id) {
        try {
            return userRepository.existsById(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Page<User> getFilteredUsers(Integer page, Integer size, String sortBy, @NotNull Boolean reverse) {
        Sort sort = Sort.by(sortBy);

        if (reverse) {
            sort = sort.reverse();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        return userRepository.findAll(pageable);
    }
}
