package comun;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Clase para los mensajes intercambiados entre el coordinador y los nodos.
 * Implementa Serializable para poder ser transmitida por los sockets.
 */
public class MensajeReloj implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum TipoMensaje {
        SOLICITUD_HORA,    // Coordinador solicita hora al nodo
        RESPUESTA_HORA,    // Nodo responde con su hora local
        AJUSTE_RELOJ,      // Coordinador envía ajuste a realizar
        SINCRONIZACION,    // Solicitud manual de sincronización
        REGISTRO           // Nodo se registra con el coordinador
    }
    
    private TipoMensaje tipo;
    private String nombreNodo;
    private LocalTime horaLocal;
    private long ajusteSegundos;
    
    // Constructor para mensajes generales
    public MensajeReloj(TipoMensaje tipo) {
        this.tipo = tipo;
    }
    
    // Constructor para mensajes con información del nodo
    public MensajeReloj(TipoMensaje tipo, String nombreNodo, LocalTime horaLocal) {
        this.tipo = tipo;
        this.nombreNodo = nombreNodo;
        this.horaLocal = horaLocal;
    }
    
    // Constructor para mensajes de ajuste
    public MensajeReloj(TipoMensaje tipo, long ajusteSegundos) {
        this.tipo = tipo;
        this.ajusteSegundos = ajusteSegundos;
    }

    // Getters y setters
    public TipoMensaje getTipo() {
        return tipo;
    }

    public String getNombreNodo() {
        return nombreNodo;
    }

    public LocalTime getHoraLocal() {
        return horaLocal;
    }

    public long getAjusteSegundos() {
        return ajusteSegundos;
    }
    
    @Override
    public String toString() {
        return "MensajeReloj [tipo=" + tipo + 
               (nombreNodo != null ? ", nodo=" + nombreNodo : "") + 
               (horaLocal != null ? ", hora=" + horaLocal : "") +
               (tipo == TipoMensaje.AJUSTE_RELOJ ? ", ajuste=" + ajusteSegundos + "s" : "") +
               "]";
    }
}