package chat.client.view.chatview;

/**
 * A system message for the chat. It contains the data of a user that has joined the chat.
 */
public class UserJoinedMessage extends ChatEntry {

  private final String nickname;

  public UserJoinedMessage(String nickname) {
    this.nickname = nickname;
  }

  public String getNickname() {
    return nickname;
  }
}
