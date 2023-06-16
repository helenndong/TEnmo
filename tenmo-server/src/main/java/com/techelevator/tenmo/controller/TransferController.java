package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.swing.table.TableRowSorter;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

//@PreAuthorize("isAuthenticated()")
@RestController
public class TransferController {

    UserDao userDao;

    @Autowired
    private TransferDao transferDao;

    @RequestMapping(path = "transfer/account/{id}", method = RequestMethod.GET)
    public List<Transfer> getAllTransferByAccountId(@NotNull @PathVariable int id){
        return transferDao.getAllTransferByAccountId(id);
    }

    @RequestMapping(path = "transfer/{id}",method = RequestMethod.GET)
    public Transfer getTransferById(@NotNull @PathVariable int id) {
        Transfer transfer = transferDao.getTransferById(id);
        return transfer;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(path = "transfer/{id}", method = RequestMethod.DELETE)
    public void deleteTransfer(@NotNull @PathVariable int id){
        transferDao.deleteTransfer(id);
    }

    @RequestMapping(path = "transfer/pending/{id}", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfers(@NotNull @PathVariable int id) {
        return transferDao.getPendingTransfers(id);
    }

    @RequestMapping(path = "/transfers/pending",method = RequestMethod.GET)
    public List<Transfer> getAllPendingTransfers(){
        return transferDao.getAllPendingTransfers();
    }
}