import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) {
        final String serverAddress = "localhost";
        final int serverPort = 8080;
        System.out.println("Connecté au serveur " + serverAddress + ":" + serverPort);


        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            System.out.println("Connecté au serveur.");

            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            String userInput;

            while ((userInput = consoleInput.readLine()) != null) {
                out.println(userInput);
                String response = in.readLine();
                if (response == null) {
                    System.out.println("Connexion terminée par le serveur.");
                    break;
                }
                System.out.println("Réponse du serveur : " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


