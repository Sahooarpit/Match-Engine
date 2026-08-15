package com.example.matchengine.controller;

import com.example.matchengine.ClientService;
import com.example.matchengine.Instrument;
import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.InstrumentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private InstrumentRepository instrumentRepository;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_shouldReturnOk() throws Exception {
        when(clientRepository.findAll()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsers_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addInstrument_shouldReturnCreated() throws Exception {
        AdminController.InstrumentRequest request = new AdminController.InstrumentRequest();
        request.setTicker("AAPL");
        request.setDescription("Apple Inc.");

        when(instrumentRepository.save(any(Instrument.class))).thenReturn(new Instrument());

        mockMvc.perform(post("/api/admin/instruments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addBalance_shouldReturnOk() throws Exception {
        AdminController.BalanceRequest request = new AdminController.BalanceRequest();
        request.setClientId("test-client-id");
        request.setTicker("USDT");
        request.setQuantity(BigDecimal.valueOf(1000.0));

        mockMvc.perform(post("/api/admin/balance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}