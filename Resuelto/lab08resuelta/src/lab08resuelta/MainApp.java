package lab08resuelta;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

// Clase principal de la ventana
class Ventana extends JFrame {
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
    JButton BtnInsertar;
    JButton BtnModificar;
    JButton BtnEliminar;
    JButton BtnActualizar;
    ResultSet resultado;
    Statement sentencia;
    Connection conexion;

    public Ventana(String titulo) {
        super(titulo);
        inicializarComponentes();
        Conexion();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        // Inicializar labels
        LblId = new JLabel("Id Categoria");
        LblNombre = new JLabel("Nombre");
        LblDescripcion = new JLabel("Descripcion");
        
        // Inicializar campos de texto
        TxtId = new JTextField(15);
        TxtNombre = new JTextField(15);
        TxtDescripcion = new JTextField(15);

        // Inicializar botones de navegación
        BtnPrimero = new JButton("Primero");
        BtnPrimero.addActionListener(new EventoBoton(this));
        BtnAnterior = new JButton("Anterior");
        BtnAnterior.addActionListener(new EventoBoton(this));
        BtnSiguiente = new JButton("Siguiente");
        BtnSiguiente.addActionListener(new EventoBoton(this));
        BtnUltimo = new JButton("Ultimo");
        BtnUltimo.addActionListener(new EventoBoton(this));

        // Inicializar botones de operaciones CRUD
        BtnInsertar = new JButton("Insertar");
        BtnInsertar.addActionListener(new EventoBoton(this));
        BtnModificar = new JButton("Modificar");
        BtnModificar.addActionListener(new EventoBoton(this));
        BtnEliminar = new JButton("Eliminar");
        BtnEliminar.addActionListener(new EventoBoton(this));
        BtnActualizar = new JButton("Actualizar");
        BtnActualizar.addActionListener(new EventoBoton(this));

        // Panel para labels
        JPanel Panel11 = new JPanel();
        Panel11.setLayout(new BoxLayout(Panel11, BoxLayout.Y_AXIS));
        Panel11.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel11.add(LblId);
        Panel11.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel11.add(LblNombre);
        Panel11.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel11.add(LblDescripcion);
        Panel11.add(Box.createRigidArea(new Dimension(10, 20)));

        // Panel para campos de texto
        JPanel Panel12 = new JPanel();
        Panel12.setLayout(new BoxLayout(Panel12, BoxLayout.Y_AXIS));
        Panel12.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel12.add(TxtId);
        Panel12.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel12.add(TxtNombre);
        Panel12.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel12.add(TxtDescripcion);
        Panel12.add(Box.createRigidArea(new Dimension(10, 10)));

        // Panel principal para datos
        JPanel Panel1 = new JPanel();
        Panel1.setLayout(new BoxLayout(Panel1, BoxLayout.X_AXIS));
        Panel1.add(Box.createRigidArea(new Dimension(5, 10)));
        Panel1.add(Panel11);
        Panel1.add(Box.createRigidArea(new Dimension(5, 10)));
        Panel1.add(Panel12);

        // Panel para botones CRUD
        JPanel Panel2 = new JPanel();
        Panel2.setLayout(new BoxLayout(Panel2, BoxLayout.X_AXIS));
        Panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        Panel2.add(BtnInsertar);
        Panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        Panel2.add(BtnModificar);
        Panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        Panel2.add(BtnActualizar);
        Panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        Panel2.add(BtnEliminar);
        Panel2.add(Box.createRigidArea(new Dimension(5, 10)));
        Panel2.setBackground(new Color(0, 0, 255));

        // Panel para botones de navegación
        JPanel Panel3 = new JPanel();
        Panel3.setLayout(new BoxLayout(Panel3, BoxLayout.X_AXIS));
        Panel3.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel3.add(BtnPrimero);
        Panel3.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel3.add(BtnAnterior);
        Panel3.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel3.add(BtnSiguiente);
        Panel3.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel3.add(BtnUltimo);
        Panel3.add(Box.createRigidArea(new Dimension(10, 10)));
        Panel3.setBackground(Color.orange);

        // Panel principal
        JPanel Panel = new JPanel();
        Panel.setLayout(new BoxLayout(Panel, BoxLayout.Y_AXIS));
        Panel.add(Panel1);
        Panel.add(Box.createRigidArea(new Dimension(0, 20)));
        Panel.add(Panel2);
        Panel.add(Box.createRigidArea(new Dimension(0, 20)));
        Panel.add(Panel3);
        Panel.setBackground(new Color(255, 0, 0));

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(Panel);
        addWindowListener(new EventosVentana(this));
    }

