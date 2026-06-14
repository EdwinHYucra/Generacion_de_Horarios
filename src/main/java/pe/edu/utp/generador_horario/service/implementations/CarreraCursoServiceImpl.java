package pe.edu.utp.generador_horario.service.implementations;

import pe.edu.utp.generador_horario.entidad.CarreraCurso;
import pe.edu.utp.generador_horario.dao.CarreraCursoDAO;
import pe.edu.utp.generador_horario.service.interfaces.CarreraCursoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementa la gestion de asignaciones entre carreras y cursos.
 *
 * <p>Controla carrera, curso, ciclo y duplicidad de la relacion antes
 * de guardar la asignacion academica.</p>
 *
 * @author Edwin
 */
@Service
public class CarreraCursoServiceImpl implements CarreraCursoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarreraCursoServiceImpl.class);

    private final CarreraCursoDAO carreraCursoDAO;

    public CarreraCursoServiceImpl(CarreraCursoDAO carreraCursoDAO) {
        this.carreraCursoDAO = carreraCursoDAO;
    }

    @Override
    public List<CarreraCurso> listarAsignaciones() {
        return carreraCursoDAO.findByEstadoTrue();
    }

    @Override
    public CarreraCurso obtenerPorId(Long id) {
        return carreraCursoDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));
    }

    @Override
    public CarreraCurso guardarAsignacion(CarreraCurso carreraCurso) {
        if (carreraCurso.getEstado() == null) {
            carreraCurso.setEstado(true);
        }

        if (carreraCurso.getCarrera() == null || carreraCurso.getCarrera().getIdCarrera() == null) {
            throw new IllegalArgumentException("Debe seleccionar una carrera.");
        }

        if (carreraCurso.getCurso() == null || carreraCurso.getCurso().getIdCurso() == null) {
            throw new IllegalArgumentException("Debe seleccionar un curso.");
        }

        if (carreraCurso.getCiclo() == null || carreraCurso.getCiclo() < 1 || carreraCurso.getCiclo() > 10) {
            throw new IllegalArgumentException("El ciclo debe estar entre 1 y 10.");
        }

        Long idCarrera = carreraCurso.getCarrera().getIdCarrera();
        Long idCurso = carreraCurso.getCurso().getIdCurso();

        if (carreraCurso.getIdCarreraCurso() == null) {
            if (carreraCursoDAO.existsByCarrera_IdCarreraAndCurso_IdCurso(idCarrera, idCurso)) {
                throw new IllegalArgumentException("Este curso ya está asignado a esa carrera.");
            }
        } else {
            if (carreraCursoDAO.existsByCarrera_IdCarreraAndCurso_IdCursoAndIdCarreraCursoNot(
                    idCarrera,
                    idCurso,
                    carreraCurso.getIdCarreraCurso()
            )) {
                throw new IllegalArgumentException("Este curso ya está asignado a esa carrera.");
            }
        }

        try {
            return carreraCursoDAO.save(carreraCurso);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la asignacion carrera-curso. id={}, carreraId={}, cursoId={}",
                    carreraCurso.getIdCarreraCurso(), idCarrera, idCurso, e);
            throw e;
        }
    }

    @Override
    public void desactivarAsignacion(Long id) {
        CarreraCurso carreraCurso = obtenerPorId(id);
        carreraCurso.setEstado(false);
        try {
            carreraCursoDAO.save(carreraCurso);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la desactivacion de la asignacion carrera-curso. id={}", id, e);
            throw e;
        }
    }
}

