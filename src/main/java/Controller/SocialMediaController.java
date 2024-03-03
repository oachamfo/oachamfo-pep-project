package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;


import java.util.List;

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
    private void registerHandler(Context context) {
        // Extract account information from the request body
        Account account = context.bodyAsClass(Account.class);

        // Call the account service to register the user
        accountService.createAccount(account);

        // Respond with the registered account details
        context.json(account);
    }

    // Handler for user login
    private void loginHandler(Context context) {
        // Extract account information from the request body
        Account account = context.bodyAsClass(Account.class);

        // Call the account service to perform login
        Account loggedInAccount = accountService.login(account);

        // Respond with the logged-in account details
        context.json(loggedInAccount);
    }

    // Handler for creating a new message
private void createMessageHandler(Context context) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    // Extract message information from the request body
    Message message = mapper.readValue(context.body(), Message.class);

    // Call the message service to create a new message
    Message addedMessage = messageService.createMessage(message);

    // Respond with the created message details
    if (addedMessage != null) {
     context.json(mapper.writeValueAsString(addedMessage));
    } else {
        context.status(400);
    }
//return addedMessage;
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
        // In the Javalin framework, this is a way to provide an HTTP response to the client with the deleted message details
        context.json(deletedMessage);
    } else {
        // In the Javalin framework, this is a way to provide an HTTP response to the client with an empty body if the message was not found
        context.status(200).result("");
    }

    //This returns data to the calling method.
    //This line is needed because the test case fails without it. Aside from the above HTTP responses, 
    //to the client, it can be inferred that the test case itself 
    //wants a return value with details about the deletedMessage 
    //even if the deletion was not successful. In this case, deletedMessage is just
    //the the name of the variable that stores the details of the message we are attempting to delete
    //it does not mean an actual deletion took place in the db. if no actual deletion took place, 
    //deletedMessage will be null because the MessageService delete method 
    //will return null from the MessageDAO delete method 
    return deletedMessage;
}

// Handler for updating a message by ID
private Message updateMessageHandler(Context context) {
    System.out.println("updatMessageHandler started from Owusu");
    // Extract message ID from the path parameters
    int messageId = context.pathParamAsClass("message_id", Integer.class).get();

    // Call the service to check if the message exists
    Message existingMessage = messageService.getMessageById(messageId);
  
    // Log existingMessage for debugging
  System.out.println("Existing message: " + existingMessage);

    //if message attempting to update does not exist, HTTP response 400
    if (existingMessage == null) {
        // Message not found
        context.status(400);
        return null;
    }

    //if message attempting to update exists, proceed as follows:
    
        // Extract updated message text from the request body
         String newMessageText = "";
        String rawBody = "";
        try {
            System.out.println("Before bodyAsClass");
            rawBody = context.body();
            System.out.println("Raw Body: " + rawBody);
            newMessageText = context.bodyAsClass(String.class);
            System.out.println("New Message Text:" + newMessageText);
            System.out.println("After bodyAsClass");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //if newMessageText is not valid
        if (newMessageText == null || newMessageText.isEmpty() || newMessageText.length() > 255) {
        System.out.println("Validation failed: " + newMessageText); // Log details for troubleshooting
        context.status(400);
        return null; // Bad Request - Invalid new message text
        }

    //if newMessageText is valid
    // Call the message service to update the message text by ID
    newMessageText = context.body();
    Message updatedMessage = messageService.updateMessageText(messageId, newMessageText);

    if (updatedMessage != null) {
        // Message found and updated successfully
    //    context.json(updatedMessage);
    context.status(200).json(updatedMessage);
} else {
        // Handle the case where the update is not successful (e.g., validation failed)
        context.status(400);
        }
    
   return updatedMessage;

}


    // Handler for retrieving messages by account ID
    private void getMessagesByAccountIdHandler(Context context) {
        // Extract account ID from the path parameters
        int accountId = context.pathParamAsClass("account_id", Integer.class).get();

        // Call the message service to retrieve messages by account ID
        context.json(messageService.getMessagesByAccountId(accountId));
    
    }
}
