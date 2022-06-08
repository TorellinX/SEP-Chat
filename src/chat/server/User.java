package chat.server;

import java.io.OutputStreamWriter;
import java.net.Socket;

// TODO: Example of a starter template for managing a single connected client at the server
// Use and/or change this class as needed for your implementation

public class User {

	private String name;
	private OutputStreamWriter writer;
	private Socket socket;

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
