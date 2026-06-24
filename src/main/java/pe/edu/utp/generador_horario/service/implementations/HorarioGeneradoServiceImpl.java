package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dao.HorarioGeneradoDAO;
import pe.edu.utp.generador_horario.dto.HorarioDetalleDTO;
import pe.edu.utp.generador_horario.dto.HorarioGeneradoResumenDTO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;
import pe.edu.utp.generador_horario.service.interfaces.OpcionesHorarioService;

import java.util.List;
import java.util.Optional;

/**
 * Servicio administrativo para persistir opciones generadas por el algoritmo.
 *
 * <p>SOLID aplicado: este servicio separa la orquestacion de persistencia del
 * algoritmo. El generador solo calcula opciones y este servicio decide como se
 * guardan para revision administrativa.</p>
 */
@Service
public class HorarioGeneradoServiceImpl implements HorarioGeneradoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorarioGeneradoServiceImpl.class);
    private static final String ESTADO_PENDIENTE = "PENDIENTE";

    private final DocenteDAO docenteDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final DocenteCursoDAO docenteCursoDAO;
    private final DisponibilidadDocenteDAO disponibilidadDocenteDAO;
    private final HorarioGeneradoDAO horarioGeneradoDAO;
    private final OpcionesHorarioService opcionesHorarioService;

    public HorarioGeneradoServiceImpl(
            DocenteDAO docenteDAO,
            CicloAcademicoDAO cicloAcademicoDAO,
            DocenteCursoDAO docenteCursoDAO,
            DisponibilidadDocenteDAO disponibilidadDocenteDAO,
            HorarioGeneradoDAO horarioGeneradoDAO,
            OpcionesHorarioService opcionesHorarioService) {
        this.docenteDAO = docenteDAO;
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.docenteCursoDAO = docenteCursoDAO;
        this.disponibilidadDocenteDAO = disponibilidadDocenteDAO;
        this.horarioGeneradoDAO = horarioGeneradoDAO;
        this.opcionesHorarioService = opcionesHorarioService;
    }

    @Override
    public int generarParaDocente(Long idDocente) {
        horarioGeneradoDAO.eliminarPendientesPorDocente(idDocente);

        List<OpcionesHorarioDTO> opciones = opcionesHorarioService.generarHorarios(idDocente);
        int guardadas = 0;

        for (OpcionesHorarioDTO opcion : opciones) {
            if (opcion.getBloques() == null || opcion.getBloques().isEmpty()) {
                continue;
            }

            Long idHorario = horarioGeneradoDAO.saveHorario(idDocente, opcion, ESTADO_PENDIENTE);
            for (HorarioDetalleDTO detalle : opcion.getBloques()) {
                horarioGeneradoDAO.saveDetalle(idHorario, detalle);
            }
            guardadas++;
        }

        LOGGER.info("Horarios generados para docente. idDocente={}, opcionesGuardadas={}", idDocente, guardadas);
        return guardadas;
    }

    @Override
    public int generarSiTieneInsumos(Long idDocente) {
        Optional<Long> cicloActivo = cicloAcademicoDAO.findIdActivo();
        if (cicloActivo.isEmpty()) {
            LOGGER.warn("No se auto-genero horario: no existe ciclo activo. docenteId={}", idDocente);
            return 0;
        }

        boolean tieneCursos = !docenteCursoDAO
                .findCursoIdsByDocenteIdAndCicloId(idDocente, cicloActivo.get())
                .isEmpty();
        boolean tieneDisponibilidad = !disponibilidadDocenteDAO
                .findByDocenteIdAndCicloId(idDocente, cicloActivo.get())
                .isEmpty();

        if (!tieneCursos || !tieneDisponibilidad) {
            LOGGER.info("Auto-generacion omitida por insumos incompletos. docenteId={}, cursos={}, disponibilidad={}",
                    idDocente, tieneCursos, tieneDisponibilidad);
            return 0;
        }

        return generarParaDocente(idDocente);
    }

    @Override
    public int generarParaTodos() {
        int total = 0;
        for (Docente docente : docenteDAO.findAll()) {
            if (Boolean.TRUE.equals(docente.getEstado())) {
                total += generarParaDocente(docente.getIdDocente());
            }
        }
        LOGGER.info("Generacion masiva finalizada. opcionesGuardadas={}", total);
        return total;
    }

    @Override
    public List<HorarioGeneradoResumenDTO> listarResumenes() {
        return horarioGeneradoDAO.listarResumenes();
    }

    @Override
    public List<OpcionesHorarioDTO> listarOpcionesPendientesPorDocente(Long idDocente) {
        return horarioGeneradoDAO.listarPendientesPorDocente(idDocente)
                .stream()
                .map(resumen -> {
                    OpcionesHorarioDTO opcion = new OpcionesHorarioDTO();
                    opcion.setIdHorario(resumen.getIdHorario());
                    opcion.setOpcion(resumen.getOpcion());
                    opcion.setBloques(horarioGeneradoDAO.listarDetalles(resumen.getIdHorario()));
                    return opcion;
                })
                .toList();
    }

    @Override
    public Optional<HorarioGeneradoResumenDTO> buscarAprobadoPorDocente(Long idDocente) {
        return horarioGeneradoDAO.buscarAprobadoPorDocente(idDocente);
    }

    @Override
    public List<HorarioDetalleDTO> listarDetalles(Long idHorario) {
        return horarioGeneradoDAO.listarDetalles(idHorario);
    }

    @Override
    public void aprobar(Long idHorario) {
        horarioGeneradoDAO.aprobar(idHorario);
        LOGGER.info("Horario aprobado. idHorario={}", idHorario);
    }

    @Override
    public void confirmarSeleccionDocente(Long idHorario, Long idDocente) {
        if (!horarioGeneradoDAO.existePorDocente(idHorario, idDocente)) {
            LOGGER.warn("Confirmacion rechazada: horario no pertenece al docente. idHorario={}, idDocente={}",
                    idHorario, idDocente);
            throw new IllegalArgumentException("La opcion seleccionada no pertenece al docente autenticado.");
        }

        horarioGeneradoDAO.aprobar(idHorario);
        LOGGER.info("Horario confirmado por docente. idHorario={}, idDocente={}", idHorario, idDocente);
    }
}
