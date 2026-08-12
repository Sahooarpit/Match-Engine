package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.ClientService;
import com.example.matchengine.Instrument;
import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.InstrumentRepository;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ClientRepository clientRepository;
    private final InstrumentRepository instrumentRepository;
    private final ClientService clientService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Client>> getAllUsers() {
        return ResponseEntity.ok(clientRepository.findAll());
    }

    @PostMapping("/instruments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Instrument> addInstrument(@Valid @RequestBody InstrumentRequest instrumentRequest) {
        Instrument instrument = new Instrument(instrumentRequest.getTicker(), instrumentRequest.getDescription());
        instrumentRepository.save(instrument);
        return new ResponseEntity<>(instrument, HttpStatus.CREATED);
    }

    @PostMapping("/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> addBalance(@Valid @RequestBody BalanceRequest balanceRequest) {
        clientService.updateHolding(balanceRequest.getClientId(), balanceRequest.getTicker(), balanceRequest.getQuantity());
        return ResponseEntity.ok().build();
    }

    @Data
    static class InstrumentRequest {
        private String ticker;
        private String description;
    }

    @Data
    static class BalanceRequest {
        private String clientId;
        private String ticker;
        private BigDecimal quantity;
    }
}