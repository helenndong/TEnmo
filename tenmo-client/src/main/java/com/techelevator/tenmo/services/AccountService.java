package com.techelevator.tenmo.services;
import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public BigDecimal getBalance(AuthenticatedUser currentUser) {
        String url = baseUrl + "users/" + currentUser.getUser().getId() + "/balance";
        return restTemplate.getForObject(url, BigDecimal.class);
    }

}
