package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

/**
 * ClientHandler.java
 * 
 * A runnable class to manage communication with a single client.
 * Handles receiving input and sending output for its specific client socket.
 * Broadcasts messages to all other active clients.
 */
public class ClientHandler implements Runnable {

    private Socket socket;
    private List<ClientHandler> clients;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, List<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error creating streams for client: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Prompt for username
            out.println("Enter your username:");
            username = in.readLine();
            if (username == null || username.trim().isEmpty()) {
                username = "Anonymous";
            }
            
            System.out.println("User " + username + " connected.");
            broadcast("Server: " + username + " has joined the chat!", false);

            String message;
            while ((message = in.readLine()) != null) {
                if ("/quit".equalsIgnoreCase(message)) {
                    break;
                }
                broadcast(username + ": " + message, true);
            }

        } catch (IOException e) {
            System.err.println("Error in ClientHandler for " + username + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Broadcasts a message to all other active clients.
     * @param message The message to broadcast.
     * @param excludeSelf Whether to exclude the sender from receiving the message.
     */
    private void broadcast(String message, boolean excludeSelf) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (excludeSelf && client == this) {
                    continue;
                }
                client.sendMessage(message);
            }
        }
    }

    /**
     * Sends a message to this specific client.
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Cleans up resources and removes this handler from the server's list.
     */
    private void cleanup() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
        
        // Remove from the list and notify others
        Server.removeClient(this);
        broadcast("Server: " + username + " has left the chat.", true);
    }
}
