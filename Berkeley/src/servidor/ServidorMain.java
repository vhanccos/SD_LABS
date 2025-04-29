package servidor;

import javax.swing.SwingUtilities;

/**
 * Clase principal para iniciar la aplicación del servidor (coordinador).
 */
public class ServidorMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crear el modelo, la vista y el controlador
            CoordinadorServidor modelo = new CoordinadorServidor();
            VistaCoordinador vista = new VistaCoordinador();
            ControladorCoordinador controlador = new ControladorCoordinador(modelo, vista);
            
            // Mostrar la interfaz gráfica
            vista.setVisible(true);
        });
    }
}