package chat.client.model;

import static java.nio.charset.StandardCharsets.UTF_8;

import chat.client.view.chatview.UserTextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The network-connection of the client. Establishes a connection to the server and takes
 * care of sending and receiving messages in JSON format.
 */
public class ClientNetworkConnection {

  private static final String HOST = "localhost";
  private static final int PORT = 8080;
  private Socket socket;

  OutputStreamWriter writer;
  BufferedReader reader;

  // TODO: insert code here

  public ClientNetworkConnection(ChatClientModel model) {
    // TODO: insert code here



  }

  /**
   * Start the network connection.
   */
  public void start() {
    // TODO: insert code here
    System.out.println("Client want start connection");
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          socket = new Socket(HOST, PORT);
          writer = new OutputStreamWriter(socket.getOutputStream(), UTF_8);
          reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));


        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
  }


  /**
   * Stop the network-connection.
   */
  public void stop() throws IOException {
    // TODO: insert code here
    socket.close();
  }

  /**
   * Send a login-request to the server.
   *
   * @param nickname The name of the user that requests to log in.
   */
  public void sendLogin(String nickname){
    // TODO: insert code here
    // { "type" : "login", "nick" : "<nick>" }
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("type", "login");
      jsonObject.put("nick", nickname);

      writer.write(jsonObject + System.lineSeparator());
      writer.flush();

      System.out.println("Sent login-message to server: " + jsonObject);
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
    // TODO: insert code here

  }
}
