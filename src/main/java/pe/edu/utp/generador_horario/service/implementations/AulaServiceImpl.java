package pe.edu.utp.generador_horario.service.implementations;

import pe.edu.utp.generador_horario.entidad.Aula;
import pe.edu.utp.generador_horario.dao.AulaDAO;
import pe.edu.utp.generador_horario.service.interfaces.AulaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementa las operaciones de negocio para la gestion de aulas.
 *
 * <p>Centraliza validaciones como capacidad valida, sede obligatoria y
 * codigo unico antes de persistir el aula.</p>
 *
 * @author Edwin
 */
@Service
public class AulaServiceImpl implements AulaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AulaServiceImpl.class);

    private final AulaDAO aulaDAO;

    public AulaServiceImpl(AulaDAO aulaDAO) {
        this.aulaDAO = aulaDAO;
    }

    @Override
    public List<Aula> listarAulas() {
        return aulaDAO.findByEstadoTrue();
    }

    @Override
    public Aula obtenerPorId(Long id) {
        return aulaDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula no encontrada con ID: " + id));
    }

    @Override
    public Aula guardarAula(Aula aula) {
        if (aula.getEstado() == null) {
            aula.setEstado(true);
        }

        if (aula.getCapacidad() == null || aula.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        }

        if (aula.getSede() == null || aula.getSede().getIdSede() == null) {
            throw new IllegalArgumentException("Debe seleccionar una sede.");
        }

        if (aula.getIdAula() == null) {
            if (aulaDAO.existsByCodigo(aula.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un aula con ese código.");
            }
        } else {
            if (aulaDAO.existsByCodigoAndIdAulaNot(aula.getCodigo(), aula.getIdAula())) {
                throw new IllegalArgumentException("Ya existe otra aula con ese código.");
            }
        }

        try {
            return aulaDAO.save(aula);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar el aula. id={}, codigo={}",
                    aula.getIdAula(), aula.getCodigo(), e);
            throw e;
        }
    }

    @Override
    public void desactivarAula(Long id) {
        Aula aula = obtenerPorId(id);
        aula.setEstado(false);
        try {
            aulaDAO.save(aula);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la desactivacion del aula. id={}", id, e);
            throw e;
        }
    }
}

