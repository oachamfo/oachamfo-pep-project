package DAO;

import Model.Message;
import Model.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Util.ConnectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MessageDAO {
    private final Connection connection;

    // Default constructor
    public MessageDAO() {
        this.connection = ConnectionUtil.getConnection();
    }

    // DAO method to create a new message
    public Message createMessage(Message message) {
        Connection connection = ConnectionUtil.getConnection();
    
        try {
            // message already validated in Service, otherwise, 
            //validate message information before creating
          
            // Execute the SQL statement to insert the message
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, message.getPosted_by());
                statement.setString(2, message.getMessage_text());
                    statement.setLong(3, message.getTime_posted_epoch());
    
                // Execute the SQL statement
                int affectedRows = statement.executeUpdate();
    
                if (affectedRows > 0) {
                    // Retrieve the generated keys
                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            // Get the generated message_id
                            int messageId = generatedKeys.getInt(1);
    
                            // Fetch and return the inserted message
                            return getMessageById(messageId);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or throw an exception as needed
        }
        return null;
    }
    
       
    // DAO method to retrieve a message by ID
    public Message getMessageById(int messageId) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM message WHERE message_id = ?")) {
            statement.setInt(1, messageId);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Check if a result is returned
            if (resultSet.next()) {
                return extractMessageFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or throw an exception as needed
        }

        // Return null if no message is found
        return null;
    }

    // DAO method to delete a message by ID
    public Message deleteMessage(int messageId) {
        Message deletedMessage = getMessageById(messageId); // Retrieve the message before deletion
    
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM message WHERE message_id = ?")) {
            statement.setInt(1, messageId);
    
            // Execute the SQL statement
            int affectedRows = statement.executeUpdate();
    
            // Check if the message was deleted
            if (affectedRows > 0) {
                return deletedMessage; // Return the deleted message
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or throw an exception as needed
        }
    
        // If the code reaches here, either the message was not found or not deleted
        return null;
    }
    
// DAO method to update the text of a message by ID
public Message updateMessageText(int messageId, String newMessageText) {
    try (PreparedStatement statement = connection.prepareStatement(
            "UPDATE message SET message_text = ? WHERE message_id = ?")) {
        statement.setString(1, newMessageText);
        statement.setInt(2, messageId);

        // Execute the SQL statement
        int affectedRows = statement.executeUpdate();

        // Check if the update was successful
        if (affectedRows > 0) {
            // Retrieve the updated message
            Message updatedMessage = getMessageById(messageId);

            // Convert the updated message to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValueAsString(updatedMessage);

            return updatedMessage;
        }
    } catch (SQLException | JsonProcessingException e) {
        e.printStackTrace();
        // Handle or throw an exception as needed
    }

    // Return null or an appropriate error message if the update fails
    return null;
}

    // DAO method to retrieve messages by account ID
    public List<Message> getMessagesByAccountId(int accountId) {
        List<Message> messages = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM message WHERE posted_by = ?")) {
            statement.setInt(1, accountId);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Process the ResultSet and add messages to the list
            while (resultSet.next()) {
                messages.add(extractMessageFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or throw an exception as needed
        }

        return messages;
    }

    // Helper method to extract a message from the ResultSet
    private Message extractMessageFromResultSet(ResultSet resultSet) throws SQLException {
        return new Message(
                resultSet.getInt("message_id"),
                resultSet.getInt("posted_by"),
                resultSet.getString("message_text"),
                resultSet.getLong("time_posted_epoch")
        );
    }

    // Helper method to retrieve an account by ID
    public Account getAccountById(int accountId) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM account WHERE account_id = ?")) {
            statement.setInt(1, accountId);

            // Execute the SQL statement
            ResultSet resultSet = statement.executeQuery();

            // Check if a result is returned
            if (resultSet.next()) {
                return new Account(
                        resultSet.getInt("account_id"),
                        resultSet.getString("username"),
                        resultSet.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or throw an exception as needed
        }

        // Return null if no account is found
        return null;
    }

    // DAO method to retrieve all messages
public List<Message> getAllMessages() {
    List<Message> messages = new ArrayList<>();

    try (Statement statement = connection.createStatement()) {
        // Execute the SQL statement
        ResultSet resultSet = statement.executeQuery("SELECT * FROM message");

        // Process the ResultSet and add messages to the list
        while (resultSet.next()) {
            messages.add(extractMessageFromResultSet(resultSet));
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle or throw an exception as needed
    }

    return messages;
}

}
