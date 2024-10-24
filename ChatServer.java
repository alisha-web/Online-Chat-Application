import java.io.*;
import java.net.*;
import java.util.concurrent.*;

// ChatServer class manages connections and communication
public class ChatServer {
    // Store client connections
    private static ConcurrentHashMap<Integer, PrintWriter> clients = new ConcurrentHashMap<>();
    private static int userId = 0;

    public static void main(String[] args) throws IOException {
        // Create a server socket on port 1234
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Chat server started. Waiting for clients...");

        // Continuously accept new client connections
        while (true) {
            Socket clientSocket = serverSocket.accept();
            userId++;  // Increment the user ID for each client
            System.out.println("Client connected: User " + userId);
            // Start a new thread for each client
            new ClientHandler(clientSocket, userId).start();
        }
    }

    // ClientHandler class manages communication with each client
    static class ClientHandler extends Thread {
        private Socket socket;
        private int userId;
        private PrintWriter out;

        public ClientHandler(Socket socket, int userId) {
            this.socket = socket;
            this.userId = userId;
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                clients.put(userId, out);  // Add client to the list

                // Inform all clients about the new connection
                broadcastMessage("User " + userId + " has joined the chat");

                String message;
                // Continuously listen for messages from the client
                while ((message = in.readLine()) != null) {
                    broadcastMessage("User " + userId + ": " + message);
                }
            } catch (IOException e) {
                System.out.println("Error handling client " + userId);
            } finally {
                // When a client disconnects, remove them and inform others
                clients.remove(userId);
                broadcastMessage("User " + userId + " has left the chat");
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Broadcast messages to all connected clients
        private void broadcastMessage(String message) {
            for (PrintWriter client : clients.values()) {
                client.println(message);
            }
        }
    }
}
