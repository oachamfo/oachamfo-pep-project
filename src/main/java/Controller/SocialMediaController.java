package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;


import Model.Account;
import Model.Message;

import Service.AccountService;
import Service.MessageService;



public class SocialMediaController {
     AccountService accountService;
     MessageService messageService;

    // Constructor with default implementations
    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        // Create a new Javalin app instance.
        Javalin app = Javalin.create();

        // Define an example endpoint with an example handler.
        app.get("example-endpoint", this::exampleHandler);

        // Registering your endpoints with their corresponding handlers
        app.post("/register", this::registerHandler);
        app.post("/login", this::loginHandler);
        app.post("/messages", this::createMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        app.patch("/messages/{message_id}", this::updateMessageHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        // Respond with a JSON string as an example.
        context.json("sample text");
    }

    // Handler for user registration
    private Account registerHandler(Context context) {
        // Extract account information from the request body
        Account account = context.bodyAsClass(Account.class);

        int validationStatus = accountService.validateAccount(account);
        if (validationStatus == 0){
        // Call the account service to register the user
        accountService.createAccount(account);

        // Respond with the registered account details
        context.status(200).json(account);
        }else context.status(400);
        return account;
    }

    // Handler for user login
    private void loginHandler(Context context) {
        // Extract account information from the request body
        Account account = context.bodyAsClass(Account.class);

        // Call the account service to perform login
        Account loggedInAccount = accountService.login(account);
        
        if (loggedInAccount != null){
        // Respond with the logged-in account details
        context.status(200).json(loggedInAccount);
        }else context.status(401); //else respond with a 401 status code
        
    }

    // Handler for creating a new message
    private void createMessageHandler(Context context) throws JsonProcessingException {

        // Extract message information from the request body
        Message message = context.bodyAsClass(Message.class); 
        
        // Call the message service to create a new message
        Message addedMessage = messageService.createMessage(message);

        // Respond with the created message details
        if (addedMessage != null) {
        context.status(200).json(addedMessage);
        } else {
        context.status(400);
        }

    }


    // Handler for retrieving all messages
    private void getAllMessagesHandler(Context context) {
        // Call the message service to retrieve all messages
        context.json(messageService.getAllMessages());
    }

    // Handler for retrieving a message by ID
    private void getMessageByIdHandler(Context context) {
      // Extract message ID from the path parameters
      int messageId = context.pathParamAsClass("message_id", Integer.class).get();

      // Call the message service to retrieve the message by ID
      Message message = messageService.getMessageById(messageId);
  
      // Check if the message was found
      if (message != null) {
          // Provide an HTTP response to the client with the retrieved message details
          context.json(message);
      } else {
          // Provide an HTTP response to the client with a status indicating the message was not found
          context.status(200).result("");
      }

    }

    // Handler for deleting a message by ID
    private Message deleteMessageHandler(Context context) {
    // Extract message ID from the path parameters
    int messageId = context.pathParamAsClass("message_id", Integer.class).get();

    // Call the message service to delete the message by ID
    Message deletedMessage = messageService.deleteMessage(messageId);

    // Check if the message was deleted or not found
    if (deletedMessage != null) {
        context.json(deletedMessage); // Provide HTTP response with deleted message details
    } else {
        context.status(200).result(""); // Provide HTTP response with empty body if message not found
    }

     // Return deletedMessage as a return value (even if null) for potential use in the calling method or the test case
     // The test case fails without the return of deletedMessage
     return deletedMessage;
}

    // Handler for updating a message by ID
    private void updateMessageHandler(Context context) {
    
    //Log this for debugging
    System.out.println("updateMessageHandler started from Owusu");
    
    // Extract message ID from the path parameters
    int messageId = context.pathParamAsClass("message_id", Integer.class).get();

    // Call the service to check if the message exists
    Message existingMessage = messageService.getMessageById(messageId);
  
    // Log existingMessage for debugging
    System.out.println("Existing message: " + existingMessage);

    //If message attempting to update does not exist, HTTP response 400
    if (existingMessage == null) {
        // Message not found
        context.status(400);
    }

    //If message attempting to update exists, proceed as follows:
    
        // Extract updated message text from the request body
        JsonNode requestBody = context.bodyAsClass(JsonNode.class);
        String newMessageText = requestBody.get("message_text").asText();
                         
        //If newMessageText is valid proceed as follows:
        if (messageService.validateUpdateMessageText(newMessageText)==0){
            // Call the message service to update the message text by ID
            Message updatedMessage = messageService.updateMessageText(messageId, newMessageText);

            if (updatedMessage != null) {
            // Message found and updated successfully HTTP response
            context.status(200).json(updatedMessage);
            }
        } else {
            // Handle the case where the update is not successful (e.g., validation failed)
            context.status(400);
        }
    
    }

    // Handler for retrieving messages by account ID
    private void getMessagesByAccountIdHandler(Context context) {
        // Extract account ID from the path parameters
        int accountId = context.pathParamAsClass("account_id", Integer.class).get();

        // Call the message service to retrieve messages by account ID
        context.json(messageService.getMessagesByAccountId(accountId));
    
    }
}
