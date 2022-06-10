package chat.client.model;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import chat.client.view.chatview.UserTextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The network-connection of the client. Establishes a connection to the server and takes care of
 * sending and receiving messages in JSON format.
 */
public class ClientNetworkConnection {

  private static final String HOST = "localhost";
  private static final int PORT = 8080;
  private final ChatClientModel model;
  private OutputStreamWriter writer;
  private BufferedReader reader;


  public ClientNetworkConnection(ChatClientModel model) {
    this.model = model;
  }

  /**
   * Start the network connection.
   */
  public void start() {
    Thread thread = new Thread(() -> {
      try (Socket socket = new Socket(HOST, PORT)) {
        writer = new OutputStreamWriter(socket.getOutputStream(), UTF_8);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));

        while (true) {
          String line = reader.readLine();
          JSONObject receivedMessage = new JSONObject(line);
          String type = (String) receivedMessage.get("type");
          switch (type) {
            case "login failed" -> {
              //    { "type" : "login failed" }
              System.out.println("Client: login-failed-message received");
              model.loginFailed();
            }
            case "login success" -> {
              //    { "type" : "login success" }
              System.out.println("Client: login-success-message received");
              model.loggedIn();
            }
            case "user joined" -> {
              //      { "type" : "user joined", "nick" : "<nick>" }
              System.out.println(
                  "Client: user-joined-message received. User " + receivedMessage.get("nick"));
              model.userJoined((String) receivedMessage.get("nick"));
            }
            case "message" -> {
              //   { "type" : "message",
              //      "time" : "<day>.<month>.<year> <hour>:<minute>:<second>",
              //      "nick" : "<sender>",
              //      "content" : "<message content>"
              //    }
              System.out.println("Client: Text-message received. " + receivedMessage);
              model.addTextMessage((String) receivedMessage.get("nick"),
                  new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").parse(
                      (String) receivedMessage.get("time")),
                  (String) receivedMessage.get("content"));
            }
            case "user left" -> {
              //    { "type" : "user left", "nick" : "<nick>" }
              System.out.println(
                  "Client: user-left-message received. User " + receivedMessage.get("nick"));
              model.userLeft((String) receivedMessage.get("nick"));
            }
            default -> throw new IllegalArgumentException("Unknown type of message.");
          }
        }
      } catch (IOException | JSONException | ParseException e) {
        e.printStackTrace();
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  //  /**
  //   * Stop the network-connection.
  //   */
  //  public void stop() throws IOException {
  //    // made in "try with resources"
  //  }

  /**
   * Send a login-request to the server.
   *
   * @param nickname The name of the user that requests to log in.
   */
  public void sendLogin(String nickname) {
    // { "type" : "login", "nick" : "<nick>" }
    try {
      JSONObject loginMessage = new JSONObject();
      loginMessage.put("type", "login");
      loginMessage.put("nick", nickname);

      writer.write(loginMessage + System.lineSeparator());
      writer.flush();

      System.out.println("Sent login-message to server: " + loginMessage);
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Send a chat message to the server.
   *
   * @param chatMessage The {@link UserTextMessage} containing the message of the user.
   */
  public void sendMessage(UserTextMessage chatMessage) {
    // { "type" : "post message", "content" : "<message content>" }
    try {
      JSONObject postMessage = new JSONObject();
      postMessage.put("type", "post message");
      postMessage.put("content", requireNonNull(chatMessage.getContent()));

      writer.write(postMessage + System.lineSeparator());
      writer.flush();

      System.out.println("Sent post-message to server: " + postMessage);

    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }
}
