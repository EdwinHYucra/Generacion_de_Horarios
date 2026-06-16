package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.DisponibilidadDocente;
import java.util.List;

public interface DisponibilidadDocenteDAO {
    List<DisponibilidadDocente> findByDocenteId(Long idDocente);

    void deleteByDocenteId(Long idDocente);

    void save(DisponibilidadDocente disponibilidad);
}