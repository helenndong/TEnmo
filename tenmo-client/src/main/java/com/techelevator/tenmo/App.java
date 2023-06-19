package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final TransferService transferService = new TransferService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final ConsoleService consoleService = new ConsoleService();
    private final UserService userService = new UserService(API_BASE_URL);
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;

    private final int PENDING_TRANSFER_STATUS = 1;
    private final int APPROVED_TRANSFER_STATUS = 2;
    private final int REJECTED_TRANSFER_STATUS = 3;


    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }

    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();

        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        } else if (currentUser.getToken() != null) {
            transferService.setAuthToken(currentUser.getToken());
            accountService.setAuthToken(currentUser.getToken());
            userService.setAuthToken(currentUser.getToken());
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    private void viewCurrentBalance() {
        BigDecimal balance = accountService.getBalance(currentUser.getUser().getId());
        System.out.println("Your current account balance is: " + balance);
    }

    private void viewTransferHistory() {
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

//            if (transfer.getTransferStatusId() == 1) {
//                continue;
//            } else

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

    private void viewPendingRequests() {
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
                if (transferService != null) {
                    System.out.println("Transfer status: Approved.");
                }
            }
        } else {
            transferStatusId = REJECTED_TRANSFER_STATUS;
            transferService.updateTransferStatus(transferId, transferStatusId);
            if (transferService != null) {
                System.out.println("Transfer status: Rejected.");
            }
        }
    }

    private void sendBucks() {
        performTransaction("Enter ID of user you are sending to (0 to cancel): ", true, true);
    }

    private void requestBucks() {
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

        int receiverId;
        while (true) {
            receiverId = consoleService.promptForInt(action);
            if (receiverId == 0) {
                System.out.println("Transaction cancelled.");
                return;
            } else if (isValidReceiverId(receiverId)) {
                break;
            }
        }

        BigDecimal amount = null;
        boolean isValidAmount = false;

        while (!isValidAmount) {
            amount = consoleService.promptForBigDecimal("Enter amount: ");
            isValidAmount = isValidAmount(amount, isCurrentUser);
        }

        if (isSending) {
            accountService.sendTeBucks(
                    accountService.getAccountByUserId(currentUser.getUser().getId()).getId(),
                    accountService.getAccountByUserId(receiverId).getId(),
                    amount);
            if (accountService != null) {
                System.out.println("Transfer status: Approved.");
            }
        } else {
            accountService.receiveTeBucks(
                    accountService.getAccountByUserId(receiverId).getId(),
                    accountService.getAccountByUserId(currentUser.getUser().getId()).getId(),
                    amount);
            if (accountService != null) {
                System.out.println("Transfer status: Pending.");
            }
        }
    }

    private boolean isValidReceiverId(int receiverId) {
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

    private boolean isValidAmount(BigDecimal amount, boolean isCurrentUser) {
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