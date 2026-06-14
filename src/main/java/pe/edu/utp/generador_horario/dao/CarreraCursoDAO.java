package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.CarreraCurso;

import java.util.List;
import java.util.Optional;

public interface CarreraCursoDAO {
    List<CarreraCurso> findByEstadoTrue();
    Optional<CarreraCurso> findById(Long id);
    CarreraCurso save(CarreraCurso carreraCurso);
    boolean existsByCarrera_IdCarreraAndCurso_IdCurso(Long idCarrera, Long idCurso);
    boolean existsByCarrera_IdCarreraAndCurso_IdCursoAndIdCarreraCursoNot(Long idCarrera, Long idCurso, Long idCarreraCurso);
}
