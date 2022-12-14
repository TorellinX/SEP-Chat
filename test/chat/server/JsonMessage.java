package chat.server;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Test-messages class.
 */
public enum JsonMessage {

  LOGIN("login"), LOGIN_SUCCESS("login success"), LOGIN_FAILED("login failed"),
  USER_JOINED("user joined"), POST_MESSAGE("post message"), MESSAGE("message"),
  USER_LEFT("user left");

  public static final String TYPE_FIELD = "type";

  public static final String NICK_FIELD = "nick";

  public static final String CONTENT_FIELD = "content";

  public static final String TIME_FIELD = "time";

  private final String jsonName;

  JsonMessage(String jsonName) {
    this.jsonName = jsonName;
  }

  /**
   * Defines a type of the message.
   *
   * @param message JSONobject
   * @return type of the message
   */
  public static JsonMessage typeOf(JSONObject message) {
    String typeName;
    try {
      typeName = message.getString(TYPE_FIELD);
    } catch (JSONException e) {
      throw new IllegalArgumentException(String.format("Unknown message type '%s'", message), e);
    }

    Optional<JsonMessage> opt =
        Arrays.stream(JsonMessage.values()).filter(x -> x.getJsonName().equals(typeName))
            .findFirst();
    return opt.orElseThrow(
        () -> new IllegalArgumentException(String.format("Unknown message type '%s'", typeName)));
  }

  /**
   * Adds a login-message to the list of messages.
   *
   * @param nickname of test client
   * @return JSON message of LOGIN type
   */
  public static JSONObject login(String nickname) {
    try {
      return createMessageOfType(LOGIN).put(NICK_FIELD, nickname);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to create a json object.", e);
    }
  }

  /**
   * Adds a post message to the list of messages.
   *
   * @param content of the message
   * @return JSON message
   */
  public static JSONObject postMessage(String content) {
    try {
      JSONObject message = createMessageOfType(POST_MESSAGE);
      message.put(CONTENT_FIELD, content);

      return message;
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to create a json object.", e);
    }
  }

  private static JSONObject createMessageOfType(JsonMessage type) throws JSONException {
    return new JSONObject().put(TYPE_FIELD, type.getJsonName());
  }

  /**
   * Get nickname from the message.
   *
   * @param object JSON message
   * @return nickname
   */
  public static String getNickname(JSONObject object) {
    try {
      return object.getString(NICK_FIELD);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to read a json object.", e);
    }
  }

  /**
   * Get content from the message.
   *
   * @param object JSON message
   * @return content
   */
  public static String getContent(JSONObject object) {
    try {
      return object.getString(CONTENT_FIELD);
    } catch (JSONException e) {
      throw new IllegalArgumentException("Failed to read a json object.", e);
    }
  }

  private static String convertDateToString(Date date) {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY)
        .format(date);
  }

  private static Date convertStringToDate(String date) throws ParseException {
    return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.GERMANY)
        .parse(date);
  }

  public String getJsonName() {
    return jsonName;
  }
}
