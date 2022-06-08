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

  public UserTextMessage(String source, Date time, String content) {
    this.source = source;
    this.time = time;
    this.content = content;
  }

  public String getSource() {
    // TODO insert code here

    return null;
  }

  public Date getTime() {
    // TODO insert code here

    return null;
  }

  public String getContent() {
    // TODO insert code here

    return null;
  }

  @Override
  public String toString() {
    String dateString = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
        DateFormat.SHORT, Locale.GERMANY).format(time);
    return String.format("%s (%s): %s", source, dateString, content);
  }
}
