package chat.client.view.chatview;

/**
 * A system message for the chat. It contains the data that the user of this client has
 * successfully logged in.
 */
public class LoggedInMessage extends ChatEntry {

  private final String nickname;

  public LoggedInMessage(String nickname) {
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }
}
