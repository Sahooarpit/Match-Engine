package com.example.matchengine.controller;

import com.example.matchengine.Client;
import com.example.matchengine.repository.ClientRepository;
import com.example.matchengine.repository.OrderRepository;
import com.example.matchengine.repository.PortfolioHoldingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PortfolioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioHoldingRepository portfolioHoldingRepository;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private ClientRepository clientRepository;

    @Test
    @WithMockUser(username = "testuser")
    void getPortfolio_shouldReturnOk() throws Exception {
        Client client = new Client("testuser", "password", "test@example.com");
        when(clientRepository.findByUsername("testuser")).thenReturn(Optional.of(client));
        when(portfolioHoldingRepository.findAllByClientClientId(client.getClientId())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/portfolio"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getPendingOrders_shouldReturnOk() throws Exception {
        Client client = new Client("testuser", "password", "test@example.com");
        when(clientRepository.findByUsername("testuser")).thenReturn(Optional.of(client));
        when(orderRepository.findByClient_ClientIdAndStatusIn(client.getClientId(), new ArrayList<>())).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/portfolio/orders"))
                .andExpect(status().isOk());
    }
}