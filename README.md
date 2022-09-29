# SEP Task "Chat"

This task is about implementing a network-based chat program where any number of clients can connect to a chat server. The communication between client and server is to be done via sockets using JSON (see http://www.json.org).

## The Message Format
Each of the JSON messages must be separated by a single newline character (\n). The messages need to be encoded in UTF-8. The general format of an individual messages is

```
{ "type" : "<message-type>", <further fields> }
```

Names that are surrounded by angle brackets represent placeholder fields. The messages are described below with the corresponding functionality.

The server shall listen for new incoming connection requests on port 8080. Once a client has established a socket connection, it must next send a login message for identification purposes. The message must contain a nickname and needs to be formatted as JSON message. In detail, the message must be of the following form:

```
{ "type" : "login", "nick" : "<nick>" }
```

The server responds to this message with `{ "type" : "login success" }` if the nickname has not yet been assigned, and otherwise with `{ "type" : "login failed" }`. If the login of a client is successful, the server sends to all other connected clients the message `{ "type" : "user joined", "nick" : "<nick>" }` to inform that a new participant has joined.

The Swing-based user interface of a client must consist of two parts after a successful login: an output window and an input window that is located below. The height of the input window should be three text lines, and its width should occupy the full width of the window. The output window is best implemented with a JList, and should occupy the complete remaining space that is available. It must however be at least 150 pixels wide and 300 pixels high. The output window displays all sent messages, which includes both messages sent by the own user as well as messages sent from other clients. The output must be updated regardless of whether the user is currently writing a message or not. The output window should be able to store and display the last hundred entries.

As soon as the user presses the Enter key in his input window, the message should appear in all clients, and the contents of the own input window should be deleted. The display format for the chat messages is thereby as follows:
```
<nick> (<day>.<month>.<year> <hour>:<minute>): <message content>
```
 

Example: The year must be specified with four digits. An example chat could look like as follows:

```
  Thomas (23.05.2022 15:01): Hey
  Henrik (23.05.2022 15:02): Hi, already talked to students today?
  ```
    
The content of the JSON message sent from client to server should be { "type" : "post message", "content" : "<message content>" } In response, the server sends the following JSON message to all clients except the original sender (where the year must also be specified with four digits here):

```
  { "type" : "message",
    "time" : "<day>.<month>.<year> <hour>:<minute>:<second>",
    "nick" : "<sender>",
    "content" : "<message content>"
  }
  ```
    
The output window must additionally display status messages of the chat. When successfully joining the chat, the own output window should output the line Chat joined as <nick>. All other clients must - in response to the user joined JSON message - display the line <nick> has joined the chat.

By closing the chat window, the network connection between the client and the server must be terminated gracefully. To this end the client should close the socket connection to the server, and the server should respond with the following two action items: First, a JSON message `{ "type" : "user left", "nick" : "<nick>" }` is sent to all clients that are still connected to the server. In the output windows of all remaining clients, the line <nick> has left the chat. shall be print in response. Second, the server should accept the freed nickname again when a new client logs in. Below is an example how the content of the chat output window could look like:

```
  Chat joined as Thomas.
  Henrik has joined the chat.
  Thomas (23.05.2022 15:01): Hey
  Henrik (23.05.2022 15:02): Hi, already talked to students today?
  Henrik has left the chat.
  ```

The implementation must follow the Model-View-Controller (MVC) pattern.

2022, SoSy-Lab
