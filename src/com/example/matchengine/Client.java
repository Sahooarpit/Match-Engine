package com.example.matchengine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
public class Client {
    @Getter
    @Setter
    private String clientId;
    @Getter
    @Setter
    private BigDecimal balance;
    // look out on what is concurrent hashmap
    private Map<Ticker, Float> shares;

    public String viewBalance(){
        return clientId + " has $" + balance +" balance";
    }

    public String viewShares(){
        StringBuilder clientShares = new StringBuilder(clientId + "owns the following shares:\n");
        for(Ticker ticker : shares.keySet()){
            clientShares.append(ticker).append(" - ").append(shares.get(ticker)).append("\n");
        }
        return clientShares.toString();
    }

}
