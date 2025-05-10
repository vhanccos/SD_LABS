import java.rmi.Naming;

public class Servidor {
    public static void main(String[] args) {
        try {
            TarjetaCredito tarjeta = new TarjetaCreditoImpl(1000.0); // saldo inicial
            Naming.rebind("rmi://localhost/TarjetaServicio", tarjeta);
            System.out.println("Servidor RMI listo.");
        } catch (Exception e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }
}
