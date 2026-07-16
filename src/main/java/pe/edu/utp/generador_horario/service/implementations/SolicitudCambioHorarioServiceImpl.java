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
    private static final String ACCION_REGISTRO_DOCENTE = "REGISTRO_DOCENTE";
    private static final String ACCION_TOMADA_REVISION = "TOMADA_EN_REVISION";
    private static final String ACCION_RESPUESTA_ADMIN = "RESPUESTA_ADMINISTRADOR";
    private static final String ACCION_HORARIO_EDITADO = "HORARIO_EDITADO";

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
    public void tomarEnRevision(Long idSolicitud, Long idAdministrador) {
        SolicitudCambioHorario solicitud = solicitudCambioHorarioDAO.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));
        if (!ESTADO_PENDIENTE.equals(solicitud.getEstadoSolicitud())) {
            throw new IllegalStateException("Solo se pueden tomar solicitudes pendientes.");
        }

        solicitudCambioHorarioDAO.tomarEnRevision(idSolicitud, idAdministrador);
        solicitudCambioHorarioDAO.registrarHistorial(
                idSolicitud,
                idAdministrador,
                solicitud.getEstadoSolicitud(),
                ESTADO_EN_REVISION,
                ACCION_TOMADA_REVISION,
                "Solicitud tomada por el administrador.");
        horarioGeneradoDAO.actualizarEstado(solicitud.getIdHorario(), ESTADO_EN_REVISION);

        LOGGER.info("Solicitud tomada en revision. idSolicitud={}, adminId={}", idSolicitud, idAdministrador);
    }

    @Override
    public void responder(Long idSolicitud, Long idAdministrador, String estado, String comentarioAdministrador) {
        validarComentario(comentarioAdministrador, "El comentario del administrador es obligatorio.");
        if (!ESTADO_APROBADA.equals(estado) && !ESTADO_RECHAZADA.equals(estado)) {
            throw new IllegalArgumentException("Estado de respuesta no permitido.");
        }

        SolicitudCambioHorario solicitud = solicitudCambioHorarioDAO.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));
        if (!ESTADO_PENDIENTE.equals(solicitud.getEstadoSolicitud())
                && !ESTADO_EN_REVISION.equals(solicitud.getEstadoSolicitud())) {
            throw new IllegalStateException("La solicitud ya fue resuelta.");
        }

        solicitudCambioHorarioDAO.responder(idSolicitud, idAdministrador, estado, comentarioAdministrador.trim());
        solicitudCambioHorarioDAO.registrarHistorial(
                idSolicitud,
                idAdministrador,
                solicitud.getEstadoSolicitud(),
                estado,
                ACCION_RESPUESTA_ADMIN,
                comentarioAdministrador.trim());

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

    @Override
    public void registrarEdicionHorario(Long idSolicitud, Long idAdministrador) {
        SolicitudCambioHorario solicitud = solicitudCambioHorarioDAO.findById(idSolicitud)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));
        solicitudCambioHorarioDAO.registrarHistorial(
                idSolicitud,
                idAdministrador,
                solicitud.getEstadoSolicitud(),
                solicitud.getEstadoSolicitud(),
                ACCION_HORARIO_EDITADO,
                "Horario modificado parcialmente desde la solicitud.");
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
        solicitudCambioHorarioDAO.registrarHistorial(
                idSolicitud,
                null,
                null,
                ESTADO_PENDIENTE,
                ACCION_REGISTRO_DOCENTE,
                comentario.trim());
        horarioGeneradoDAO.actualizarEstado(idHorario, estadoHorario);
        if (TIPO_RECHAZO.equals(tipoSolicitud)) {
            horarioGeneradoDAO.descartarPendientesDeDocenteExcepto(idHorario, idDocente);
        }
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
