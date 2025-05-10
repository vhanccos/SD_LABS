import java.rmi.Naming;

public class Cliente {

    public static void mostrar_menu(){
        System.out.println("\nSeleccione la operacion que desea realizar:");
        System.out.println("1. Consultar saldo");
        System.out.println("2. Realizar pago");
        System.out.println("3. Salir");
    }

    public static void main(String[] args) {
        boolean fin_programa = false;
        while(!fin_programa){
            try {
                TarjetaCredito tarjeta = (TarjetaCredito) Naming.lookup("rmi://localhost/TarjetaServicio");
                
                int entrada = 0;
                mostrar_menu();
                
                entrada = Integer.parseInt(System.console().readLine());
                switch(entrada){
                    case 1:
                        System.out.println("Saldo inicial: " + tarjeta.consultarSaldo());
                        break;
                    case 2:
                        System.out.println("Ingrese el monto a pagar: ");
                        double monto = Double.parseDouble(System.console().readLine());
                        if (monto <= 0) {
                            System.out.println("Monto invalido.");
                            break;
                        }
                        else if (!tarjeta.verificarSaldo(monto)) {
                            System.out.println("Saldo insuficiente.");
                            break;
                        }
                        tarjeta.realizarPago(monto);
                        System.out.println("Pago de " + monto + " realizado.");
                        break;
                    case 3:
                        System.out.println("Saliendo del programa.");
                        fin_programa = true;
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                fin_programa = true;
            }
        }

    }
}
