import java.io.*;
import java.net.*;

// ChatClient class handles communication with the ChatServer
public class ChatClient {
    public static void main(String[] args) throws IOException {
        // Connect to the server running on localhost, port 1234
        Socket socket = new Socket("localhost", 1234);
        BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Thread to handle receiving messages from the server
        new Thread(() -> {
            String serverMessage;
            try {
                // Continuously read and display messages from the server
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.out.println("Error receiving messages from server.");
            }
        }).start();

        // Main thread for sending messages to the server
        String userInput;
        while ((userInput = userInputReader.readLine()) != null) {
            out.println(userInput);  // Send user input to the server
        }

        // Close the socket when done
        socket.close();
    }
}
