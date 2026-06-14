package pe.edu.utp.generador_horario.service.implementations;

import pe.edu.utp.generador_horario.entidad.Sede;
import pe.edu.utp.generador_horario.dao.SedeDAO;
import pe.edu.utp.generador_horario.service.interfaces.SedeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementa las operaciones de negocio para sedes.
 *
 * <p>Valida la unicidad del codigo y nombre antes de persistir una sede.</p>
 *
 * @author Edwin
 */
@Service
public class SedeServiceImpl implements SedeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SedeServiceImpl.class);

    private final SedeDAO sedeDAO;

    public SedeServiceImpl(SedeDAO sedeDAO) {
        this.sedeDAO = sedeDAO;
    }

    @Override
    public List<Sede> listarSedes() {
        return sedeDAO.findByEstadoTrue();
    }

    @Override
    public Sede obtenerPorId(Long id) {
        return sedeDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada con ID: " + id));
    }

    @Override
    public Sede guardarSede(Sede sede) {
        if (sede.getEstado() == null) {
            sede.setEstado(true);
        }

        if (sede.getIdSede() == null) {
            if (sedeDAO.existsByCodigo(sede.getCodigo())) {
                throw new IllegalArgumentException("Ya existe una sede con ese código.");
            }

            if (sedeDAO.existsByNombre(sede.getNombre())) {
                throw new IllegalArgumentException("Ya existe una sede con ese nombre.");
            }
        } else {
            if (sedeDAO.existsByCodigoAndIdSedeNot(sede.getCodigo(), sede.getIdSede())) {
                throw new IllegalArgumentException("Ya existe otra sede con ese código.");
            }

            if (sedeDAO.existsByNombreAndIdSedeNot(sede.getNombre(), sede.getIdSede())) {
                throw new IllegalArgumentException("Ya existe otra sede con ese nombre.");
            }
        }

        try {
            return sedeDAO.save(sede);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la sede. id={}, codigo={}, nombre={}",
                    sede.getIdSede(), sede.getCodigo(), sede.getNombre(), e);
            throw e;
        }
    }

    @Override
    public void desactivarSede(Long id) {
        Sede sede = obtenerPorId(id);
        sede.setEstado(false);
        try {
            sedeDAO.save(sede);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la desactivacion de la sede. id={}", id, e);
            throw e;
        }
    }
}

