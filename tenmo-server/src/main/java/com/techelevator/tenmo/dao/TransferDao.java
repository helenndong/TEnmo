package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDao {

    List<Transfer> getAllPendingTransfers();
    List<Transfer> getAllTransferByAccountId(int id);

    Transfer getTransferById(int id);

    Transfer createTransfer(Transfer transfer);

    void deleteTransfer(int id);

    List<Transfer> getPendingTransfers(int id);

    void populateTransferDescriptions(Transfer transfer);
}