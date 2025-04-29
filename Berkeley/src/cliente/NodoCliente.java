package cliente;

import comun.MensajeReloj;
import comun.Reloj;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Implementa un nodo cliente que participa en el algoritmo de Berkeley.
 * Se conecta al coordinador y recibe ajustes de tiempo.
 */
public class NodoCliente {
    private static final int PUERTO_COORDINADOR = 5000;
    private static final String HOST_COORDINADOR = "localhost";
    
    private String nombreNodo;
    private Reloj relojLocal;
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private boolean conectado;
    private ScheduledExecutorService scheduler;
    
    // Para notificar a la interfaz de usuario sobre eventos
    private List<EventoNodoListener> listeners;
    
    public NodoCliente(String nombreNodo) {
        this.nombreNodo = nombreNodo;
        this.relojLocal = Reloj.conHoraAleatoria(); // Inicia con una hora aleatoria
        this.listeners = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    /**
     * Conecta con el servidor coordinador.
     */
    public void conectar() {
        try {
            socket = new Socket(HOST_COORDINADOR, PUERTO_COORDINADOR);
            salida = new ObjectOutputStream(socket.getOutputStream());
            entrada = new ObjectInputStream(socket.getInputStream());
            
            conectado = true;
            notificarEventoNodo("Conectado al coordinador en " + HOST_COORDINADOR + ":" + PUERTO_COORDINADOR);
            
            // Iniciar el hilo que avanza el reloj
            scheduler.scheduleAtFixedRate(() -> {
                relojLocal.avanzarSegundo();
                notificarActualizacionReloj();
            }, 0, 1, TimeUnit.SECONDS);
            
            // Registrarse con el coordinador
            registrarConCoordinador();
            
            // Iniciar hilo para recibir mensajes
            new Thread(this::procesarMensajes).start();
            
        } catch (IOException e) {
            notificarEventoNodo("Error al conectar con el coordinador: " + e.getMessage());
        }
    }
    
    /**
     * Registra este nodo con el coordinador.
     */
    private void registrarConCoordinador() {
        try {
            MensajeReloj mensaje = new MensajeReloj(
                MensajeReloj.TipoMensaje.REGISTRO,
                nombreNodo,
                relojLocal.getHora()
            );
            
            salida.writeObject(mensaje);
            salida.flush();
            notificarEventoNodo("Registrado con el coordinador como: " + nombreNodo);
            
        } catch (IOException e) {
            notificarEventoNodo("Error al registrarse con el coordinador: " + e.getMessage());
        }
    }
    
    /**
     * Procesa los mensajes entrantes del coordinador.
     */
    private void procesarMensajes() {
        try {
            while (conectado) {
                Object obj = entrada.readObject();
                if (obj instanceof MensajeReloj) {
                    manejarMensaje((MensajeReloj) obj);
                }
            }
        } catch (EOFException | SocketException e) {
            notificarEventoNodo("Desconectado del coordinador");
            desconectar();
        } catch (Exception e) {
            notificarEventoNodo("Error en comunicación con coordinador: " + e.getMessage());
            desconectar();
        }
    }
    
    /**
     * Maneja un mensaje recibido del coordinador.
     */
    private void manejarMensaje(MensajeReloj mensaje) {
        switch (mensaje.getTipo()) {
            case SOLICITUD_HORA:
                enviarHoraActual();
                break;
                
            case AJUSTE_RELOJ:
                ajustarReloj(mensaje.getAjusteSegundos());
                break;
                
            default:
                notificarEventoNodo("Mensaje no reconocido del coordinador: " + mensaje.getTipo());
        }
    }
    
    /**
     * Envía la hora actual al coordinador.
     */
    private void enviarHoraActual() {
        try {
            LocalTime horaActual = relojLocal.getHora();
            MensajeReloj respuesta = new MensajeReloj(
                MensajeReloj.TipoMensaje.RESPUESTA_HORA,
                nombreNodo,
                horaActual
            );
            
            salida.writeObject(respuesta);
            salida.flush();
            notificarEventoNodo("Enviada hora actual al coordinador: " + horaActual);
            
        } catch (IOException e) {
            notificarEventoNodo("Error enviando hora al coordinador: " + e.getMessage());
        }
    }
    
    /**
     * Ajusta el reloj local según la información del coordinador.
     */
    private void ajustarReloj(long ajusteSegundos) {
        LocalTime horaAntes = relojLocal.getHora();
        relojLocal.ajustar(ajusteSegundos);
        LocalTime horaDespues = relojLocal.getHora();
        
        notificarEventoNodo("Ajustando reloj: " + ajusteSegundos + " segundos");
        notificarEventoNodo("Hora anterior: " + horaAntes + ", Hora nueva: " + horaDespues);
        notificarActualizacionReloj();
    }
    
    /**
     * Solicita una sincronización manual al coordinador.
     */
    public void solicitarSincronizacion() {
        if (!conectado) {
            notificarEventoNodo("No conectado al coordinador, no se puede sincronizar");
            return;
        }
        
        try {
            MensajeReloj solicitud = new MensajeReloj(MensajeReloj.TipoMensaje.SINCRONIZACION);
            salida.writeObject(solicitud);
            salida.flush();
            notificarEventoNodo("Enviada solicitud de sincronización al coordinador");
            
        } catch (IOException e) {
            notificarEventoNodo("Error solicitando sincronización: " + e.getMessage());
        }
    }
    
    /**
     * Desconecta del servidor coordinador.
     */
    public void desconectar() {
        if (!conectado) {
            return;
        }
        
        conectado = false;
        scheduler.shutdown();
        
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            notificarEventoNodo("Error al cerrar conexión: " + e.getMessage());
        }
        
        notificarEventoNodo("Desconectado del coordinador");
    }
    
    // Método para añadir listeners
    public void addListener(EventoNodoListener listener) {
        listeners.add(listener);
    }
    
    // Métodos para notificar eventos a la interfaz de usuario
    private void notificarEventoNodo(String mensaje) {
        for (EventoNodoListener listener : listeners) {
            listener.onEventoNodo(mensaje);
        }
    }
    
    private void notificarActualizacionReloj() {
        for (EventoNodoListener listener : listeners) {
            listener.onActualizacionReloj(relojLocal.getHora(), relojLocal.estaSincronizado());
        }
    }
    
    // Getters
    public LocalTime getHoraActual() {
        return relojLocal.getHora();
    }
    
    public boolean estaSincronizado() {
        return relojLocal.estaSincronizado();
    }
    
    public boolean estaConectado() {
        return conectado;
    }
    
    public String getNombreNodo() {
        return nombreNodo;
    }
    
    /**
     * Interfaz para notificar eventos del nodo.
     */
    public interface EventoNodoListener {
        void onEventoNodo(String mensaje);
        void onActualizacionReloj(LocalTime hora, boolean sincronizado);
    }
}