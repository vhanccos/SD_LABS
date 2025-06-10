// Archivo único: Lab08resuelta.java
// Todas las clases en un solo archivo para evitar problemas de paquetes

package lab08resuelta;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

// Clase principal
public class Lab08resuelta {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
          
            
            Ventana ventana = new Ventana("Consultas MySQL - EmpresaMSQL");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setVisible(true);
            ventana.pack();
            ventana.setLocationRelativeTo(null);
        });
    }
}

// Clase Ventana
class Ventana extends JFrame {
    JTextField txtSql;
    JTextArea areaResultados;
    JButton btnConsulta;
    
    public Ventana(String titulo) {
        super(titulo);
        initComponents();
        setupLayout();
        setupEvents();
    }
    
    private void initComponents() {
        txtSql = new JTextField(30);
        txtSql.setText("SELECT * FROM productos"); // Consulta por defecto
        areaResultados = new JTextArea(15, 50);
        areaResultados.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        areaResultados.setEditable(false);
        btnConsulta = new JButton("Ejecutar SQL");
    }
    
    private void setupLayout() {
        JScrollPane scrollPanel = new JScrollPane(areaResultados);
        scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JPanel panelSql = new JPanel(new FlowLayout());
        panelSql.add(new JLabel("Consulta SQL:"));
        panelSql.add(txtSql);
        panelSql.add(btnConsulta);
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(panelSql, BorderLayout.NORTH);
        panelPrincipal.add(scrollPanel, BorderLayout.CENTER);
        
        // Panel de consultas predefinidas
        JPanel panelConsultas = new JPanel(new FlowLayout());
        JButton btnProductos = new JButton("Ver Productos");
        JButton btnCategorias = new JButton("Ver Categorías");
        JButton btnJoin = new JButton("Productos con Categorías");
        
        btnProductos.addActionListener(e -> {
            txtSql.setText("SELECT * FROM productos");
            ejecutarConsulta();
        });
        
        btnCategorias.addActionListener(e -> {
            txtSql.setText("SELECT * FROM categorias");
            ejecutarConsulta();
        });
        
        btnJoin.addActionListener(e -> {
            txtSql.setText("SELECT p.IDProducto, p.Nombre as Producto, c.Nombre as Categoria, p.Precio " +
                          "FROM productos p LEFT JOIN categorias c ON p.IDCategoria = c.IDCategoria");
            ejecutarConsulta();
        });
        
        panelConsultas.add(btnProductos);
        panelConsultas.add(btnCategorias);
        panelConsultas.add(btnJoin);
        
        panelPrincipal.add(panelConsultas, BorderLayout.SOUTH);
        
        getContentPane().add(panelPrincipal);
    }
    
    private void setupEvents() {
        // Usar ActionListener directo en lugar de clase separada
        btnConsulta.addActionListener(e -> ejecutarConsulta());
        
        // Permitir ejecutar con Enter
        txtSql.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ejecutarConsulta();
                }
            }
        });
        
        // Manejar cierre de ventana directamente
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Cerrando aplicación...");
                System.exit(0);
            }
            
            @Override
            public void windowOpened(WindowEvent e) {
                System.out.println("Aplicación iniciada correctamente.");
            }
        });
    }
    
    // Método público para ejecutar consultas
    public void ejecutarConsulta() {
        verBaseDatos();
    }
    
    void verBaseDatos() {
        Connection conexion = null;
        Statement sentencia = null;
        ResultSet resultado = null;
        
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer conexión
            String url = "jdbc:mysql://localhost:3306/empresamsql?useTimezone=true&serverTimezone=UTC&useSSL=false";
            String usuario = "root";
            String password = "Frandiego20224232";
            
            conexion = DriverManager.getConnection(url, usuario, password);
            sentencia = conexion.createStatement();
            
            String sql = txtSql.getText().trim();
            if (sql.isEmpty()) {
                areaResultados.setText("Por favor, ingrese una consulta SQL.");
                return;
            }
            
            // Verificar si es una consulta SELECT
            if (sql.toUpperCase().startsWith("SELECT")) {
                resultado = sentencia.executeQuery(sql);
                mostrarResultados(resultado);
            } else {
                // Para INSERT, UPDATE, DELETE
                int filasAfectadas = sentencia.executeUpdate(sql);
                areaResultados.setText("Consulta ejecutada exitosamente.\nFilas afectadas: " + filasAfectadas);
            }
            
        } catch (ClassNotFoundException e) {
            areaResultados.setText("Error: Controlador MySQL no encontrado.\n" +
                "Asegúrese de tener el JAR de MySQL Connector/J en el classpath.\n" +
                "Detalle: " + e.getMessage());
        } catch (SQLException e) {
            areaResultados.setText("Error de SQL:\n" + e.getMessage() + 
                "\n\nCódigo de error: " + e.getErrorCode());
        } catch (Exception e) {
            areaResultados.setText("Error inesperado:\n" + e.getMessage());
            e.printStackTrace();
        } finally {
            // Cerrar recursos
            try {
                if (resultado != null) resultado.close();
                if (sentencia != null) sentencia.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar conexiones: " + e.getMessage());
            }
        }
    }
    
    void mostrarResultados(ResultSet r) throws SQLException {
        if (r == null) {
            areaResultados.setText("No se obtuvieron resultados.");
            return;
        }
        
        ResultSetMetaData metaData = r.getMetaData();
        int numColumnas = metaData.getColumnCount();
        StringBuilder texto = new StringBuilder();
        
        // Mostrar encabezados
        for (int i = 1; i <= numColumnas; i++) {
            String nombreColumna = metaData.getColumnLabel(i);
            texto.append(String.format("%-20s", nombreColumna));
            if (i < numColumnas) texto.append(" | ");
        }
        texto.append("\n");
        
        // Línea separadora
        for (int i = 1; i <= numColumnas; i++) {
            texto.append("--------------------");
            if (i < numColumnas) texto.append("-+-");
        }
        texto.append("\n");
        
        // Mostrar datos
        int contador = 0;
        while (r.next()) {
            for (int i = 1; i <= numColumnas; i++) {
                Object valor = r.getObject(i);
                String valorStr = (valor != null) ? valor.toString() : "NULL";
                texto.append(String.format("%-20s", valorStr));
                if (i < numColumnas) texto.append(" | ");
            }
            texto.append("\n");
            contador++;
        }
        
        texto.append("\n--- Total de registros: ").append(contador).append(" ---");
        areaResultados.setText(texto.toString());
        areaResultados.setCaretPosition(0);
    }
}