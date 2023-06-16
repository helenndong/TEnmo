package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final TransferService transferService = new TransferService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final ConsoleService consoleService = new ConsoleService();
    private final UserService userService = new UserService(API_BASE_URL);
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final List<User> users = userService.getAllUsers();

    private Transfer[] transfers = transferService.getAllTransfers();
    private AuthenticatedUser currentUser;

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

        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.printf("%-10s %-15s %s%n", "ID", "From/To", "Amount");
        System.out.println("-------------------------------------------");

        for (Transfer transfer : transfers) {
            String fromTo = transfer.getTransferTypeId() == 1 ? "From: " + accountService.getUsernameByAccountId(transfer.getAccountFrom()) :
                    "To: " + accountService.getUsernameByAccountId(transfer.getAccountTo());
            String formattedAmount = String.format("$%.2f", transfer.getAmount());
            System.out.printf("%-10d %-15s %s%n", transfer.getId(), fromTo, formattedAmount);
        }
        System.out.println("-------------------------------------------");
        System.out.println();

        int transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if (transferId == 0) {
            return;
        }

        Transfer transfer = transferService.getTransferById(transferId);
        if (transfer == null) {
            System.out.println("Transfer with ID " + transferId + " not found.");
            return;
        }

        System.out.println();
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------");
        System.out.println("Id: " + transfer.getId());
        System.out.println("From: " + accountService.getUsernameByAccountId(transfer.getAccountFrom()));
        System.out.println("To: " + accountService.getUsernameByAccountId(transfer.getAccountTo()));
        System.out.println("Type: " + transfer.getTransferTypeDesc());
        System.out.println("Status: " + transfer.getTransferStatusDesc());
        System.out.println("Amount: " + String.format("$%.2f", transfer.getAmount()));
    }

    private void viewPendingRequests() {
        Transfer[] pendingTransfers = transferService.getPendingTransfers
                (accountService.getAccountByUserId(currentUser.getUser().getId()).getId());
        for (Transfer transfer : pendingTransfers) {
            System.out.println(transfer);
        }

    }

    private void sendBucks() {
        performTransaction("Enter ID of user you are sending to (0 to cancel): ", true);
    }

    private void requestBucks() {
        performTransaction("Enter ID of user you are requesting from (0 to cancel): ", false);
    }

    private void printUserList() {

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
    private void performTransaction(String action, boolean isSending) {
        printUserList();

        int receiverId = consoleService.promptForInt(action);
        if (isInvalidReceiverId(receiverId)) return;

        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        if (isValidAmount(amount)) return;

        if (isSending) {
            accountService.sendTeBucks(
                    accountService.getAccountByUserId(currentUser.getUser().getId()).getId(),
                    accountService.getAccountByUserId(receiverId).getId(),
                    amount);
        } else {
            accountService.receiveTeBucks(
                    accountService.getAccountByUserId(receiverId).getId(),
                    accountService.getAccountByUserId(currentUser.getUser().getId()).getId(),
                    amount);
        }
    }

    private boolean isValidAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid amount. Amount must be greater than zero.");
            return true;
        }
        if (accountService.getAccountByUserId(currentUser.getUser().getId()).getBalance().compareTo(amount) < 0) {
            System.out.println("Insufficient funds");
            return true;
        }
        return false;
    }

    private boolean isInvalidReceiverId(int receiverId) {
        if (receiverId == 0) {
            System.out.println("Transaction cancelled.");
            return true;
        } else if (receiverId == currentUser.getUser().getId()) {
            System.out.println("You cannot enter your own ID.");
            return true;
        }
        User receiverUser = userService.getUserById(receiverId);
        if (receiverUser == null) {
            System.out.println("The ID you entered does not exist.");
            return true;
        }
        return false;
    }

}