    private void Conexion() {
        try {
            // Cargar el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establecer conexión - MODIFICA ESTOS DATOS SEGÚN TU CONFIGURACIÓN
            String url = "jdbc:mysql://localhost:3306/empresamsql?useSSL=false&serverTimezone=UTC";
            String usuario = "root";
            String password = "Frandiego20224232"; // Cambia por tu password
            
            conexion = DriverManager.getConnection(url, usuario, password);
            
            sentencia = conexion.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                ResultSet.CONCUR_UPDATABLE
            );
            
            boolean tieneResultados = sentencia.execute("SELECT * FROM categorias ORDER BY IDCategoria");
            
            if (tieneResultados) {
                resultado = sentencia.getResultSet();
                if (resultado != null && resultado.next()) {
                    mostrarRegistroActual();
                }
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Driver MySQL no encontrado: " + e.getMessage());
            System.out.println("Controlador no Encontrado: " + e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos: " + e.getMessage());
            System.out.println("Error de Conexión: " + e);
        }
    }

    private void mostrarRegistroActual() {
        try {
            TxtId.setText(resultado.getString("IDCategoria"));
            TxtNombre.setText(resultado.getString("Nombre"));
            TxtDescripcion.setText(resultado.getString("Descripcion"));
        } catch (SQLException e) {
            System.out.println("Error al mostrar registro: " + e);
        }
    }

    public void IrPrimero() {
        try {
            if (resultado.first()) {
                mostrarRegistroActual();
            }
        } catch (SQLException e) {
            System.out.println("Error al ir al primer registro: " + e);
        }
    }

    public void IrAnterior() {
        try {
            if (!resultado.previous()) {
                resultado.first();
            }
            mostrarRegistroActual();
        } catch (SQLException e) {
            System.out.println("Error al ir al registro anterior: " + e);
        }
    }

    public void IrSiguiente() {
        try {
            if (!resultado.next()) {
                resultado.last();
            }
            mostrarRegistroActual();
        } catch (SQLException e) {
            System.out.println("Error al ir al siguiente registro: " + e);
        }
    }

    public void IrUltimo() {
        try {
            if (resultado.last()) {
                mostrarRegistroActual();
            }
        } catch (SQLException e) {
            System.out.println("Error al ir al último registro: " + e);
        }
    }

    public void IrInsertar() {
        try {
            String nombre = TxtNombre.getText().trim();
            String descripcion = TxtDescripcion.getText().trim();
            
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio");
                return;
            }
            
            String sql = "INSERT INTO categorias (Nombre, Descripcion) VALUES (?, ?)";
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setString(1, nombre);
            pstmt.setString(2, descripcion);
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Registro insertado correctamente");
                IrActualizar(); // Actualizar el ResultSet
                IrUltimo(); // Ir al último registro (el que acabamos de insertar)
            }
            
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al insertar: " + e.getMessage());
            System.out.println("Error de inserción: " + e);
        }
    }

    public void IrEliminar() {
        try {
            String id = TxtId.getText().trim();
            
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay registro seleccionado para eliminar");
                return;
            }
            
            int confirmacion = JOptionPane.showConfirmDialog(
                this, 
                "¿Está seguro de eliminar este registro?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirmacion == JOptionPane.YES_OPTION) {
                String sql = "DELETE FROM categorias WHERE IDCategoria = ?";
                PreparedStatement pstmt = conexion.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(id));
                
                int filasAfectadas = pstmt.executeUpdate();
                
                if (filasAfectadas > 0) {
                    JOptionPane.showMessageDialog(this, "Registro eliminado correctamente");
                    IrActualizar(); // Actualizar el ResultSet
                    if (resultado.next()) {
                        mostrarRegistroActual();
                    } else if (resultado.previous()) {
                        mostrarRegistroActual();
                    } else {
                        // No hay más registros
                        TxtId.setText("");
                        TxtNombre.setText("");
                        TxtDescripcion.setText("");
                    }
                }
                
                pstmt.close();
            }
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
            System.out.println("Error de eliminación: " + e);
        }
    }

    public void IrModificar() {
        try {
            String id = TxtId.getText().trim();
            String nombre = TxtNombre.getText().trim();
            String descripcion = TxtDescripcion.getText().trim();
            
            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID y Nombre son obligatorios");
                return;
            }
            
            String sql = "UPDATE categorias SET Nombre = ?, Descripcion = ? WHERE IDCategoria = ?";
            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setString(1, nombre);
            pstmt.setString(2, descripcion);
            pstmt.setInt(3, Integer.parseInt(id));
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                JOptionPane.showMessageDialog(this, "Registro modificado correctamente");
                IrActualizar(); // Actualizar el ResultSet
                // Buscar el registro modificado
                while (resultado.next()) {
                    if (resultado.getString("IDCategoria").equals(id)) {
                        mostrarRegistroActual();
                        break;
                    }
                }
            }
            
            pstmt.close();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error al modificar: " + e.getMessage());
            System.out.println("Error de modificación: " + e);
        }
    }

    public void IrActualizar() {
        try {
            sentencia.execute("SELECT * FROM categorias ORDER BY IDCategoria");
            resultado = sentencia.getResultSet();
            if (resultado != null && resultado.next()) {
                mostrarRegistroActual();
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar: " + e);
        }
    }

    public void cerrarConexion() {
        try {
            if (resultado != null) resultado.close();
            if (sentencia != null) sentencia.close();
            if (conexion != null) conexion.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión: " + e);
        }
    }
}

// Clase para manejar eventos de botones
class EventoBoton implements ActionListener {
    private Ventana ventana;

    public EventoBoton(Ventana v) {
        this.ventana = v;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        
        switch (comando) {
            case "Primero":
                ventana.IrPrimero();
                break;
            case "Anterior":
                ventana.IrAnterior();
                break;
            case "Siguiente":
                ventana.IrSiguiente();
                break;
            case "Ultimo":
                ventana.IrUltimo();
                break;
            case "Insertar":
                ventana.IrInsertar();
                break;
            case "Modificar":
                ventana.IrModificar();
                break;
            case "Eliminar":
                ventana.IrEliminar();
                break;
            case "Actualizar":
                ventana.IrActualizar();
                break;
            default:
                System.out.println("Comando desconocido: " + comando);
                break;
        }
    }
}

// Clase para manejar eventos de ventana
class EventosVentana extends WindowAdapter {
    private Ventana ventana;

    public EventosVentana(Ventana v) {
        this.ventana = v;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        ventana.cerrarConexion();
        System.exit(0);
    }
}

// Clase principal para ejecutar la aplicación
public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            
            
            Ventana ventana = new Ventana("Sistema de Gestión de Categorías");
            ventana.setVisible(true);
        });
    }
}