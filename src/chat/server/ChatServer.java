package chat.server;

import java.io.IOException;

/**
 * The main class of the chat server. It starts the application to let clients connect themselves to
 * this server, and deals with the distribution of incoming messages accordingly.
 */
public class ChatServer {

  /**
   * Launch the chat server.
   */
  public static void main(String[] args) throws IOException {
    final ServerNetworkConnection connection = new ServerNetworkConnection();
    connection.start();

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          connection.stop();
        } catch (IOException e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }
      }
    });
  }
}
