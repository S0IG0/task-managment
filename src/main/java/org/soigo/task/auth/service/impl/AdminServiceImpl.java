package org.soigo.task.auth.service.impl;

import jakarta.annotation.PostConstruct;
import org.soigo.task.user.model.Role;
import org.soigo.task.user.model.User;
import org.soigo.task.user.repository.UserRepository;
import org.soigo.task.auth.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AdminServiceImpl implements AdminService {
    final String adminEmail;
    final String adminUsername;
    final String adminPassword;
    final String adminFirstName;
    final String adminLastName;


    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminServiceImpl(
            @Value("${user.admin.email}")
            String adminEmail,
            @Value("${user.admin.username}")
            String adminUsername,
            @Value("${user.admin.password}")
            String adminPassword,
            @Value("${user.admin.first_name}")
            String adminFirstName,
            @Value("${user.admin.last_name}")
            String adminLastName,
            UserRepository userRepository, PasswordEncoder passwordEncoder
    ) {
        this.adminEmail = adminEmail;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Override
    public void createDefaultAdminUser() {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User user = User
                .builder()
                .username(adminUsername)
                .email(adminEmail)
                .first_name(adminFirstName)
                .last_name(adminLastName)
                .password(passwordEncoder.encode(adminPassword))
                .roles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN))
                .build();

        userRepository.save(user);
    }
}