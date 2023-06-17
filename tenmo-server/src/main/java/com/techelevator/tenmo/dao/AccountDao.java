package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;

public interface AccountDao {

    Account getAccountById(int id);

    Account getAccountByUserId(int id);

    Account createAccount (Account account);

    void deleteAccount (int id);

    BigDecimal getBalanceByUserId (int id);

    BigDecimal getBalanceByAccountId (int id);

    Account addToAccount (int id, BigDecimal amount);

    Account subtractFromAccount (int id, BigDecimal amount);

    String getUsernameByAccountId(int id);

    Integer getUserIdByAccountId(int id);

    void sendTeBucks(int sender, int receiver, BigDecimal amount);

    void sendRequestedTeBucks(int sender, int receiver, BigDecimal amount);

//    void receiveTeBucks (int receiver, int sender, BigDecimal amount);


}