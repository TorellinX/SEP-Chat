package chat.client.model.events;

/**
 * Events that get send by the model when it changes its state. The events can afterwards be caught
 * by the listeners which are subscribed to the model.
 */
public abstract class ChatEvent {

  /**
   * The name of the event as string representation.
   *
   * @return a string describing the implementing event.
   */
  public abstract String getName();
}
