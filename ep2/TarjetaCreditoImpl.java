import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

public class TarjetaCreditoImpl extends UnicastRemoteObject implements TarjetaCredito {
    private double saldo;

    public TarjetaCreditoImpl(double saldoInicial) throws RemoteException {
        this.saldo = saldoInicial;
    }

    public boolean verificarSaldo(double monto) throws RemoteException {
        return saldo >= monto;
    }

    public void realizarPago(double monto) throws RemoteException {
        if (saldo >= monto) {
            saldo -= monto;
        }
    }

    public double consultarSaldo() throws RemoteException {
        return saldo;
    }
}
