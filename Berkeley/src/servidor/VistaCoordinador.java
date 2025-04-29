package servidor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Interfaz gráfica para el coordinador del algoritmo de Berkeley.
 */
public class VistaCoordinador extends JFrame {
    private JLabel etiquetaReloj;
    private JTextArea areaLog;
    private JButton botonIniciar;
    private JButton botonDetener;
    private JButton botonSincronizar;
    private JList<String> listaNodos;
    private DefaultListModel<String> modeloListaNodos;
    
    private DateTimeFormatter formateadorHora = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public VistaCoordinador() {
        configurarVentana();
        inicializarComponentes();
        colocarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Coordinador - Algoritmo de Berkeley");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void inicializarComponentes() {
        // Panel superior con el reloj
        etiquetaReloj = new JLabel("00:00:00", SwingConstants.CENTER);
        etiquetaReloj.setFont(new Font("Arial", Font.BOLD, 48));
        etiquetaReloj.setOpaque(true);
        etiquetaReloj.setBackground(Color.LIGHT_GRAY);
        etiquetaReloj.setForeground(Color.BLACK);
        
        // Área de logs
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        // Lista de nodos conectados
        modeloListaNodos = new DefaultListModel<>();
        listaNodos = new JList<>(modeloListaNodos);
        listaNodos.setBorder(BorderFactory.createTitledBorder("Nodos Conectados"));
        
        // Botones
        botonIniciar = new JButton("Iniciar Servidor");
        botonDetener = new JButton("Detener Servidor");
        botonSincronizar = new JButton("Sincronizar Relojes");
        
        // Estado inicial
        botonDetener.setEnabled(false);
        botonSincronizar.setEnabled(false);
    }
    
    private void colocarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(new JLabel("Reloj del Coordinador:", SwingConstants.CENTER), BorderLayout.NORTH);
        panelSuperior.add(etiquetaReloj, BorderLayout.CENTER);
        
        // Panel lateral con lista de nodos
        JScrollPane scrollNodos = new JScrollPane(listaNodos);
        scrollNodos.setPreferredSize(new Dimension(200, 0));
        
        // Panel central con logs
        JScrollPane scrollLog = new JScrollPane(areaLog);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.add(botonIniciar);
        panelBotones.add(botonDetener);
        panelBotones.add(botonSincronizar);
        
        // Añadir paneles a la ventana
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollNodos, BorderLayout.WEST);
        add(scrollLog, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
        
        // Añadir márgenes
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    // Métodos para actualizar la interfaz
    
    public void actualizarReloj(LocalTime hora, boolean sincronizado) {
        SwingUtilities.invokeLater(() -> {
            etiquetaReloj.setText(hora.format(formateadorHora));
            
            if (sincronizado) {
                etiquetaReloj.setBackground(Color.GREEN);
                etiquetaReloj.setForeground(Color.BLACK);
            } else {
                etiquetaReloj.setBackground(Color.RED);
                etiquetaReloj.setForeground(Color.WHITE);
            }
        });
    }
    
    public void actualizarNodos(List<String> nodos) {
        SwingUtilities.invokeLater(() -> {
            modeloListaNodos.clear();
            for (String nodo : nodos) {
                modeloListaNodos.addElement(nodo);
            }
        });
    }
    
    public void agregarLog(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            areaLog.append(mensaje + "\n");
            // Auto-scroll al final
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }
    
    public void servidorIniciado(boolean iniciado) {
        SwingUtilities.invokeLater(() -> {
            botonIniciar.setEnabled(!iniciado);
            botonDetener.setEnabled(iniciado);
            botonSincronizar.setEnabled(iniciado);
        });
    }
    
    // Métodos para establecer listeners
    
    public void setIniciarListener(ActionListener listener) {
        botonIniciar.addActionListener(listener);
    }
    
    public void setDetenerListener(ActionListener listener) {
        botonDetener.addActionListener(listener);
    }
    
    public void setSincronizarListener(ActionListener listener) {
        botonSincronizar.addActionListener(listener);
    }
}