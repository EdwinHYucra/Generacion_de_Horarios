package pe.edu.utp.generador_horario.dao;

import pe.edu.utp.generador_horario.entidad.SolicitudCambioHorario;

import java.util.List;
import java.util.Optional;

public interface SolicitudCambioHorarioDAO {

    Long registrar(SolicitudCambioHorario solicitud);

    Optional<SolicitudCambioHorario> findById(Long idSolicitud);

    List<SolicitudCambioHorario> findByDocente(Long idDocente);

    List<SolicitudCambioHorario> findAll();

    void responder(Long idSolicitud, Long idAdministrador, String estado, String comentarioAdministrador);
}
