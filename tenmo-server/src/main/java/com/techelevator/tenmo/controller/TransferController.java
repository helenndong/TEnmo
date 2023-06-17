package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
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

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("transfer/approve/{id}")
    public void updateTransferStatusToApproved(@NotNull @PathVariable int id){
        transferDao.updateTransferStatusToApproved(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("transfer/reject/{id}")
    public void updateTransferStatusToRejected(@NotNull @PathVariable int id){
        transferDao.updateTransferStatusToRejected(id);
    }

//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @PutMapping("transfer/{id}")
//    public void updateTransferStatus(@NotNull @PathVariable int transfer_status_id, @PathVariable int id){
//        transferDao.updateTransferStatus(transfer_status_id, id);
//    }


    @RequestMapping(path = "transfer/pending/{id}", method = RequestMethod.GET)
    public List<Transfer> getPendingTransfers(@NotNull @PathVariable int id) {
        return transferDao.getPendingTransfers(id);
    }

    @RequestMapping(path = "/transfers/pending",method = RequestMethod.GET)
    public List<Transfer> getAllPendingTransfers(){
        return transferDao.getAllPendingTransfers();
    }
}