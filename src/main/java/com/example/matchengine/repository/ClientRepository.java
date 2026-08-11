package com.example.matchengine.repository;

import com.example.matchengine.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByUsername(String username);

    boolean existsByUsername(String username);
}