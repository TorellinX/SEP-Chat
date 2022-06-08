package chat.server;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ChatServerIntegrationTest {

  private ServerNetworkConnection serverConnection;

  private ChatTestClient client;

  @BeforeEach
  public void setUp() throws IOException {
    serverConnection = new ServerNetworkConnection();
    serverConnection.start();

    client = new ChatTestClient();
  }

  @AfterEach
  public void tearDown() throws IOException {
    serverConnection.stop();
    client.close();
  }

  @Test
  public void handleMessage_whenLogin_answersSuccess() throws IOException {
    client.send(JsonMessage.login("SomeUser"));

    JSONObject message = client.receive();

    Assertions.assertEquals(JsonMessage.LOGIN_SUCCESS, JsonMessage.typeOf(message));
  }

  @Test
  public void handleMessage_whenLoginTaken_answersFailure()
      throws IOException, InterruptedException {
    ChatTestClient otherClient = new ChatTestClient();
    try {
      otherClient.send(JsonMessage.login("SomeUser"));

      // wait to ensure ordering of message processing.
      Thread.sleep(100);

      client.send(JsonMessage.login("SomeUser"));

      List<JSONObject> messages = receiveAllClientMessages();

      Assertions.assertFalse(hasMessageOfType(JsonMessage.LOGIN_SUCCESS, messages));
      Assertions.assertTrue(hasMessageOfType(JsonMessage.LOGIN_FAILED, messages));
    } finally {
      otherClient.close();
    }
  }

  @Test
  public void handleMessage_whenLoginReleased_answersSuccess()
      throws IOException, InterruptedException {
    ChatTestClient otherClient = new ChatTestClient();
    try {
      otherClient.send(JsonMessage.login("SomeUser"));
      otherClient.receive();
      otherClient.close();

      // wait to ensure ordering of message processing.
      Thread.sleep(100);
      client.send(JsonMessage.login("SomeUser"));

      List<JSONObject> messages = receiveAllClientMessages();

      Assertions.assertFalse(hasMessageOfType(JsonMessage.LOGIN_FAILED, messages));
      Assertions.assertTrue(hasMessageOfType(JsonMessage.LOGIN_SUCCESS, messages));
    } finally {
      otherClient.close();
    }
  }

  @Test
  public void handleMessage_whenMessage_broadcasts() throws IOException, InterruptedException {
    ChatTestClient otherClient = new ChatTestClient();
    try {
      client.send(JsonMessage.login("SomeUser"));
      otherClient.send(JsonMessage.login("AnotherUser"));
      otherClient.send(JsonMessage.postMessage("Hi!"));

      List<JSONObject> messages = receiveAllClientMessages();

      Assertions.assertTrue(hasMessageOfType(JsonMessage.MESSAGE, messages));

      JSONObject message = getMessageOfType(JsonMessage.MESSAGE, messages);

      Assertions.assertNotNull(message);

      Assertions.assertEquals("AnotherUser", JsonMessage.getNickname(message));
      Assertions.assertEquals("Hi!", JsonMessage.getContent(message));
    } finally {
      otherClient.close();
    }
  }

  @Test
  public void handleMessage_whenLoggedIn_broadcasts() throws IOException {
    ChatTestClient otherClient = new ChatTestClient();
    try {
      otherClient.send(JsonMessage.login("SomeUser"));

      JSONObject message = client.receive();

      Assertions.assertEquals(JsonMessage.USER_JOINED, JsonMessage.typeOf(message));
      Assertions.assertEquals("SomeUser", JsonMessage.getNickname(message));
    } finally {
      otherClient.close();
    }
  }

  @Test
  public void handleMessage_whenLoggedInAndDisconnect_broadcasts()
      throws IOException, InterruptedException {
    ChatTestClient otherClient = new ChatTestClient();
    try {
      otherClient.send(JsonMessage.login("SomeUser"));
      otherClient.close();

      List<JSONObject> messages = receiveAllClientMessages();

      Assertions.assertTrue(hasMessageOfType(JsonMessage.USER_LEFT, messages));
      JSONObject message = getMessageOfType(JsonMessage.USER_LEFT, messages);

      Assertions.assertNotNull(message);

      Assertions.assertEquals("SomeUser", JsonMessage.getNickname(message));
    } finally {
      otherClient.close();
    }

  }

  private List<JSONObject> receiveAllClientMessages() throws InterruptedException, IOException {
    // wait until the server has processed all messages.
    Thread.sleep(200);
    // get all messages.
    List<JSONObject> messages = client.receiveAll();
    Assertions.assertFalse(messages.isEmpty());
    return messages;
  }

  public boolean hasMessageOfType(JsonMessage type, List<JSONObject> messages) {
    return getMessageOfType(type, messages) != null;
  }

  private JSONObject getMessageOfType(JsonMessage type, List<JSONObject> messages) {
    Optional<JSONObject> opt = messages.stream().filter(x -> type == JsonMessage.typeOf(x)).findFirst();
    return opt.orElse(null);
  }
}
