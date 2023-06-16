package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class UserService {

    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public UserService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    private String authToken = null;

    public void setAuthToken(String authToken) {this.authToken = authToken;}

    public List<User> getAllUsers() {
        String url = baseUrl + "users";
        User[] users = restTemplate.getForObject(url, User[].class);
        return Arrays.asList(users);
    }

    public User getUserById(int userId) {
        User user = null;
        try {
            String url = baseUrl + "users/" + userId;
            ResponseEntity<User> response =
                    restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(), User.class);
            user = response.getBody();

            if (user != null) {
                String username = findUsernameById(userId);
                user.setUsername(username);
            }
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return user;
    }

    private String findUsernameById(int userId) {
        String url = baseUrl + "users/" + userId + "/username";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, makeAuthEntity(), String.class);
        return response.getBody();
    }

    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

}