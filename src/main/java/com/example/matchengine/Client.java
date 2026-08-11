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

    @Column(unique = true)
    private String username;

    private String password;
    
    @Column(unique = true)
    private String email;

    private Long balance;

    private Instant createdAt;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "client_roles", joinColumns = @JoinColumn(name = "client_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PortfolioHolding> holdings = new HashSet<>();

    public Client(String username, String password, String email) {
        this.clientId = UUID.randomUUID().toString();
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = Instant.now();
        this.roles.add(Role.USER);
    }
}