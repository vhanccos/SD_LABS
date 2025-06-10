/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab08resuelta;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

// Clase principal que contiene toda la funcionalidad
class NavegadorCategorias extends JFrame {
    // Componentes de interfaz
    JLabel LblId;
    JLabel LblNombre;
    JLabel LblDescripcion;
    JTextField TxtId;
    JTextField TxtNombre;
    JTextField TxtDescripcion;
    JButton BtnPrimero;
    JButton BtnSiguiente;
    JButton BtnAnterior;
    JButton BtnUltimo;
    
    // Variables de conexión y datos
    ResultSet resultado;
    Connection conexion;
    Statement sentencia;
    
    public NavegadorCategorias(String titulo) {
        super(titulo);
        initComponents();
        setupLayout();
        setupEvents();
        conectarBaseDatos();
    }
    
    private void initComponents() {
        // Inicializar labels
        LblId = new JLabel("ID Categoría:");
        LblNombre = new JLabel("Nombre:");
        LblDescripcion = new JLabel("Descripción:");
        
        // Configurar labels
        LblId.setPreferredSize(new Dimension(100, 25));
        LblNombre.setPreferredSize(new Dimension(100, 25));
        LblDescripcion.setPreferredSize(new Dimension(100, 25));
        
        // Inicializar campos de texto
        TxtId = new JTextField(15);
        TxtNombre = new JTextField(15);
        TxtDescripcion = new JTextField(15);
        
        // Hacer el ID de solo lectura
        TxtId.setEditable(false);
        TxtId.setBackground(Color.LIGHT_GRAY);
        
        // Inicializar botones
        BtnPrimero = new JButton("Primero");
        BtnAnterior = new JButton("Anterior");
        BtnSiguiente = new JButton("Siguiente");
        BtnUltimo = new JButton("Último");
        
        // Configurar botones
        Dimension btnSize = new Dimension(100, 30);
        BtnPrimero.setPreferredSize(btnSize);
        BtnAnterior.setPreferredSize(btnSize);
        BtnSiguiente.setPreferredSize(btnSize);
        BtnUltimo.setPreferredSize(btnSize);
    }
    
    private void setupLayout() {
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelPrincipal.setBackground(new Color(240, 240, 255));
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(new Color(240, 240, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Agregar componentes al formulario
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(LblId, gbc);
        gbc.gridx = 1;
        panelFormulario.add(TxtId, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelFormulario.add(LblNombre, gbc);
        gbc.gridx = 1;
        panelFormulario.add(TxtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(LblDescripcion, gbc);
        gbc.gridx = 1;
        panelFormulario.add(TxtDescripcion, gbc);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(new Color(255, 200, 100));
        panelBotones.add(BtnPrimero);
        panelBotones.add(BtnAnterior);
        panelBotones.add(BtnSiguiente);
        panelBotones.add(BtnUltimo);
        
        // Panel de información
        JPanel panelInfo = new JPanel(new FlowLayout());
        panelInfo.setBackground(new Color(240, 240, 255));
        JLabel lblInfo = new JLabel("Navegación de Categorías - Base de Datos: empresamsql");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblInfo.setForeground(new Color(50, 50, 150));
        panelInfo.add(lblInfo);
        
        // Agregar paneles al panel principal
        panelPrincipal.add(panelInfo, BorderLayout.NORTH);
        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);
        
        getContentPane().add(panelPrincipal);
    }
    
    private void setupEvents() {
        // Crear instancia del manejador de eventos
        EventoBoton manejador = new EventoBoton(this);
        
        // Asignar eventos a botones
        BtnPrimero.addActionListener(manejador);
        BtnAnterior.addActionListener(manejador);
        BtnSiguiente.addActionListener(manejador);
        BtnUltimo.addActionListener(manejador);
        
        // Evento de ventana
        addWindowListener(new EventosVentana(this));
        
        // Eventos de teclado para navegación
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_HOME:
                        IrPrimero();
                        break;
                    case KeyEvent.VK_END:
                        IrUltimo();
                        break;
                    case KeyEvent.VK_LEFT:
                        IrAnterior();
                        break;
                    case KeyEvent.VK_RIGHT:
                        IrSiguiente();
                        break;
                }
            }
        });
        
