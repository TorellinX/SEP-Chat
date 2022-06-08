package chat.client.controller;

import chat.client.model.ChatClientModel;
import chat.client.view.ChatFrame;

/**
 * The controller of the chat-UI.
 */
public class ChatController {

  // TODO: insert code here
  private final ChatClientModel model;

  public ChatController(ChatClientModel model) {
    // TODO: insert code here
    this.model = model;

  }

  /**
   * Send a login-request to the model.
   *
   * @param nickname The user-name with whom the user attempts to log in.
   */
  public void login(final String nickname) {
    // TODO: insert code here
    if(nickname.trim().equals("")) {
      return;
    }
    System.out.println("Controller: from frame: EventListener: log versuch");
    model.logInWithName(nickname);

  }

  /**
   * Send a message to the model that is to be published to the other chat clients.
   *
   * @param text The message that is to be send.
   */
  public void postMessage(final String text) {
    // TODO: insert code here

  }

  /**
   * Run cleanup tasks.
   */
  public void dispose() {
    // TODO: insert code here

  }
}
