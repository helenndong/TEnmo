package com.techelevator.tenmo.managers;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.services.AccountService;

import java.math.BigDecimal;

public class AccountManager {

    private final AccountService accountService;
    private final AuthenticatedUser currentUser;

    public AccountManager(AuthenticatedUser currentUser, AccountService accountService) {
        this.accountService = accountService;
        this.currentUser = currentUser;
    }

    public void viewCurrentBalance() {
        BigDecimal balance = accountService.getBalance(currentUser.getUser().getId());
        System.out.println("Your current account balance is: " + balance);
    }
}
