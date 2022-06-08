package chat.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatTestClient {

  private static final String ADDRESS = "localhost";
  private static final int PORT = 8080;

  private final Socket socket;
  private final BufferedWriter writer;
  private final BufferedReader reader;

  public ChatTestClient() throws IOException {
    socket = new Socket(ADDRESS, PORT);
    writer = new BufferedWriter(
        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
    reader =
        new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
  }

  public void send(JSONObject message) throws IOException {
    writer.write(message + System.lineSeparator());
    writer.flush();
  }

  public List<JSONObject> receiveAll() throws IOException {
    List<JSONObject> messages = new ArrayList<>();
    while (reader.ready()) {
      messages.add(receive());
    }
    return messages;
  }

  public JSONObject receive() throws IOException {
    try {
      String line = reader.readLine();
      if (line == null || line.isEmpty()) {
        return null;
      }

      return new JSONObject(line);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Unable to parse message as JSON object", e);
    }
  }

  public void close() throws IOException {
    socket.close();
  }
}
