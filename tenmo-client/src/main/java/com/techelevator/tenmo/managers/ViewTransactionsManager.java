package com.techelevator.tenmo.managers;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

public class ViewTransactionsManager {

    private final TransferService transferService;
    private final AccountService accountService;
    private final AuthenticatedUser currentUser;
    private final ConsoleService consoleService = new ConsoleService();



    public ViewTransactionsManager(AuthenticatedUser currentUser, TransferService transferService, AccountService accountService) {
        this.currentUser = currentUser;
        this.transferService = transferService;
        this.accountService = accountService;
    }

    public void viewTransferHistory() {
        Account currentUserAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] transfers = transferService.getAllTransfersByAccountId(currentUserAccount.getId());

        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.printf("%-10s %-15s %s%n", "ID", "From/To", "Amount");
        System.out.println("-------------------------------------------");


        for (Transfer transfer : transfers) {
            int accountFromId = transfer.getAccountFrom();
            String fromTo;
            String usernameTo = accountService.getUsernameByAccountId(transfer.getAccountTo());
            String usernameFrom = accountService.getUsernameByAccountId(transfer.getAccountFrom());

            if (currentUserAccount.getId() == accountFromId) {
                fromTo = "To: " + usernameTo;
            } else {
                fromTo = "From: " + usernameFrom;
            }
            String formattedAmount = String.format("$%.2f", transfer.getAmount());
            System.out.printf("%-10d %-15s %s%n", transfer.getId(), fromTo, formattedAmount);
        }
        System.out.println("-------------------------------------------");
        System.out.println();

        int transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        viewTransactionDetails(transferId);
    }

    private void viewTransactionDetails(int transferId) {
        Transfer transfer = transferService.getTransferById(transferId);
        if (transferId == 0) {
            return;
        } else if (transfer == null) {
            System.out.println("Transfer with ID " + transferId + " not found.");
            return;
        }
        String accountFrom = accountService.getUsernameByAccountId(transfer.getAccountFrom());
        String accountTo = accountService.getUsernameByAccountId(transfer.getAccountTo());

        System.out.println();
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------");
        System.out.println("Id: " + transfer.getId());
        System.out.println("From: " + accountFrom);
        System.out.println("To: " + accountTo);
        System.out.println("Type: " + transfer.getTransferTypeDesc());
        System.out.println("Status: " + transfer.getTransferStatusDesc());
        System.out.println("Amount: " + String.format("$%.2f", transfer.getAmount()));
    }

    public void viewPendingRequests() {
        Account currentUserAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] pendingTransfers = transferService.getPendingTransfers(currentUserAccount.getId());


        System.out.println("-------------------------------------------");
        System.out.println("Pending Transfers");
        System.out.printf("%-10s %-15s %s%n", "ID", "To", "Amount");
        System.out.println("-------------------------------------------");

        for (Transfer pendingTransfer : pendingTransfers) {
            String formattedAmount = String.format("$%.2f", pendingTransfer.getAmount());
            String accountTo = accountService.getUsernameByAccountId(pendingTransfer.getAccountTo());
            System.out.printf("%-10d %-15s %s%n", pendingTransfer.getId(), accountTo, formattedAmount);
        }

        System.out.println("-------------------------------------------");
        System.out.println();

    }

}
