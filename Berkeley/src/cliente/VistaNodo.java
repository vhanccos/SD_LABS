package cliente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Interfaz gráfica para el nodo cliente.
 */
public class VistaNodo extends JFrame {
    private JLabel etiquetaReloj;
    private JTextArea areaLog;
    private JButton botonConectar;
    private JButton botonDesconectar;
    private JButton botonSincronizar;
    private JTextField campoNombreNodo;
    private JLabel etiquetaEstado;
    
    private DateTimeFormatter formateadorHora = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    public VistaNodo() {
        configurarVentana();
        inicializarComponentes();
        colocarComponentes();
    }
    
    private void configurarVentana() {
        setTitle("Nodo Cliente - Algoritmo de Berkeley");
        setSize(600, 500);
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
        
        // Campo para el nombre del nodo
        campoNombreNodo = new JTextField(10);
        campoNombreNodo.setText("Nodo" + (int)(Math.random() * 1000));
        
        // Etiqueta de estado
        etiquetaEstado = new JLabel("Desconectado", SwingConstants.CENTER);
        etiquetaEstado.setOpaque(true);
        etiquetaEstado.setBackground(Color.RED);
        etiquetaEstado.setForeground(Color.WHITE);
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Botones
        botonConectar = new JButton("Conectar");
        botonDesconectar = new JButton("Desconectar");
        botonSincronizar = new JButton("Solicitar Sincronización");
        
        // Estado inicial
        botonDesconectar.setEnabled(false);
        botonSincronizar.setEnabled(false);
    }
    
    private void colocarComponentes() {
        setLayout(new BorderLayout(10, 10));
        
        // Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.add(new JLabel("Reloj del Nodo:", SwingConstants.CENTER), BorderLayout.NORTH);
        panelSuperior.add(etiquetaReloj, BorderLayout.CENTER);
        
        // Panel para nombre del nodo
        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNombre.add(new JLabel("Nombre del nodo:"));
        panelNombre.add(campoNombreNodo);
        panelNombre.add(etiquetaEstado);
        
        // Combinamos el panel del reloj y el panel del nombre
        JPanel panelCabecera = new JPanel(new BorderLayout());
        panelCabecera.add(panelSuperior, BorderLayout.CENTER);
        panelCabecera.add(panelNombre, BorderLayout.SOUTH);
        
        // Panel central con logs
        JScrollPane scrollLog = new JScrollPane(areaLog);
        
        // Panel inferior con botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.add(botonConectar);
        panelBotones.add(botonDesconectar);
        panelBotones.add(botonSincronizar);
        
        // Añadir paneles a la ventana
        add(panelCabecera, BorderLayout.NORTH);
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
                etiquetaReloj.setBackground(Color.YELLOW);
                etiquetaReloj.setForeground(Color.BLACK);
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
    
    public void clienteConectado(boolean conectado) {
        SwingUtilities.invokeLater(() -> {
            botonConectar.setEnabled(!conectado);
            botonDesconectar.setEnabled(conectado);
            botonSincronizar.setEnabled(conectado);
            campoNombreNodo.setEditable(!conectado);
            
            if (conectado) {
                etiquetaEstado.setText("Conectado");
                etiquetaEstado.setBackground(Color.GREEN);
            } else {
                etiquetaEstado.setText("Desconectado");
                etiquetaEstado.setBackground(Color.RED);
            }
        });
    }
    
    // Métodos para establecer listeners
    
    public void setConectarListener(ActionListener listener) {
        botonConectar.addActionListener(listener);
    }
    
    public void setDesconectarListener(ActionListener listener) {
        botonDesconectar.addActionListener(listener);
    }
    
    public void setSincronizarListener(ActionListener listener) {
        botonSincronizar.addActionListener(listener);
    }
    
    // Getter para el nombre del nodo
    public String getNombreNodo() {
        return campoNombreNodo.getText();
    }
}