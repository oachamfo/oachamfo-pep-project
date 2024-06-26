package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {
    private final Connection connection;

    // Default constructor
    public AccountDAO() {
        this.connection = ConnectionUtil.getConnection();
    }

     // DAO method to create a new account and return the created account
    public Account createAccount(Account account) {
    try (PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO account (username, password) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
        statement.setString(1, account.getUsername());
        statement.setString(2, account.getPassword());

        // Execute the SQL statement
        int affectedRows = statement.executeUpdate();

        // Check if the account was successfully created
        if (affectedRows > 0) {
            // Retrieve the auto-generated keys (if any)
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Set the ID of the created account
                    account.setAccount_id(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating account failed, no ID obtained.");
                }
            }
        } else {
            throw new SQLException("Creating account failed, no rows affected.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle or throw an exception as needed
    }
    return account;
}


    // DAO method to retrieve an account by username
    public Account getAccountByUsername(String username) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM account WHERE username = ?")) {
            statement.setString(1, username);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Check if a result is returned
            if (resultSet.next()) {
                return extractAccountFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or throw an exception as needed
        }

        // Return null if no account is found
        return null;
    }

    // DAO method to retrieve an account by username and password for login
    public Account getAccountByUsernameAndPassword(String username, String password) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM account WHERE username = ? AND password = ?")) {
            statement.setString(1, username);
            statement.setString(2, password);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Check if a result is returned
            if (resultSet.next()) {
                return extractAccountFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or throw an exception as needed
        }

        // Return null if no account is found
        return null;
    }

    // Helper method to extract an account from the ResultSet
    private Account extractAccountFromResultSet(ResultSet resultSet) throws SQLException {
        return new Account(
                resultSet.getInt("account_id"),
                resultSet.getString("username"),
                resultSet.getString("password")
        );
    }
}
