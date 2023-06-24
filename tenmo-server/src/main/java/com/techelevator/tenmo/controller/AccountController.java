package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountDao accountDao;
    @Autowired
    private TransferDao transferDao;

    @Autowired
    private UserDao userDao;

    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public Account getAccountById(@NotNull @PathVariable int id){
        return accountDao.getAccountById(id);
    }

    @RequestMapping(path="/user/{id}", method = RequestMethod.GET)
    public Account getAccountByUserId(@NotNull @PathVariable int id) {
        return accountDao.getAccountByUserId(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "", method = RequestMethod.POST)
    public Account createAccount(@Valid @RequestBody Account account) {
        return accountDao.createAccount(account);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
    public void deleteAccount(@NotNull @PathVariable int id) {
        accountDao.deleteAccount(id);
    }


    @RequestMapping(path = "/user/{id}/balance", method = RequestMethod.GET)
    public BigDecimal getBalanceByUserId(@NotNull @PathVariable int id, Principal principal) {
        Authentication authentication = (Authentication) principal;
        String authenticatedUsername = authentication.getName();

        User authenticatedUser = userDao.findByUsername(authenticatedUsername);
        int authenticatedUserId = authenticatedUser.getId();

        if (id == authenticatedUserId) {
            return accountDao.getBalanceByUserId(id);
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized access to user's balance");
    }


//    @RequestMapping(path = "/user/{id}/balance", method = RequestMethod.GET)
//    public BigDecimal getBalanceByUserId(@NotNull @PathVariable int id) {
//        return accountDao.getBalanceByUserId(id);
//    }

    @RequestMapping(path = "/{id}/balance", method = RequestMethod.GET)
    public BigDecimal getBalanceByAccountId(@NotNull @PathVariable int id) {
        return accountDao.getBalanceByAccountId(id);
    }

    @GetMapping("/{id}/username")
    public String getUsernameByAccountId (@PathVariable int id) {
        return accountDao.getUsernameByAccountId(id);
    }

    @GetMapping("/userId/{id}")
    public Integer getUserIdByAccountId (@PathVariable int id) {
        return accountDao.getUserIdByAccountId(id);
    }

    @Transactional
    @RequestMapping(path = "/send/{sender}/{receiver}/{amount}", method = RequestMethod.POST)
    public void sendTeBucks (@PathVariable int sender, @PathVariable int receiver, @PathVariable BigDecimal amount) {
        accountDao.sendTeBucks(sender, receiver, amount);
            Transfer newTransfer = new Transfer();
            newTransfer.setAccountFrom(sender);
            newTransfer.setAccountTo(receiver);
            newTransfer.setAmount(amount);
            newTransfer.setTransferTypeId(2);
            newTransfer.setTransferStatusId(2);
            transferDao.createTransfer(newTransfer);
    }
    @Transactional
    @RequestMapping(path = "/send-requested/{sender}/{receiver}/{amount}", method = RequestMethod.POST)
    public void sendRequestedTeBucks(@PathVariable int sender, @PathVariable int receiver, @PathVariable BigDecimal amount) {
        accountDao.sendTeBucks(sender, receiver, amount);
    }

    @Transactional
    @RequestMapping(path = "/receive/{receiver}/{sender}/{amount}", method = RequestMethod.POST)
    public void receiveTeBucks (@PathVariable int receiver, @PathVariable int sender, @PathVariable BigDecimal amount) {
        Transfer newTransfer = new Transfer();
        newTransfer.setAccountFrom(receiver);
        newTransfer.setAccountTo(sender);
        newTransfer.setAmount(amount);
        newTransfer.setTransferTypeId(1);
        newTransfer.setTransferStatusId(1);
        transferDao.createTransfer(newTransfer);
    }




}