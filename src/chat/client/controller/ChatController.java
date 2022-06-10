package chat.client.controller;

import chat.client.model.ChatClientModel;

/**
 * The controller of the chat-UI.
 */
public class ChatController {

  private final ChatClientModel model;

  public ChatController(ChatClientModel model) {
    this.model = model;
  }

  /**
   * Send a login-request to the model.
   *
   * @param nickname The user-name with whom the user attempts to log in.
   */
  public void login(final String nickname) {
    if (nickname.trim().equals("")) {
      return;
    }
    model.logInWithName(nickname);
  }

  /**
   * Send a message to the model that is to be published to the other chat clients.
   *
   * @param text The message that is to be send.
   */
  public void postMessage(final String text) {
    model.postMessage(text);
  }

  //  /**
  //   * Run cleanup tasks.
  //   */
  //  public void dispose() {
  //    // made in "try with resources"
  //    try {
  //      model.dispose();
  //    } catch (IOException e) {
  //      e.printStackTrace();
  //    }
  //  }
}
