import java.io.*;
import java.net.*;
import java.time.*;

public class ClockClient {
  public static void main(String[] args) {
    String hostname = "127.0.0.1";
    int port = 8007;

    try (Socket socket = new Socket(hostname, port)) {
      long requestTime = System.nanoTime();
      InputStream input = socket.getInputStream();
      BufferedReader reader = new BufferedReader(new InputStreamReader(input));
      String serverTimeStr = reader.readLine();
      long responseTime = System.nanoTime();

      Instant actualClientTime = Instant.now();

      System.out.println("Hora devuelta por el servidor: " + serverTimeStr);

      Duration processDelay = Duration.ofNanos(responseTime - requestTime);
      System.out.println("Latencia de ida y vuelta (RTT): " + processDelay.toNanos() / 1_000_000_000.0 + " segundos");

      System.out.println("\nHora actual en el cliente: " + actualClientTime);

      Instant serverTime = Instant.parse(serverTimeStr);
      Instant synchronizedTime = serverTime.plusNanos(processDelay.toNanos() / 2);
      System.out.println("Hora sincronizada del cliente: " + synchronizedTime);

      Duration error = Duration.between(synchronizedTime, actualClientTime);
      System.out.println("Error de sincronización: " + error.toNanos() / 1_000_000_000.0 + " segundos");

    } catch (UnknownHostException ex) {
      System.out.println("Servidor no encontrado: " + ex.getMessage());
    } catch (IOException ex) {
      System.out.println("Error de E/S: " + ex.getMessage());
    }
  }
}
