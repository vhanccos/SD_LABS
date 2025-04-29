package servidor;

import comun.MensajeReloj;
import comun.Reloj;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Implementa el coordinador del algoritmo de Berkeley.
 * Actúa como servidor, recibiendo conexiones de los nodos.
 */
public class CoordinadorServidor {
    private static final int PUERTO = 5000;
    
    private ServerSocket serverSocket;
    private boolean ejecutando;
    private Reloj relojCoordinador;
    private Map<String, GestorCliente> nodosConectados;
    private ScheduledExecutorService scheduler;
    
    // Para notificar a la interfaz de usuario sobre eventos
    private List<EventoCoordinadorListener> listeners;
    
    public CoordinadorServidor() {
        this.relojCoordinador = new Reloj();
        this.nodosConectados = new ConcurrentHashMap<>();
        this.listeners = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(2);
    }
    
    /**
     * Inicia el servidor y comienza a aceptar conexiones.
     */
    public void iniciar() {
        try {
            serverSocket = new ServerSocket(PUERTO);
            ejecutando = true;
            
            notificarEventoServidor("Servidor iniciado en puerto " + PUERTO);
            
            // Iniciar el hilo que avanza el reloj
            scheduler.scheduleAtFixedRate(() -> {
                relojCoordinador.avanzarSegundo();
                notificarActualizacionReloj();
            }, 0, 1, TimeUnit.SECONDS);
            
            // Iniciar el hilo que realiza sincronizaciones periódicas
            //scheduler.scheduleAtFixedRate(this::sincronizarRelojes, 15, 15, TimeUnit.SECONDS);
            
            // Iniciar el hilo que acepta conexiones
            new Thread(this::aceptarConexiones).start();
            
        } catch (IOException e) {
            notificarEventoServidor("Error al iniciar servidor: " + e.getMessage());
        }
    }
    
    /**
     * Detiene el servidor y cierra todas las conexiones.
     */
    public void detener() {
        ejecutando = false;
        scheduler.shutdown();
        
        // Cerrar conexiones con los nodos
        for (GestorCliente gestor : nodosConectados.values()) {
            gestor.cerrar();
        }
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            notificarEventoServidor("Error al cerrar servidor: " + e.getMessage());
        }
        
