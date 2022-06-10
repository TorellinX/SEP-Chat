package chat.server;

import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Manages a single connected client at the server.
 */

public class User {

  private final String name;
  private final OutputStreamWriter writer;
  private final Socket socket;

  /**
   * Create a single connected client.
   *
   * @param name   the name of the client
   * @param writer the stream to the client
   * @param socket the socket tp the client
   */
  public User(String name, OutputStreamWriter writer, Socket socket) {
    this.name = name;
    this.socket = socket;
    this.writer = writer;
  }

  public String getName() {
    return name;
  }

  public OutputStreamWriter getWriter() {
    return writer;
  }

  public Socket getSocket() {
    return socket;
  }

}
