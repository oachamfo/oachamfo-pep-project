package Service;

import DAO.MessageDAO;
import Model.Message;

import java.util.List;

import io.javalin.http.Context;

public class MessageService {
    private MessageDAO messageDAO;

    // Default constructor
    public MessageService() {
        messageDAO = new MessageDAO(); // Instantiate MessageDAO with the default constructor
    }
    public MessageService(MessageDAO messageDAO){
        this.messageDAO = messageDAO;
    }
    

    // Service method to create a new message
    public Message createMessage(Message message) {
        // Validate message information before creating
        validateMessage(message);

        // Call the DAO to persist the new message
        return messageDAO.createMessage(message);
        
        
}
    // Service method to retrieve all messages
    public List<Message> getAllMessages() {
        // Call the DAO to get all messages
        return messageDAO.getAllMessages();
    }

    // Service method to retrieve a message by ID
    public Message getMessageById(int messageId) {
        // Call the DAO to get the message by ID
        return messageDAO.getMessageById(messageId);
    }

    // Service method to delete a message by ID
    public Message deleteMessage(int messageId) {
        // Call the DAO to delete the message by ID
        return messageDAO.deleteMessage(messageId);
    }

// Service method to update the text of a message by ID
public Message updateMessageText(int messageId, String newMessageText) {
            Message updatedMessage = messageDAO.updateMessageText(messageId, newMessageText);

            // If the update is successful, return the updated message
            if (updatedMessage != null) {
                return updatedMessage;
            }

    // Return null if the update is not successful
    return null;
}
    
  
    // Service method to retrieve messages by account ID
    public List<Message> getMessagesByAccountId(int accountId) {
        // Call the DAO to get messages by account ID
        return messageDAO.getMessagesByAccountId(accountId);
    }

//validation methods

// Service method to validate message information and return the appropriate status code
private int validateMessage(Message message) {
    // Check if the message text is blank or over 255 characters
    if (message.getMessage_text() == null || message.getMessage_text().isBlank() || message.getMessage_text().length() > 255) {
        return 400; // Bad Request - Invalid message text
    }

    // Check if the posted_by refers to an existing user
    if (messageDAO.getAccountById(message.getPosted_by()) == null) {
        return 400; // Bad Request - Invalid user ID in posted_by
    }

    return 0; // Validation successful
}

// Service method to validate the new message text for an update
private int validateUpdateMessageText(String newMessageText) {
    // Check if the new message text is blank or over 255 characters
    if (newMessageText == null || newMessageText.trim().isEmpty() || newMessageText.length() > 255) {
        System.out.println("Validation failed: " + newMessageText); // Log details for troubleshooting
        return 400; // Bad Request - Invalid new message text
    }

    return 0; // Validation successful
}


}
