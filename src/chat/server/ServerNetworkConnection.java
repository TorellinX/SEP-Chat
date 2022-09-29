package chat.server;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The network layer of the chat server. Takes care of processing both the connection requests and
 * message handling.
 */
public class ServerNetworkConnection {

  private final ServerSocket serverSocket;
  private static final int PORT = 8080;
  private final List<User> users;

  /**
   * Creates an instance of network connection.
   */
  public ServerNetworkConnection() throws IOException {
    this.serverSocket = new ServerSocket(PORT);
    this.users = new ArrayList<>();
  }

  /**
   * Start the network-connection such that clients can establish a connection to this server.
   */
  public void start() {
    System.out.println("Server is waiting for connections...");
    while (true) {
      try {
        Socket socket = serverSocket.accept();
        Thread thread = new Thread() {
          @Override
          public void run() {
            try {
              System.out.println("Server: new connection via socket " + socket);

              OutputStreamWriter writer = new OutputStreamWriter(socket.getOutputStream(),
                  UTF_8);
              BufferedReader reader = new BufferedReader(
                  new InputStreamReader(socket.getInputStream(), UTF_8));

              while (true) {
                String line = reader.readLine();
                if (line == null) {
                  System.out.println("Server: Client disconnected");
                  return;
                }
                System.out.println("Server: Message from client: " + line);
                JSONObject receivedMessage = new JSONObject(line);
                String type = (String) receivedMessage.get("type");
                if (!type.equals("login")) {
                  throw new IllegalArgumentException("Client not logged in");
                }
                if (receivedMessage.get("nick").toString().trim().equals("")) {
                  continue;
                }
                if (!isNicknameFree(receivedMessage)) {
                  sendLoginFailedMessage(writer);
                  continue;
                } else {
                  handleLoginSuccess(receivedMessage, writer, socket);
                }
              }
            } catch (IOException | JSONException e) {
              e.printStackTrace();
            }
          }
        };
        thread.setDaemon(true);
        thread.start();
      } catch (SocketException e) {
        if (serverSocket.isClosed()) {
          // Server was stopped, exiting the thread
          return;
        }
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Check if the nickname is not used by another client.
   *
   * @param loginRequestMessage message from client with login-request
   * @return true, when the nickname is free
   * @throws JSONException if the message don't have appropriate format
   */
  private boolean isNicknameFree(JSONObject loginRequestMessage)
      throws JSONException {
    // received: { "type" : "login", "nick" : "<nick>" }
    String nickname = loginRequestMessage.get("nick").toString().trim();
    synchronized (users) {
      for (User user : users) {
        if (user.getName().equals(nickname)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Send a message to the client, that login is failed.
   *
   * @param writer output stream to the client
   */
  private void sendLoginFailedMessage(OutputStreamWriter writer) {
    // { "type" : "login failed" }
    try {
      JSONObject loginFailedMessage = new JSONObject();
      loginFailedMessage.put("type", "login failed");
      writer.write(loginFailedMessage + System.lineSeparator());
      writer.flush();
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Executes routines upon successful connection..
   *
   * @param loginRequestMessage message from client with login-request
   * @param writer              output stream to the client
   * @param socket              socket through which the client is connected
   */
  private void handleLoginSuccess(JSONObject loginRequestMessage, OutputStreamWriter writer,
      Socket socket) {
    try {
      User loggedUser = new User((String) loginRequestMessage.get("nick"), writer, socket);
      users.add(loggedUser);
      sendLoginSuccessMessage(writer);
      sendUserJoinedMessage(loggedUser);
      handleLoggedUser(loggedUser);
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }


  /**
   * Send a message to the client, that login is successful.
   *
   * @param writer output stream to the client
   * @throws JSONException if the message don't have appropriate format
   * @throws IOException   if the output stream is not open
   */
  private void sendLoginSuccessMessage(OutputStreamWriter writer)
      throws JSONException, IOException {
    // { "type" : "login success" }
    JSONObject loginSuccessMessage = new JSONObject();
    loginSuccessMessage.put("type", "login success");
    writer.write(loginSuccessMessage + System.lineSeparator());
    writer.flush();
  }

  /**
   * Send a message to the clients, that user joined the chat.
   *
   * @param loggedUser joined client
   * @throws JSONException if the message don't have appropriate format
   * @throws IOException   if the output stream is not open
   */
  private void sendUserJoinedMessage(User loggedUser) throws JSONException, IOException {
    // { "type" : "user joined", "nick" : "<nick>" }
    JSONObject userJoinedMessage = new JSONObject();
    userJoinedMessage.put("type", "user joined");
    userJoinedMessage.put("nick", loggedUser.getName());
    broadcast(loggedUser, userJoinedMessage);
  }

  /**
   * Send a message to all clients in the chat except the source.
   *
   * @param source  source client
   * @param message message to be sent
   * @throws IOException if the output stream is not open
   */
  private void broadcast(User source, JSONObject message) throws IOException {
    for (User u : users) {
      if (u == source) {
        continue;
      }
      u.getWriter().write(message + System.lineSeparator());
      u.getWriter().flush();
    }
  }

  /**
   * Handles logged client.
   *
   * @param user logged client
   */
  private void handleLoggedUser(User user) {
    try {
      BufferedReader reader = new BufferedReader(
          new InputStreamReader(user.getSocket().getInputStream(), UTF_8));
      while (true) {
        String line = reader.readLine();
        if (line == null) {
          handleDisconnectedUser(user);
          return;
        }
        JSONObject userMessage = new JSONObject(line);
        String type = (String) userMessage.get("type");
        switch (type) {
          case "login" -> throw new IllegalArgumentException("The client is already logged in.");
          case "post message" -> {
            //    { "type" : "post message", "content" : "<message content>" }
            String content = (String) userMessage.get("content");
            sendTextMessage(user, content);
          }
          default -> throw new IllegalArgumentException("Unknown type of message.");
        }
      }
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handles disconnected client.
   *
   * @param user disconnected client
   * @throws IOException   if the output stream is not open
   * @throws JSONException if the message don't have appropriate format
   */
  private void handleDisconnectedUser(User user) throws IOException, JSONException {
    user.getSocket().close();
    users.remove(user);
    sendUserLeftMessage(user);
  }

  /**
   * Send a message to the clients, that user left the chat.
   *
   * @param user disconnected client
   * @throws JSONException if the output stream is not open
   * @throws IOException   if the message don't have appropriate format
   */
  private void sendUserLeftMessage(User user) throws JSONException, IOException {
    // { "type" : "user left", "nick" : "<nick>" }
    JSONObject userLeftMessage = new JSONObject();
    userLeftMessage.put("type", "user left");
    userLeftMessage.put("nick", user.getName());
    broadcast(user, userLeftMessage);
  }

  /**
   * Send a message from the source-client to the other clients in the chat.
   *
   * @param source  source-client
   * @param content message
   * @throws JSONException if the output stream is not open
   * @throws IOException   if the message don't have appropriate format
   */
  private void sendTextMessage(User source, String content) throws JSONException, IOException {
    //            { "type" : "message",
    //              "time" : "<day>.<month>.<year> <hour>:<minute>:<second>",
    //              "nick" : "<sender>",
    //              "content" : "<message content>" }
    JSONObject textMessage = new JSONObject();
    textMessage.put("type", "message");
    textMessage.put("time", new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(
        new Date()));
    textMessage.put("nick", source.getName());
    textMessage.put("content", content);

    broadcast(source, textMessage);
  }

  /**
   * Stop the network-connection.
   */
  public void stop() throws IOException {
    serverSocket.close();
  }
}
