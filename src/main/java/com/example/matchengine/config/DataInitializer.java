package com.example.matchengine.config;

import com.example.matchengine.Client;
import com.example.matchengine.Role;
import com.example.matchengine.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@org.springframework.core.annotation.Order(1)
public class DataInitializer implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("DataInitializer is running...");
        if (clientRepository.existsByUsername(adminUsername)) {
            log.info("Admin user '{}' already exists. Skipping creation.", adminUsername);
            return;
        }

        Client adminUser = new Client(adminUsername, passwordEncoder.encode(adminPassword), adminEmail);
        adminUser.setRoles(Set.of(Role.ADMIN, Role.USER));
        clientRepository.save(adminUser);

        log.info("************************************************************");
        log.info("Default admin user created successfully: {}", adminUsername);
        log.info("IMPORTANT: This is a default account. In production, override credentials using environment variables.");
        log.info("************************************************************");
    }
}