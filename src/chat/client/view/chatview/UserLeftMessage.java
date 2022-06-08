package chat.client.view.chatview;

/**
 * A system message for the chat containing the data of a user that has left the chat.
 */
public class UserLeftMessage extends ChatEntry {

  private final String nickname;

  public UserLeftMessage(String nickname) {
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }
}
