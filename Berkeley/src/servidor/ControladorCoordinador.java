package servidor;


import java.time.LocalTime;
import java.util.List;

/**
 * Controlador que conecta el modelo y la vista del coordinador.
 */
public class ControladorCoordinador implements CoordinadorServidor.EventoCoordinadorListener {
    private CoordinadorServidor modelo;
    private VistaCoordinador vista;
    
    public ControladorCoordinador(CoordinadorServidor modelo, VistaCoordinador vista) {
        this.modelo = modelo;
        this.vista = vista;
        
        // Registrar este controlador como listener del modelo
        modelo.addListener(this);
        
        // Configurar listeners para los botones de la vista
        vista.setIniciarListener(e -> iniciarServidor());
        vista.setDetenerListener(e -> detenerServidor());
        vista.setSincronizarListener(e -> sincronizarRelojes());
    }
    
    private void iniciarServidor() {
        new Thread(() -> {
            modelo.iniciar();
            vista.servidorIniciado(true);
        }).start();
    }
    
    private void detenerServidor() {
        modelo.detener();
        vista.servidorIniciado(false);
    }
    
    private void sincronizarRelojes() {
        new Thread(() -> modelo.sincronizarRelojes()).start();
    }
    
    // Implementación de los métodos de la interfaz EventoCoordinadorListener
    
    @Override
    public void onEventoServidor(String mensaje) {
        vista.agregarLog(mensaje);
    }
    
    @Override
    public void onActualizacionReloj(LocalTime hora, boolean sincronizado) {
        vista.actualizarReloj(hora, sincronizado);
    }
    
    @Override
    public void onCambioNodos(List<String> nodos) {
        vista.actualizarNodos(nodos);
    }
}