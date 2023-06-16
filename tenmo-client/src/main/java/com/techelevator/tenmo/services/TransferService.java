package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class TransferService {

    private String baseURL;
    public TransferService(String baseURL) {
        this.baseURL = baseURL;
    }
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {this.authToken = authToken;}

    public Transfer[] getAllTransfers() {
        Transfer[] allTransfers = null;
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(baseURL + "/transfers", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            allTransfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        } return allTransfers;
    }

    public Transfer createTransfer(Transfer transfer) {
        Transfer transferCreated = null;
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseURL + "/transfer", HttpMethod.POST, makeTransferEntity(transfer), Transfer.class);
            transferCreated = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transferCreated;
    }

    public Transfer getTransferById(int id) {
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response =
                    restTemplate.exchange(baseURL + "/transfer/" + id, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        } return transfer;
    }

    public Transfer[] getPendingTransfers(int id){
        Transfer[] pendingTransfers = null;
        try {
            ResponseEntity<Transfer[]> response =
                    restTemplate.exchange(baseURL + "/transfer" + "/pending/" + id, HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            pendingTransfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        } return pendingTransfers;
    }


    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transfer, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }


}