package comun;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.Duration;

/**
 * Clase que representa un reloj con funcionalidades básicas.
 */
public class Reloj implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private LocalTime hora;
    private boolean sincronizado;
    
    public Reloj() {
        this.hora = LocalTime.now();
        this.sincronizado = false;
    }
    
    public Reloj(LocalTime horaInicial) {
        this.hora = horaInicial;
        this.sincronizado = false;
    }
    
    /**
     * Genera una hora aleatoria que difiere de la hora actual
     * entre -30 y +30 segundos.
     */
    public static Reloj conHoraAleatoria() {
        // Generar un desplazamiento aleatorio entre -30 y +30 segundos
        int desplazamiento = (int) (Math.random() * 61) - 30;
        LocalTime horaAleatoria = LocalTime.now().plusSeconds(desplazamiento);
        return new Reloj(horaAleatoria);
    }
    
    /**
     * Avanza el reloj en un segundo.
     */
    public void avanzarSegundo() {
        hora = hora.plusSeconds(1);
    }
    
    /**
     * Ajusta el reloj según la diferencia especificada en segundos.
     */
    public void ajustar(long segundos) {
        hora = hora.plusSeconds(segundos);
        sincronizado = true;
    }
    
    /**
     * Calcula la diferencia en segundos entre este reloj y otro.
     */
    public long diferenciaEn(Reloj otroReloj) {
        return Duration.between(this.hora, otroReloj.getHora()).getSeconds();
    }
    
    /**
     * Calcula la diferencia en segundos entre este reloj y una hora específica.
     */
    public long diferenciaEn(LocalTime otraHora) {
        return Duration.between(this.hora, otraHora).getSeconds();
    }
    
    // Getters y setters
    public LocalTime getHora() {
        return hora;
    }
    
    public void setHora(LocalTime hora) {
        this.hora = hora;
    }
    
    public boolean estaSincronizado() {
        return sincronizado;
    }
    
    public void setSincronizado(boolean sincronizado) {
        this.sincronizado = sincronizado;
    }
    
    @Override
    public String toString() {
        return hora.toString();
    }
}