        notificarEventoServidor("Servidor detenido");
    }
    
    /**
     * Hilo que acepta nuevas conexiones de nodos.
     */
    private void aceptarConexiones() {
        try {
            while (ejecutando) {
                Socket socketCliente = serverSocket.accept();
                procesarNuevaConexion(socketCliente);
            }
        } catch (IOException e) {
            if (ejecutando) {
                notificarEventoServidor("Error aceptando conexiones: " + e.getMessage());
            }
        }
    }
    
    /**
     * Procesa una nueva conexión de un nodo.
     */
    private void procesarNuevaConexion(Socket socketCliente) {
        try {
            GestorCliente gestor = new GestorCliente(socketCliente);
            gestor.iniciar();
        } catch (IOException e) {
            notificarEventoServidor("Error al procesar conexión: " + e.getMessage());
        }
    }
    
    /**
     * Implementa el algoritmo de Berkeley para sincronizar los relojes.
     */
    public void sincronizarRelojes() {
        if (nodosConectados.isEmpty()) {
            notificarEventoServidor("No hay nodos para sincronizar");
            return;
        }
        
        notificarEventoServidor("\n--- INICIANDO SINCRONIZACIÓN ---");
        LocalTime horaCoordinador = relojCoordinador.getHora();
        notificarEventoServidor("Hora del coordinador: " + horaCoordinador);
        
        // Paso 1: Solicitar la hora a todos los nodos
        Map<String, LocalTime> horasNodos = new HashMap<>();
        
        // Añadir la hora del coordinador
        horasNodos.put("Coordinador", horaCoordinador);
        
        // Solicitar y recoger la hora de cada nodo
        for (GestorCliente gestor : nodosConectados.values()) {
            MensajeReloj solicitud = new MensajeReloj(MensajeReloj.TipoMensaje.SOLICITUD_HORA);
            try {
                gestor.enviarMensaje(solicitud);
                // La respuesta se procesará en el método manejarMensaje del GestorCliente
            } catch (IOException e) {
                notificarEventoServidor("Error solicitando hora al nodo " + gestor.nombreNodo + ": " + e.getMessage());
            }
        }
        
        // Esperar un momento para recibir todas las respuestas
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Recolectar las horas de los nodos
        for (Map.Entry<String, GestorCliente> entry : nodosConectados.entrySet()) {
            GestorCliente gestor = entry.getValue();
            if (gestor.ultimaHoraReportada != null) {
                horasNodos.put(gestor.nombreNodo, gestor.ultimaHoraReportada);
                notificarEventoServidor("Hora recibida de " + gestor.nombreNodo + ": " + gestor.ultimaHoraReportada);
            }
        }
        
        // Paso 2: Calcular el promedio de todas las horas
        long totalSegundos = 0;
        for (LocalTime hora : horasNodos.values()) {
            totalSegundos += hora.toSecondOfDay();
        }
        
        long promedioSegundos = totalSegundos / horasNodos.size();
        LocalTime horaPromedio = LocalTime.ofSecondOfDay(promedioSegundos);
        notificarEventoServidor("Hora promedio calculada: " + horaPromedio);
        
        // Paso 3: Calcular y enviar ajustes a cada nodo
        for (Map.Entry<String, GestorCliente> entry : nodosConectados.entrySet()) {
            String nombreNodo = entry.getKey();
            GestorCliente gestor = entry.getValue();
            
            if (horasNodos.containsKey(nombreNodo)) {
                LocalTime horaNodo = horasNodos.get(nombreNodo);
                long ajuste = horaPromedio.toSecondOfDay() - horaNodo.toSecondOfDay();
                
                MensajeReloj mensajeAjuste = new MensajeReloj(MensajeReloj.TipoMensaje.AJUSTE_RELOJ, ajuste);
                try {
                    gestor.enviarMensaje(mensajeAjuste);
                    notificarEventoServidor("Enviado ajuste de " + ajuste + " segundos a " + nombreNodo);
                } catch (IOException e) {
                    notificarEventoServidor("Error enviando ajuste a " + nombreNodo + ": " + e.getMessage());
                }
            }
        }
        
        // Ajustar también el reloj del coordinador
        long ajusteCoordinador = horaPromedio.toSecondOfDay() - horaCoordinador.toSecondOfDay();
        relojCoordinador.ajustar(ajusteCoordinador);
        notificarEventoServidor("Ajuste del coordinador: " + ajusteCoordinador + " segundos");
        notificarActualizacionReloj();
        
        notificarEventoServidor("--- SINCRONIZACIÓN COMPLETADA ---\n");
    }
    
    /**
     * Clase interna que gestiona la comunicación con un nodo cliente.
     */
    private class GestorCliente {
        private Socket socket;
        private ObjectOutputStream salida;
        private ObjectInputStream entrada;
        private String nombreNodo;
        private LocalTime ultimaHoraReportada;
        
        public GestorCliente(Socket socket) throws IOException {
            this.socket = socket;
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
        }
        
        public void iniciar() {
            new Thread(this::procesarMensajes).start();
        }
        
        private void procesarMensajes() {
            try {
                while (ejecutando) {
                    Object obj = entrada.readObject();
                    if (obj instanceof MensajeReloj) {
                        manejarMensaje((MensajeReloj) obj);
                    }
                }
            } catch (EOFException | SocketException e) {
                // Cliente desconectado
                cerrar();
            } catch (Exception e) {
                notificarEventoServidor("Error en comunicación con " + 
                                      (nombreNodo != null ? nombreNodo : socket.getInetAddress()) + 
                                      ": " + e.getMessage());
                cerrar();
            }
        }
        
        private void manejarMensaje(MensajeReloj mensaje) {
            switch (mensaje.getTipo()) {
                case REGISTRO:
                    this.nombreNodo = mensaje.getNombreNodo();
                    this.ultimaHoraReportada = mensaje.getHoraLocal();
                    nodosConectados.put(nombreNodo, this);
                    notificarEventoServidor("Nodo registrado: " + nombreNodo + 
                                          " con hora inicial " + ultimaHoraReportada);
                    notificarCambioNodos();
                    break;
                    
                case RESPUESTA_HORA:
                    this.ultimaHoraReportada = mensaje.getHoraLocal();
                    break;
                    
                case SINCRONIZACION:
                    // Solicitud manual de sincronización
                    sincronizarRelojes();
                    break;
                    
                default:
                    notificarEventoServidor("Mensaje no reconocido de " + nombreNodo);
            }
        }
        
        public void enviarMensaje(MensajeReloj mensaje) throws IOException {
            salida.writeObject(mensaje);
            salida.flush();
        }
        
        public void cerrar() {
            try {
                if (nombreNodo != null) {
                    nodosConectados.remove(nombreNodo);
                    notificarEventoServidor("Nodo desconectado: " + nombreNodo);
                    notificarCambioNodos();
                }
                
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                notificarEventoServidor("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
    
    // Método para añadir listeners
    public void addListener(EventoCoordinadorListener listener) {
        listeners.add(listener);
    }
    
    // Métodos para notificar eventos a la interfaz de usuario
    private void notificarEventoServidor(String mensaje) {
        for (EventoCoordinadorListener listener : listeners) {
            listener.onEventoServidor(mensaje);
        }
    }
    
    private void notificarActualizacionReloj() {
        for (EventoCoordinadorListener listener : listeners) {
            listener.onActualizacionReloj(relojCoordinador.getHora(), relojCoordinador.estaSincronizado());
        }
    }
    
    private void notificarCambioNodos() {
        List<String> nombresNodos = new ArrayList<>(nodosConectados.keySet());
        for (EventoCoordinadorListener listener : listeners) {
            listener.onCambioNodos(nombresNodos);
        }
    }
    
    // Getters
    public LocalTime getHoraActual() {
        return relojCoordinador.getHora();
    }
    
    public boolean estaSincronizado() {
        return relojCoordinador.estaSincronizado();
    }
    
    public List<String> getNodosConectados() {
        return new ArrayList<>(nodosConectados.keySet());
    }
    
    /**
     * Interfaz para notificar eventos del coordinador.
     */
    public interface EventoCoordinadorListener {
        void onEventoServidor(String mensaje);
        void onActualizacionReloj(LocalTime hora, boolean sincronizado);
        void onCambioNodos(List<String> nodos);
    }
}