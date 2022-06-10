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
