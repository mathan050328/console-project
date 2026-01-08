<<<<<<< HEAD
# Java Console LAN Chat Application

A multi-client, multi-threaded console-based LAN Chat Application.

## Architectural Overview

The application follows a strict **Client-Server Architecture** using TCP/IP sockets.

1.  **Server (`Server.java`)**:
    *   Acts as the central hub.
    *   Listens on port `5000` for incoming connections.
    *   Maintains a thread-safe list of active clients.
    *   Spawns a dedicated `ClientHandler` thread for each connected client to ensure the main server thread remains free to accept new connections.

2.  **Client Handler (`ClientHandler.java`)**:
    *   A `Runnable` task running on the server side, dedicated to a single client.
    *   Handles reading messages from its specific client and broadcasting them to all other clients via the Server's central list.
    *   Manages client disconnection and resource cleanup.

3.  **Client (`Client.java`)**:
    *   The end-user application.
    *   Connects to the server.
    *   Uses **two separate threads** to allow simultaneous sending and receiving:
        *   **Main Thread**: Reads user input from the console and sends it to the server.
        *   **IncomingReader Thread**: Continuously listens for messages from the server and prints them to the console.

## Concurrency and Synchronization

*   **Server-Side Concurrency**: The server uses a thread-per-client model. This ensures that one slow or idle client does not block the server from processing messages from other clients or accepting new connections.
*   **Synchronization**: The list of active clients (`List<ClientHandler> clients`) is a shared resource accessed by multiple threads (each `ClientHandler` needs to iterate over it to broadcast messages). To prevent race conditions (e.g., iterating while another thread adds/removes a client), we use `Collections.synchronizedList` and synchronize on the list object during iteration in the `broadcast` method.

## How to Run

### Prerequisites
*   Java Development Kit (JDK) installed (Java 8 or higher).

### Compilation
Open a terminal in the project root directory and run:

```bash
mkdir -p bin
javac -d bin src/chat/*.java
```

### Running the Server
In a terminal window, run:

```bash
java -cp bin chat.Server
```

### Running Clients
Open multiple new terminal windows (one for each client) and run:

```bash
java -cp bin chat.Client
```

### Usage
1.  The client will connect to the server.
2.  Enter a username when prompted.
3.  Type messages and press Enter to send.
4.  Type `/quit` to disconnect.
=======
# console-project
>>>>>>> 45849a3b092088c21f7d15ffbd816904f18a07bb
