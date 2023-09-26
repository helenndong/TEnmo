package com.techelevator.tenmo.managers;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;
import com.techelevator.tenmo.services.UserService;

import java.math.BigDecimal;
import java.util.List;

public class TransferManager {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final TransferService transferService;
    private final AccountService accountService;
    private final AuthenticatedUser currentUser;
    private ValidationManager validationManager;
    private final ConsoleService consoleService = new ConsoleService();
    private final UserService userService = new UserService(API_BASE_URL);
    private final int PENDING_TRANSFER_STATUS = 1;
    private final int APPROVED_TRANSFER_STATUS = 2;
    private final int REJECTED_TRANSFER_STATUS = 3;


    public TransferManager(AuthenticatedUser currentUser, TransferService transferService, AccountService accountService) {
        this.currentUser = currentUser;
        this.transferService = transferService;
        this.accountService = accountService;
    }

    public void pendingMenuSelection() {
        int transferId = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        Transfer transfer = transferService.getTransferById(transferId);

        if (transferId == 0) {
            return;
        } else if (transfer == null || transfer.getTransferStatusId() != PENDING_TRANSFER_STATUS) {
            System.out.println("Pending transfer with ID " + transferId + " not found.");
            return;
        }

        processPendingTransfer(transfer, transferId);
    }

    private void processPendingTransfer(Transfer transfer, int transferId) {
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("--------------------");
        int userInput = consoleService.promptForInt("Please choose an option: ");

        BigDecimal amount = transfer.getAmount();
        int transferStatusId;

        if (userInput == 0) {
            System.out.println("Transaction cancelled.");
        } else if (userInput == 1) {
            if (accountService.getAccountByUserId(currentUser.getUser().getId()).getBalance().compareTo(amount) < 0) {
                System.out.println("Insufficient funds");
            } else {
                accountService.sendRequestedTeBucks(
                        accountService.getAccountByUserId(currentUser.getUser().getId()).getId(),
                        transfer.getAccountTo(), amount);
                transferStatusId = APPROVED_TRANSFER_STATUS;
                transferService.updateTransferStatus(transferId, transferStatusId);
                System.out.println("Transfer status: Approved.");
            }
        } else {
            transferStatusId = REJECTED_TRANSFER_STATUS;
            transferService.updateTransferStatus(transferId, transferStatusId);
            System.out.println("Transfer status: Rejected.");
        }
    }

    public void sendBucks() {
        performTransaction("Enter ID of user you are sending to (0 to cancel): ", true, true);
    }

    public void requestBucks() {
        performTransaction("Enter ID of user you are requesting from (0 to cancel): ", false, false);
    }

    private void printUserList() {
        List<User> users = userService.findAllUsers();

        System.out.println();
        System.out.println("---------------------");
        System.out.println("Users");
        System.out.printf("%-15s%s%n", "ID", "Name");
        System.out.println("---------------------");
        for (User user : users) {
            if (user.getId() == currentUser.getUser().getId()) {
                continue;
            }
            System.out.printf("%-15d%s%n", user.getId(), user.getUsername());
        }
        System.out.println();
    }

    private void performTransaction(String action, boolean isSending, boolean isCurrentUser) {
        printUserList();
        validationManager = new ValidationManager(currentUser, transferService, accountService);

        int receiverId;
        while (true) {
            receiverId = consoleService.promptForInt(action);
            if (receiverId == 0) {
                System.out.println("Transaction cancelled.");
                return;
            } else if (validationManager.isValidReceiverId(receiverId)) {
                break;
            }
        }

        BigDecimal amount = null;
        boolean isValidAmount = false;

        while (!isValidAmount) {
            amount = consoleService.promptForBigDecimal("Enter amount: ");
            isValidAmount = validationManager.isValidAmount(amount, isCurrentUser);
        }

        if (isSending) {
            accountService.sendTeBucks(
                    accountService.getAccountByUserId(currentUser.getUser().getId()).getId(),
                    accountService.getAccountByUserId(receiverId).getId(),
                    amount);
            System.out.println("Transfer status: Approved.");
        } else {
            accountService.receiveTeBucks(
                    accountService.getAccountByUserId(receiverId).getId(),
                    accountService.getAccountByUserId(currentUser.getUser().getId()).getId(),
                    amount);
            System.out.println("Transfer status: Pending.");
        }
    }


}
