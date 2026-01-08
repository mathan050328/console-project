package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client.java
 * 
 * The main client application logic.
 * Connects to the server and handles sending/receiving messages.
 * Uses two threads: one for sending (main thread) and one for receiving.
 */
public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to the chat server!");

            // Start a separate thread to listen for incoming messages
            new Thread(new IncomingReader(socket)).start();

            // Main thread handles outgoing messages
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                out.println(message);

                if ("/quit".equalsIgnoreCase(message)) {
                    System.out.println("Disconnecting...");
                    break;
                }
            }

            scanner.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inner class to handle incoming messages from the server.
     */
    private static class IncomingReader implements Runnable {
        private Socket socket;
        private BufferedReader in;

        public IncomingReader(Socket socket) {
            this.socket = socket;
            try {
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("Error creating input stream: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                     System.err.println("Error reading from server: " + e.getMessage());
                }
            }
        }
    }
}
