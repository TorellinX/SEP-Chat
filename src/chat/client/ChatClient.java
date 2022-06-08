package chat.client;

import chat.client.controller.ChatController;
import chat.client.model.ChatClientModel;
import chat.client.model.ClientNetworkConnection;
import chat.client.view.ChatFrame;

/**
 * Starts the chat-client.
 */
public class ChatClient {

  public static void main(String[] args) {

    ChatClientModel model = new ChatClientModel();
    ChatController controller = new ChatController(model);
    ChatFrame chatFrame = new ChatFrame(controller, model);

    model.addPropertyChangeListener(chatFrame);

    System.out.println("Client: Text before connection");
    ClientNetworkConnection connection = new ClientNetworkConnection(model);
    model.setConnection(connection);
    connection.start();
    System.out.println("Client: Text after connection");

    chatFrame.setVisible(true);
  }
}
