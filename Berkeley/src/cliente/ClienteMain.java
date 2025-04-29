package cliente;

import javax.swing.SwingUtilities;

/**
 * Clase principal para iniciar la aplicación del nodo cliente.
 */
public class ClienteMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crear la vista y el controlador
            VistaNodo vista = new VistaNodo();
            ControladorNodo controlador = new ControladorNodo(vista);
            
            // Mostrar la interfaz gráfica
            vista.setVisible(true);
        });
    }
}