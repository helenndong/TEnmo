package com.techelevator.tenmo.managers;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.UserService;

import java.math.BigDecimal;

public class ValidationManager {

    private final TransferService transferService;
    private final AccountService accountService;
    private final AuthenticatedUser currentUser;

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final UserService userService = new UserService(API_BASE_URL);


    public ValidationManager (AuthenticatedUser currentUser, TransferService transferService, AccountService accountService) {
        this.currentUser = currentUser;
        this.transferService = transferService;
        this.accountService = accountService;
    }

    public boolean isValidReceiverId(int receiverId) {
        if (receiverId == currentUser.getUser().getId()) {
            System.out.println("You cannot enter your own ID.");
            System.out.println();
            return false;
        }
        User receiverUser = userService.getUserById(receiverId);
        if (receiverUser == null) {
            System.out.println("The ID you entered does not exist.");
            System.out.println();
            return false;
        }
        return true;
    }

    public boolean isValidAmount(BigDecimal amount, boolean isCurrentUser) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Amount must be greater than zero.");
            System.out.println();
            return false;
        }
        if (isCurrentUser) {
            if (accountService.getAccountByUserId(currentUser.getUser().getId()).getBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient funds");
                return false;
            }
        }
        return true;
    }
}
