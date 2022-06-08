package chat.client.model.events;

import chat.client.view.chatview.ChatEntry;

/**
 * Event that is sent by the model to the observers. It notifies about a new message that has been
 * received from another participating user in the chat.
 */
public class MessageAddedEvent extends ChatEvent {

  private final ChatEntry message;

  public MessageAddedEvent(ChatEntry message) {
    this.message = message;
  }

  public ChatEntry getMessage() {
    return message;
  }

  @Override
  public String getName() {
    return "MessageAddedEvent";
  }
}
