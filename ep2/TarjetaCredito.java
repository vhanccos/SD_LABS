import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TarjetaCredito extends Remote {
    public boolean verificarSaldo(double monto) throws RemoteException;
    public void realizarPago(double monto) throws RemoteException;
    public double consultarSaldo() throws RemoteException;
}
