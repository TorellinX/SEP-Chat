package chat.server;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The network layer of the chat server. Takes care of processing both the connection requests and
 * message handling.
 */
public class ServerNetworkConnection {

  // TODO: insert code here
  private final ServerSocket serverSocket;
  private static final int PORT = 8080;
  BufferedReader reader;
  OutputStreamWriter writer;
  List<User> users;

  /**
   * TODO: insert JavaDoc
   */
  public ServerNetworkConnection() throws IOException{
    // TODO: insert code here
    this.serverSocket = new ServerSocket(PORT);
    this.users = new ArrayList<>();

  }

  /**
   * Start the network-connection such that clients can establish a connection to this server.
   */
  public void start() throws IOException {
    // TODO: insert code here
      while (true){
        System.out.println("Server is waiting for connections...");
        try{
          Socket socket = serverSocket.accept();
          writer = new OutputStreamWriter(socket.getOutputStream(), UTF_8);
          reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), UTF_8));
          System.out.println("Test socket 1: " + socket);

          Thread thread = new Thread() {
            @Override
            public void run() {
              System.out.println("Server: A client is connected to the server.");
              // обработка клиентов
              try {
                String line = reader.readLine();
                System.out.println("Server: A message from the client: " + line);
                JSONObject tokens = new JSONObject(line);
                String type = (String) tokens.get("type");

                switch (type){
                  case "login":
                    handleLogin(tokens, writer, socket);
                    break;
                  case "post message":
                    // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    break;
                }


                //socket.close(); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
              } catch (IOException | JSONException e) {
                e.printStackTrace();
              }
            }
          };
          thread.setDaemon(true);
          thread.start();

        } catch (IOException
                // | JSONException
            e) {
          e.printStackTrace();
        }
      }
  }

  private void handleLogin(JSONObject tokens, OutputStreamWriter writer, Socket socket) throws JSONException {
    // { "type" : "login", "nick" : "<nick>" }
    if(tokens.get("nick").equals("")) {
      return;
    }
    for (User user : users) {
      if(user.getName().equals(tokens.get("nick"))){
        sendLoginFailed();
        return;
      }
    }
    handleLoggedIn(tokens, writer, socket);

  }

  private void sendLoginFailed() {
    // { "type" : "login failed" }
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("type", "login failed");

      writer.write(jsonObject + System.lineSeparator());
      writer.flush();

      System.out.println("Sent login-failed-message to client: " + jsonObject);
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }

  private void   handleLoggedIn(JSONObject tokens, OutputStreamWriter writer, Socket socket)
      throws JSONException {
    // { "type" : "login success" }
    // { "type" : "user joined", "nick" : "<nick>" }

    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("type", "login success");

      writer.write(jsonObject + System.lineSeparator());
      writer.flush();

      System.out.println("Sent login-success-message to client: " + jsonObject);

      JSONObject userJoinedMessage = new JSONObject();
      userJoinedMessage.put("type", "user joined");
      userJoinedMessage.put("nick", tokens.get("nick"));

      for(User user : users){
        user.getWriter().write(userJoinedMessage + System.lineSeparator());
        user.getWriter().flush();
        System.out.println("Sent userJoined-Message  " + jsonObject + " to client: " + user.getName());
      }
      users.add(new User((String) tokens.get("nick"), writer, socket));
      System.out.println("Users: " + users.toString());
    } catch (IOException | JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * Stop the network-connection.
   */
  public void stop() throws IOException {
    // TODO insert code here
    serverSocket.close();
    System.out.println("Server is stopped");

  }
}
