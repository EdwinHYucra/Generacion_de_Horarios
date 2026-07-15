package pe.edu.utp.generador_horario.service.interfaces;

/**
 * Programa la generación sin bloquear el guardado de disponibilidad o cursos.
 */
public interface HorarioGeneracionAsyncService {

    boolean programarGeneracion(Long idDocente);

    boolean estaEnProceso(Long idDocente);
}
