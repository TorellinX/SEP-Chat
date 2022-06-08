package chat.client.view.chatview;

import java.awt.Component;
import java.io.Serial;
import java.text.DateFormat;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

public class ChatCellRenderer extends JTextArea implements ListCellRenderer<ChatEntry> {

  @Serial
  private static final long serialVersionUID = 1L;

  private final DateFormat dateFormat;

  public ChatCellRenderer() {
    super();
    setOpaque(true);
    dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends ChatEntry> list,
                                                ChatEntry value, int index, boolean isSelected,
                                                boolean cellHasFocus) {
    setBackground(list.getBackground());
    setForeground(list.getForeground());

    setWrapStyleWord(true);

    if (value instanceof UserTextMessage userTextMsg) {
      String time = dateFormat.format(userTextMsg.getTime());

      setText(
          String.format("%s (%s): %s", userTextMsg.getSource(), time, userTextMsg.getContent()));
    }
    if (value instanceof UserJoinedMessage userJoinedMsg) {
      setText(userJoinedMsg.getNickname() + " has joined the chat.");
    }
    if (value instanceof UserLeftMessage userLeftMsg) {
      setText(userLeftMsg.getNickname() + " has left the chat.");
    }
    if (value instanceof LoggedInMessage loggedInMsg) {
      setText("Chat joined as " + loggedInMsg.getNickname() + ".");
    }

    return this;
  }
}
