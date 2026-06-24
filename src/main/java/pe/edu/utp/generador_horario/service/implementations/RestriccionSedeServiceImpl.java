package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.dao.RestriccionSedeDAO;
import pe.edu.utp.generador_horario.entidad.RestriccionSede;
import pe.edu.utp.generador_horario.service.interfaces.RestriccionSedeService;

import java.util.List;
import java.util.Objects;

/**
 * Servicio de negocio para reglas de traslado entre sedes.
 *
 * <p>SOLID aplicado: el controlador solo coordina la vista y este servicio
 * concentra las validaciones de negocio antes de delegar al DAO.</p>
 */
@Service
public class RestriccionSedeServiceImpl implements RestriccionSedeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestriccionSedeServiceImpl.class);

    private final RestriccionSedeDAO restriccionSedeDAO;

    public RestriccionSedeServiceImpl(RestriccionSedeDAO restriccionSedeDAO) {
        this.restriccionSedeDAO = restriccionSedeDAO;
    }

    @Override
    public List<RestriccionSede> listarRestricciones() {
        return restriccionSedeDAO.findAll();
    }

    @Override
    public RestriccionSede obtenerPorId(Long id) {
        return restriccionSedeDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Regla de traslado no encontrada."));
    }

    @Override
    public RestriccionSede guardar(RestriccionSede restriccionSede) {
        validar(restriccionSede);

        try {
            RestriccionSede guardada = restriccionSedeDAO.save(restriccionSede);
            LOGGER.info("Regla de traslado guardada. id={}, origen={}, destino={}, minutos={}",
                    guardada.getIdRestriccion(),
                    guardada.getSedeOrigen().getIdSede(),
                    guardada.getSedeDestino().getIdSede(),
                    guardada.getTiempoMinimoMinutos());
            return guardada;
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo guardar la regla de traslado entre sedes.", e);
            throw e;
        }
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        try {
            restriccionSedeDAO.deleteById(id);
            LOGGER.info("Regla de traslado eliminada. id={}", id);
        } catch (RuntimeException e) {
            LOGGER.error("No se pudo eliminar la regla de traslado. id={}", id, e);
            throw e;
        }
    }

    private void validar(RestriccionSede restriccionSede) {
        Long sedeOrigen = restriccionSede.getSedeOrigen() == null
                ? null
                : restriccionSede.getSedeOrigen().getIdSede();
        Long sedeDestino = restriccionSede.getSedeDestino() == null
                ? null
                : restriccionSede.getSedeDestino().getIdSede();

        if (sedeOrigen == null || sedeDestino == null) {
            throw new IllegalArgumentException("Debe seleccionar sede de origen y sede de destino.");
        }

        if (Objects.equals(sedeOrigen, sedeDestino)) {
            throw new IllegalArgumentException("La sede de origen y destino deben ser diferentes.");
        }

        if (restriccionSede.getTiempoMinimoMinutos() == null
                || restriccionSede.getTiempoMinimoMinutos() < 0) {
            throw new IllegalArgumentException("El tiempo minimo debe ser mayor o igual a cero.");
        }

        boolean existe = restriccionSede.getIdRestriccion() == null
                ? restriccionSedeDAO.existsBySedes(sedeOrigen, sedeDestino)
                : restriccionSedeDAO.existsBySedesAndIdNot(
                        sedeOrigen,
                        sedeDestino,
                        restriccionSede.getIdRestriccion());

        if (existe) {
            throw new IllegalArgumentException("Ya existe una regla para ese traslado entre sedes.");
        }
    }
}
