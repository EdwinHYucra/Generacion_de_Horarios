package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.SolicitudCambioHorario;

import java.util.List;
import java.util.Optional;

public interface SolicitudCambioHorarioDAO {

    Long registrar(SolicitudCambioHorario solicitud);

    Optional<SolicitudCambioHorario> findById(Long idSolicitud);

    List<SolicitudCambioHorario> findByDocente(Long idDocente);

    List<SolicitudCambioHorario> findAll();

    void tomarEnRevision(Long idSolicitud, Long idAdministrador);

    void responder(Long idSolicitud, Long idAdministrador, String estado, String comentarioAdministrador);

    void registrarHistorial(
            Long idSolicitud,
            Long idAdministrador,
            String estadoAnterior,
            String estadoNuevo,
            String accion,
            String comentario);
}
