package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Server.java
 * 
 * The main server application logic.
 * Listens on a hardcoded port (5000) and accepts incoming client connections.
 * Maintains a thread-safe list of active ClientHandlers.
 */
public class Server {

    private static final int PORT = 5000;
    // Thread-safe list to store active client handlers
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        System.out.println("Server started on port " + PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Accept new client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Create a new handler for the client
                ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
                clients.add(clientHandler);

                // Start the client handler in a new thread
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Removes a client handler from the active list.
     * @param clientHandler The handler to remove.
     */
    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client disconnected. Active clients: " + clients.size());
    }
}
