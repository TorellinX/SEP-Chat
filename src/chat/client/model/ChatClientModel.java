package chat.client.model;

import static java.util.Objects.requireNonNull;

import chat.client.model.events.ChatEvent;
import chat.client.model.events.LoggedInEvent;
import chat.client.model.events.LoginFailedEvent;
import chat.client.model.events.MessageAddedEvent;
import chat.client.view.chatview.ChatEntry;
import chat.client.view.chatview.LoggedInMessage;
import chat.client.view.chatview.UserJoinedMessage;
import chat.client.view.chatview.UserLeftMessage;
import chat.client.view.chatview.UserTextMessage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The model of the chat-client. Manages all the internal data belonging to a single chat client.
 */
public class ChatClientModel {

  private ClientNetworkConnection connection;
  private final PropertyChangeSupport support;
  private final List<ChatEntry> messages;
  private String nickname;

  /**
   * Construct a new chat client model with empty stored {@link ChatEntry chat entries} and a user
   * in a logged off state.
   */
  public ChatClientModel() {
    support = new PropertyChangeSupport(this);
    messages = new ArrayList<>();
  }

  /**
   * Add a network connector to this model.
   *
   * @param connection The network connection to be added.
   */
  public void setConnection(ClientNetworkConnection connection) {
    this.connection = connection;
  }

  /**
   * Add a {@link PropertyChangeListener} to the model for getting notified about any changes that
   * are published by this model.
   *
   * @param listener the view that subscribes itself to the model.
   */
  public void addPropertyChangeListener(PropertyChangeListener listener) {
    requireNonNull(listener);
    support.addPropertyChangeListener(listener);
  }

  /**
   * Remove a listener from the model. From then on tt will no longer get notified about any events
   * fired by the model.
   *
   * @param listener the view that is to be unsubscribed from the model.
   */
  public void removePropertyChangeListener(PropertyChangeListener listener) {
    requireNonNull(listener);
    support.removePropertyChangeListener(listener);
  }

  /**
   * Notify subscribed listeners that the state of the model has changed. To this end, a specific
   * {@link ChatEvent} gets fired such that the attached observers (i.e.,
   * {@link PropertyChangeListener}) can distinguish between what exactly has changed.
   *
   * @param event A concrete implementation of {@link ChatEvent}
   */
  private void notifyListeners(ChatEvent event) {
    support.firePropertyChange(event.getName(), null, event);
  }

  /**
   * Send a login request to the server.
   *
   * @param nickname the chosen nickname of the chat participant.
   */
  public void logInWithName(String nickname) {
    this.nickname = nickname;
    connection.sendLogin(nickname);
  }

  /**
   * Send a chat-message to the server that should be broadcast to other chat participants.
   *
   * @param message The message to be broadcast.
   */
  public void postMessage(String message) {
    UserTextMessage userTextMessage = new UserTextMessage(nickname, new Date(), message);
    connection.sendMessage(userTextMessage);
    addTextMessage(userTextMessage.getSource(), userTextMessage.getTime(),
        userTextMessage.getContent());
  }

  /**
   * Return a list of all chat-entries, including both user-message entries and status-update
   * entries in the chat.
   *
   * @return a list containing the entries of the chat.
   */
  public List<ChatEntry> getMessages() {
    return new ArrayList<>(messages);
  }

  /**
   * Update the model accordingly when a login attempt is successful. This is afterwards published
   * to the subscribed listeners.
   */
  public void loggedIn() {
    LoggedInMessage loggedInMessage = new LoggedInMessage(nickname);
    messages.add(loggedInMessage);
    notifyListeners(new LoggedInEvent());
    notifyListeners(new MessageAddedEvent(loggedInMessage));
  }

  /**
   * Notify the subscribed observers that a login attempt has failed.
   */
  public void loginFailed() {
    notifyListeners(new LoginFailedEvent());
  }

  /**
   * Add a text message to the list of chat entries. Used by the network layer to update the model
   * accordingly.
   *
   * @param nickname The name of the chat participants that has sent this message.
   * @param date     The date when the chat message was sent.
   * @param content  The actual content (text) that the participant had sent.
   */
  public void addTextMessage(String nickname, Date date, String content) {
    ChatEntry textMessage = new UserTextMessage(nickname, date, content);
    messages.add(textMessage);
    notifyListeners(new MessageAddedEvent(textMessage));
  }

  /**
   * Add a status-update entry "User joined" to the list of chat entries. Used by the network layer
   * to update the model accordingly.
   *
   * @param nickname The name of the newly joined user.
   */
  public void userJoined(String nickname) {
    ChatEntry userJoinedMessage = new UserJoinedMessage(nickname);
    messages.add(userJoinedMessage);
    notifyListeners(new MessageAddedEvent(userJoinedMessage));
  }

  /**
   * Add a status-update entry "User has left the chat" to the list of chat entries. Used by the
   * network layer to update the model accordingly.
   *
   * @param nickname nickname of the user
   */
  public void userLeft(String nickname) {
    ChatEntry userLeftMessage = new UserLeftMessage(nickname);
    messages.add(userLeftMessage);
    notifyListeners(new MessageAddedEvent(userLeftMessage));
  }

  //  /**
  //   * Cleanup the resources.
  //   */
  //  // made in "try with resources"
  //  public void dispose() throws IOException {
  //    connection.stop();
  //  }
}
