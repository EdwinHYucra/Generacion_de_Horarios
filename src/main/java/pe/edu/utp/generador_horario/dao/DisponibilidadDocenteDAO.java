package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;
import java.util.List;

public interface DisponibilidadDocenteDAO {
    List<DisponibilidadDocente> findByDocenteIdAndCicloId(Long idDocente, Long idCicloAcademico);

    long countBloquesDisponiblesEnRango(
            Long idDocente,
            Long idCicloAcademico,
            String diaSemana,
            java.time.LocalTime horaInicio,
            java.time.LocalTime horaFin);

    void deleteByDocenteIdAndCicloId(Long idDocente, Long idCicloAcademico);

    void save(DisponibilidadDocente disponibilidad);
}
