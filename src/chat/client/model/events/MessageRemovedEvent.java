package chat.client.model.events;


import chat.client.view.chatview.ChatEntry;

/**
 * Event that is sent by the model to the observers. It notifies about a message that has been
 * removed from the collection of stored messages.
 */
public class MessageRemovedEvent extends ChatEvent {

  private final ChatEntry message;

  public MessageRemovedEvent(ChatEntry message) {
    this.message = message;
  }

  public ChatEntry getMessage() {
    return message;
  }

  @Override
  public String getName() {
    return "MessageRemovedEvent";
  }
}
