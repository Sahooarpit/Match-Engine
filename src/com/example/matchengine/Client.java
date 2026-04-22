package com.example.matchengine;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    private String clientId;

    private String name;

    private Instant createdAt;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PortfolioHolding> holdings = new HashSet<>();

    public Client(String name) {
        this.clientId = UUID.randomUUID().toString();
        this.name = name;
        this.createdAt = Instant.now();
    }
}
