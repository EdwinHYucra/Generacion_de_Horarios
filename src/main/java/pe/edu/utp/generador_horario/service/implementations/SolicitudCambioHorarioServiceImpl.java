package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.dao.HorarioGeneradoDAO;
import pe.edu.utp.generador_horario.dao.SolicitudCambioHorarioDAO;
import pe.edu.utp.generador_horario.entidad.SolicitudCambioHorario;
import pe.edu.utp.generador_horario.service.interfaces.SolicitudCambioHorarioService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SolicitudCambioHorarioServiceImpl implements SolicitudCambioHorarioService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudCambioHorarioServiceImpl.class);
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_APROBADA = "APROBADA";
    private static final String ESTADO_RECHAZADA = "RECHAZADA";
    private static final String ESTADO_DESCARTADO = "DESCARTADO";
    private static final String ESTADO_EN_REVISION = "EN_REVISION";
    private static final String TIPO_OBSERVACION = "OBSERVACION";
    private static final String TIPO_RECHAZO = "RECHAZO";

    private final SolicitudCambioHorarioDAO solicitudCambioHorarioDAO;
    private final HorarioGeneradoDAO horarioGeneradoDAO;

    public SolicitudCambioHorarioServiceImpl(
            SolicitudCambioHorarioDAO solicitudCambioHorarioDAO,
            HorarioGeneradoDAO horarioGeneradoDAO) {
        this.solicitudCambioHorarioDAO = solicitudCambioHorarioDAO;
        this.horarioGeneradoDAO = horarioGeneradoDAO;
    }

    @Override
    public void observar(Long idHorario, Long idDocente, String comentario) {
        registrar(idHorario, idDocente, comentario, TIPO_OBSERVACION, "EN_REVISION");
    }

    @Override
    public void rechazar(Long idHorario, Long idDocente, String comentario) {
        registrar(idHorario, idDocente, comentario, TIPO_RECHAZO, "RECHAZADA");
    }

    @Override
    public List<SolicitudCambioHorario> listarPorDocente(Long idDocente) {
        return solicitudCambioHorarioDAO.findByDocente(idDocente);
    }

    @Override
    public List<SolicitudCambioHorario> listarTodas() {
        return solicitudCambioHorarioDAO.findAll();
    }

    @Override
    public void responder(Long idSolicitud, Long idAdministrador, String estado, String comentarioAdministrador) {
        validarComentario(comentarioAdministrador, "El comentario del administrador es obligatorio.");
        if (!ESTADO_APROBADA.equals(estado) && !ESTADO_RECHAZADA.equals(estado)) {
            throw new IllegalArgumentException("Estado de respuesta no permitido.");
        }

        SolicitudCambioHorario solicitud = solicitudCambioHorarioDAO.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));

        solicitudCambioHorarioDAO.responder(idSolicitud, idAdministrador, estado, comentarioAdministrador.trim());

        if (ESTADO_APROBADA.equals(estado) && TIPO_OBSERVACION.equals(solicitud.getTipoSolicitud())) {
            horarioGeneradoDAO.aprobar(solicitud.getIdHorario());
        } else if (ESTADO_APROBADA.equals(estado) && TIPO_RECHAZO.equals(solicitud.getTipoSolicitud())) {
            horarioGeneradoDAO.actualizarEstado(solicitud.getIdHorario(), ESTADO_DESCARTADO);
        } else if (ESTADO_RECHAZADA.equals(estado) && TIPO_OBSERVACION.equals(solicitud.getTipoSolicitud())) {
            horarioGeneradoDAO.actualizarEstado(solicitud.getIdHorario(), ESTADO_EN_REVISION);
        }

        LOGGER.info("Solicitud de cambio respondida. idSolicitud={}, estado={}, adminId={}",
                idSolicitud, estado, idAdministrador);
    }

    private void registrar(
            Long idHorario,
            Long idDocente,
            String comentario,
            String tipoSolicitud,
            String estadoHorario) {

        validarComentario(comentario, "Debe registrar una justificacion para continuar.");
        if (!horarioGeneradoDAO.existePorDocente(idHorario, idDocente)) {
            throw new IllegalArgumentException("La propuesta seleccionada no pertenece al docente autenticado.");
        }

        SolicitudCambioHorario solicitud = new SolicitudCambioHorario();
        solicitud.setCodigoSolicitud(generarCodigo());
        solicitud.setIdHorario(idHorario);
        solicitud.setIdDocente(idDocente);
        solicitud.setComentarioDocente(comentario.trim());
        solicitud.setTipoSolicitud(tipoSolicitud);
        solicitud.setEstadoSolicitud(ESTADO_PENDIENTE);

        Long idSolicitud = solicitudCambioHorarioDAO.registrar(solicitud);
        horarioGeneradoDAO.actualizarEstado(idHorario, estadoHorario);
        LOGGER.info("Solicitud de cambio registrada. idSolicitud={}, idHorario={}, docenteId={}, tipo={}",
                idSolicitud, idHorario, idDocente, tipoSolicitud);
    }

    private void validarComentario(String comentario, String mensaje) {
        if (comentario == null || comentario.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    private String generarCodigo() {
        return "SOL-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }
}
