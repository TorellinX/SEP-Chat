package chat.client.model.events;

/**
 * Event that is sent by the model to the observers. It notifies the listeners about a failed login
 * attempt.
 */
public class LoginFailedEvent extends ChatEvent {

  @Override
  public String getName() {
    return "LoginFailedEvent";
  }
}
