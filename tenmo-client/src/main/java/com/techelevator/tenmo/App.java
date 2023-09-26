package com.techelevator.tenmo;

import com.techelevator.tenmo.managers.AccountManager;
import com.techelevator.tenmo.managers.TransferManager;
import com.techelevator.tenmo.managers.ViewTransactionsManager;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;
public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final TransferService transferService = new TransferService(API_BASE_URL);
    private final AccountService accountService = new AccountService();
    private final ConsoleService consoleService = new ConsoleService();
    private final UserService userService = new UserService(API_BASE_URL);
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private AuthenticatedUser currentUser;
    private ViewTransactionsManager viewTransactionsManager;
    private AccountManager accountManager;
    private TransferManager transferManager;


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

            viewTransactionsManager = new ViewTransactionsManager(currentUser, transferService, accountService);
            transferManager = new TransferManager(currentUser, transferService, accountService);
            accountManager = new AccountManager(currentUser, accountService);

        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                accountManager.viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransactionsManager.viewTransferHistory();
            } else if (menuSelection == 3) {
                viewTransactionsManager.viewPendingRequests();
                transferManager.pendingMenuSelection();
            } else if (menuSelection == 4) {
                transferManager.sendBucks();
            } else if (menuSelection == 5) {
                transferManager.requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

}