package chat.client.view.chatview;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A chat message sent by a user at a specific point in time.
 */
public class UserTextMessage extends ChatEntry {

  private final String source;

  private final Date time;

  private final String content;

  /**
   * Create a chat text message.
   *
   * @param source  the Nickname of the sending user.
   * @param time    the time, when the message was sent.
   * @param content the text-content of the message
   */
  public UserTextMessage(String source, Date time, String content) {
    this.source = source;
    this.time = time;
    this.content = content;
  }

  public String getSource() {
    return source;
  }

  public Date getTime() {
    return (Date) time.clone();
  }

  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    String dateString = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT,
        Locale.GERMANY).format(time);
    return String.format("%s (%s): %s", source, dateString, content);
  }
}
