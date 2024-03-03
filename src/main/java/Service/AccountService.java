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
    public Account createAccount(Account account) {
        // Validate account information before creating
        validateAccount(account);
        if (validateAccount(account)==0){
        // Call the DAO to persist the new account
        return accountDAO.createAccount(account);
        }
        return null; //if account is not created
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
    public int validateAccount(Account account) {
        // Check if the username is not blank
        if (account.getUsername().isBlank()) {
       return 401;
        }

        // Check if the password is at least 4 characters long
        if (account.getPassword().length() < 4) {
       return 401;
        }

        // Check if an account with the same username already exists
        if (accountDAO.getAccountByUsername(account.getUsername()) != null) {
            return 401;
        }
        return 0;
    }
}
