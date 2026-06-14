package pe.edu.utp.generador_horario.service.implementations;

import pe.edu.utp.generador_horario.entidad.Carrera;
import pe.edu.utp.generador_horario.dao.CarreraDAO;
import pe.edu.utp.generador_horario.service.interfaces.CarreraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementa las operaciones de negocio para carreras academicas.
 *
 * <p>Valida la unicidad del codigo y nombre antes de persistir una carrera.</p>
 *
 * @author Edwin
 */
@Service
public class CarreraServiceImpl implements CarreraService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarreraServiceImpl.class);

    private final CarreraDAO carreraDAO;

    public CarreraServiceImpl(CarreraDAO carreraDAO) {
        this.carreraDAO = carreraDAO;
    }

    @Override
    public List<Carrera> listarCarreras() {
        return carreraDAO.findByEstadoTrue();
    }

    @Override
    public Carrera obtenerPorId(Long id) {
        return carreraDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada con ID: " + id));
    }

    @Override
    public Carrera guardarCarrera(Carrera carrera) {
        if (carrera.getEstado() == null) {
            carrera.setEstado(true);
        }

        if (carrera.getIdCarrera() == null) {
            if (carreraDAO.existsByCodigo(carrera.getCodigo())) {
                throw new IllegalArgumentException("Ya existe una carrera con ese código.");
            }

            if (carreraDAO.existsByNombre(carrera.getNombre())) {
                throw new IllegalArgumentException("Ya existe una carrera con ese nombre.");
            }
        } else {
            if (carreraDAO.existsByCodigoAndIdCarreraNot(carrera.getCodigo(), carrera.getIdCarrera())) {
                throw new IllegalArgumentException("Ya existe otra carrera con ese código.");
            }

            if (carreraDAO.existsByNombreAndIdCarreraNot(carrera.getNombre(), carrera.getIdCarrera())) {
                throw new IllegalArgumentException("Ya existe otra carrera con ese nombre.");
            }
        }

        try {
            return carreraDAO.save(carrera);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la carrera. id={}, codigo={}, nombre={}",
                    carrera.getIdCarrera(), carrera.getCodigo(), carrera.getNombre(), e);
            throw e;
        }
    }

    @Override
    public void desactivarCarrera(Long id) {
        Carrera carrera = obtenerPorId(id);
        carrera.setEstado(false);
        try {
            carreraDAO.save(carrera);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la desactivacion de la carrera. id={}", id, e);
            throw e;
        }
    }
}

