package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.MatchEngine;
import com.example.matchengine.dto.OrderRequest;
import com.example.matchengine.repository.ClientRepository;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchEngine matchEngine;

    @MockBean
    private ClientRepository clientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testuser")
    void submitOrder_shouldReturnOk() throws Exception {
        Client client = new Client("testuser", "password", "test@example.com");
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setTicker("AAPL");
        orderRequest.setSide(com.example.matchengine.Side.BUY);
        orderRequest.setQuantity(10);
        orderRequest.setPrice(BigDecimal.valueOf(150.0));

        when(clientRepository.findByUsername("testuser")).thenReturn(Optional.of(client));
        when(matchEngine.processOrder(any())).thenReturn(new java.util.ArrayList<>());

        mockMvc.perform(post("/trade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk());
    }
}