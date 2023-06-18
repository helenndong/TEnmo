package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class AccountService {

    public static final String API_BASE_URL = "http://localhost:8080/api/account/";
    private RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {this.authToken = authToken;}

    public BigDecimal getBalance(int userId){
        BigDecimal balance = null;
        try{
            ResponseEntity<BigDecimal> response = restTemplate.exchange(
                    API_BASE_URL + "user/" + userId + "/balance" ,
                    HttpMethod.GET, makeAuthEntity(), BigDecimal.class);

            balance = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public Account getAccountByUserId(int userId){
        Account account = null;
        try{
            ResponseEntity<Account> response = restTemplate.exchange(
                    API_BASE_URL + "user/" + userId,
                    HttpMethod.GET, makeAuthEntity(), Account.class);

            account = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return account;
    }

    public Integer getUserIdByAccountId(int id){
        Integer userId = null;
        try{
            ResponseEntity<Integer> response = restTemplate.exchange(
                    API_BASE_URL + "userId/" + id,
                    HttpMethod.GET, makeAuthEntity(), Integer.class);
            userId = response.getBody();
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return userId;
    }


    public void sendTeBucks(int senderId, int receiverId, BigDecimal amount){
        try{
            ResponseEntity<Void> response = restTemplate.exchange(
                    API_BASE_URL + "send/" + senderId + "/" + receiverId + "/" + amount ,
                    HttpMethod.POST, makeAuthEntity(), Void.class);
            System.out.println("Transfer Successful.");
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println("Transfer failed.");
        }
    }

    public void sendRequestedTeBucks(int senderId, int receiverId, BigDecimal amount){
        try{
            ResponseEntity<Void> response = restTemplate.exchange(
                    API_BASE_URL + "send-requested/" + senderId + "/" + receiverId + "/" + amount ,
                    HttpMethod.POST, makeAuthEntity(), Void.class);
            System.out.println("Transfer Successful.");
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
            System.out.println("Transfer failed.");
        }
    }

    public void receiveTeBucks(int receiverId, int senderId, BigDecimal amount){

        try{
            ResponseEntity<Void> response = restTemplate.exchange(
                    API_BASE_URL + "receive/" + receiverId + "/" + senderId + "/" + amount ,
                    HttpMethod.POST, makeAuthEntity(), Void.class);
            System.out.println("Request Successful.");
        }
        catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }
    public String getUsernameByAccountId(int accountId) {
        String url = API_BASE_URL + accountId + "/username";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(), String.class);
        return response.getBody();
    }


    private HttpEntity<Account> makeAccountEntity(Account account) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(account, headers);
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }







}