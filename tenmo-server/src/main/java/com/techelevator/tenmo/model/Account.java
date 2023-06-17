package com.techelevator.tenmo.model;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Account {

    private int id;
    @NotNull
    private int userId;
    @NotNull
    private BigDecimal balance;

    public Account(){
    }

    public Account(int id, int userId, BigDecimal balance) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}