        setFocusable(true);
    }
    
    private void conectarBaseDatos() {
        try {
            // Cargar driver MySQL moderno
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer conexión (ajusta los parámetros según tu configuración)
            String url = "jdbc:mysql://localhost:3306/empresamsql?useTimezone=true&serverTimezone=UTC&useSSL=false";
            String usuario = "root";
            String password = "Frandiego20224232";
            
            conexion = DriverManager.getConnection(url, usuario, password);
            
            // Crear statement con capacidad de desplazamiento
            sentencia = conexion.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY
            );
            
            // Ejecutar consulta
            boolean tieneResultados = sentencia.execute("SELECT * FROM categorias ORDER BY IDCategoria");
            
            if (tieneResultados) {
                resultado = sentencia.getResultSet();
                if (resultado != null && resultado.next()) {
                    // Mostrar el primer registro
                    mostrarRegistroActual();
                    resultado.first(); // Volver al primer registro
                } else {
                    mostrarMensajeError("No se encontraron registros en la tabla categorías.");
                }
            }
            
        } catch (ClassNotFoundException e) {
            mostrarMensajeError("Error: Driver MySQL no encontrado.\nAsegúrese de tener mysql-connector-java en el classpath.\nDetalle: " + e.getMessage());
        } catch (SQLException e) {
            mostrarMensajeError("Error de SQL: " + e.getMessage() + "\nCódigo: " + e.getErrorCode());
        } catch (Exception e) {
            mostrarMensajeError("Error inesperado: " + e.getMessage());
        }
    }
    
    private void mostrarRegistroActual() {
        try {
            if (resultado != null && !resultado.isAfterLast() && !resultado.isBeforeFirst()) {
                // Obtener valores del registro actual
                String id = resultado.getString("IDCategoria");
                String nombre = resultado.getString("Nombre");
                String descripcion = resultado.getString("Descripcion");
                
                // Mostrar en los campos de texto (manejar valores null)
                TxtId.setText(id != null ? id : "");
                TxtNombre.setText(nombre != null ? nombre : "");
                TxtDescripcion.setText(descripcion != null ? descripcion : "");
                
                // Actualizar título de ventana con información de posición
                int posicion = resultado.getRow();
                resultado.last();
                int total = resultado.getRow();
                resultado.absolute(posicion); // Volver a la posición original
                
                setTitle("Navegación de Categorías - Registro " + posicion + " de " + total);
            }
        } catch (SQLException e) {
            mostrarMensajeError("Error al mostrar registro: " + e.getMessage());
        }
    }
    
    public void IrPrimero() {
        try {
            if (resultado != null && resultado.first()) {
                mostrarRegistroActual();
                System.out.println("Navegado al primer registro");
            }
        } catch (SQLException e) {
            mostrarMensajeError("Error al ir al primer registro: " + e.getMessage());
        }
    }
    
    public void IrAnterior() {
        try {
            if (resultado != null) {
                if (!resultado.previous()) {
                    // Si no hay registro anterior, ir al primero
                    resultado.first();
                }
                mostrarRegistroActual();
                System.out.println("Navegado al registro anterior");
            }
        } catch (SQLException e) {
            mostrarMensajeError("Error al ir al registro anterior: " + e.getMessage());
        }
    }
    
    public void IrSiguiente() {
        try {
            if (resultado != null) {
                if (!resultado.next()) {
                    // Si no hay registro siguiente, ir al último
                    resultado.last();
                }
                mostrarRegistroActual();
                System.out.println("Navegado al siguiente registro");
            }
        } catch (SQLException e) {
            mostrarMensajeError("Error al ir al siguiente registro: " + e.getMessage());
        }
    }
    
    public void IrUltimo() {
        try {
            if (resultado != null && resultado.last()) {
                mostrarRegistroActual();
                System.out.println("Navegado al último registro");
            }
        } catch (SQLException e) {
            mostrarMensajeError("Error al ir al último registro: " + e.getMessage());
        }
    }
    
    private void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        System.err.println(mensaje);
    }
    
    public void cerrarConexion() {
        try {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexion != null) conexion.close();
            System.out.println("Conexión cerrada correctamente");
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}

// Clase para manejar eventos de botones
class EventoBoton implements ActionListener {
    NavegadorCategorias fuente;
    
    public EventoBoton(NavegadorCategorias pWnd) {
        fuente = pWnd;
    }
    
    public void actionPerformed(ActionEvent evento) {
        String comando = evento.getActionCommand();
        
        // Usar equals() en lugar de == para comparar strings
        if ("Primero".equals(comando)) {
            System.out.println("Boton Primero presionado");
            fuente.IrPrimero();
        } else if ("Anterior".equals(comando)) {
            System.out.println("Boton Anterior presionado");
            fuente.IrAnterior();
        } else if ("Siguiente".equals(comando)) {
            System.out.println("Boton Siguiente presionado");
            fuente.IrSiguiente();
        } else if ("Último".equals(comando)) {
            System.out.println("Boton Último presionado");
            fuente.IrUltimo();
        }
    }
}

// Clase para manejar eventos de ventana
class EventosVentana extends WindowAdapter {
    private NavegadorCategorias fuente;
    
    public EventosVentana(NavegadorCategorias pWnd) {
        this.fuente = pWnd;
    }
    
    @Override
    public void windowClosing(WindowEvent evento) {
        System.out.println("Cerrando aplicación...");
        fuente.cerrarConexion();
        fuente.dispose();
        System.exit(0);
    }
    
    @Override
    public void windowOpened(WindowEvent evento) {
        System.out.println("Ventana abierta correctamente");
    }
    
    @Override
    public void windowIconified(WindowEvent evento) {
        System.out.println("Ventana minimizada");
    }
    
    @Override
    public void windowDeiconified(WindowEvent evento) {
        System.out.println("Ventana restaurada");
    }
}

// Clase principal con método main
public class NavegadorCategoriasApp {
    public static void main(String args[]) {
        
        // Crear y mostrar ventana en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            NavegadorCategorias miVentana = new NavegadorCategorias("Navegación de Categorías - EmpresaMSQL");
            miVentana.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            miVentana.pack();
            miVentana.setLocationRelativeTo(null); // Centrar ventana
            miVentana.setVisible(true);
            
            System.out.println("Aplicacion iniciada correctamente.");
            System.out.println("Use las teclas de flecha, Home y End para navegar.");
        });
    }
}
