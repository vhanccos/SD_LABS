import java.io.*;
import java.net.*;
import java.time.Instant;

public class ClockServer {
  public static void main(String[] args) {
    int port = 8007;

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      System.out.println("Socket successfully created");
      System.out.println("Socket is listening...");

      while (true) {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Server connected to " + clientSocket.getInetAddress());

        Instant now = Instant.now();
        String timeString = now.toString();

        OutputStream output = clientSocket.getOutputStream();
        PrintWriter writer = new PrintWriter(output, true);
        writer.println(timeString);

        clientSocket.close();
      }
    } catch (IOException e) {
      System.out.println("Server exception: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
