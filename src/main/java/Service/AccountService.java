package Service;

import DAO.AccountDAO;
import Model.Account;

public class AccountService {
    private final AccountDAO accountDAO;

    // Default constructor
    public AccountService() {
        this.accountDAO = new AccountDAO(); // Instantiate AccountDAO with the default constructor
    }

    // Service method to create a new account
    public void createAccount(Account account) {
        // Validate account information before creating
        validateAccount(account);

        // Call the DAO to persist the new account
        accountDAO.createAccount(account);
    }

    // Service method to perform user login
    public Account login(Account account) {
        // Call the DAO to check if the account credentials are valid
        return accountDAO.getAccountByUsernameAndPassword(account.getUsername(), account.getPassword());
    }

    // Service method to retrieve an account by username
    public Account getAccountByUsername(String username) {
        // Call the DAO to get the account by username
        return accountDAO.getAccountByUsername(username);
    }

    // Service method to validate account information
    private void validateAccount(Account account) {
        // Check if the username is not blank
        if (account.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be blank");
        }

        // Check if the password is at least 4 characters long
        if (account.getPassword().length() < 4) {
            throw new IllegalArgumentException("Password must be at least 4 characters long");
        }

        // Check if an account with the same username already exists
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            throw new IllegalArgumentException("An account with the same username already exists");
        }
    }
}
