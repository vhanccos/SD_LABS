package cliente;

import java.time.LocalTime;

/**
 * Controlador que conecta el modelo y la vista del nodo cliente.
 */
public class ControladorNodo implements NodoCliente.EventoNodoListener {
    private NodoCliente modelo;
    private VistaNodo vista;
    
    public ControladorNodo(VistaNodo vista) {
        this.vista = vista;
        
        // Configurar listeners para los botones de la vista
        vista.setConectarListener(e -> conectarNodo());
        vista.setDesconectarListener(e -> desconectarNodo());
        vista.setSincronizarListener(e -> solicitarSincronizacion());
    }
    
    private void conectarNodo() {
        String nombreNodo = vista.getNombreNodo();
        
        if (nombreNodo == null || nombreNodo.trim().isEmpty()) {
            vista.agregarLog("Error: El nombre del nodo no puede estar vacío");
            return;
        }
        
        // Crear el modelo del nodo con el nombre especificado
        modelo = new NodoCliente(nombreNodo);
        modelo.addListener(this);
        
        // Iniciar conexión en un hilo separado
        new Thread(() -> {
            modelo.conectar();
            vista.clienteConectado(modelo.estaConectado());
        }).start();
    }
    
    private void desconectarNodo() {
        if (modelo != null) {
            modelo.desconectar();
            vista.clienteConectado(false);
        }
    }
    
    private void solicitarSincronizacion() {
        if (modelo != null && modelo.estaConectado()) {
            modelo.solicitarSincronizacion();
        }
    }
    
    // Implementación de los métodos de la interfaz EventoNodoListener
    
    @Override
    public void onEventoNodo(String mensaje) {
        vista.agregarLog(mensaje);
    }
    
    @Override
    public void onActualizacionReloj(LocalTime hora, boolean sincronizado) {
        vista.actualizarReloj(hora, sincronizado);
    }
}