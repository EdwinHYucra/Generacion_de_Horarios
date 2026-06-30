package pe.edu.utp.generador_horario.service.interfaces;

import pe.edu.utp.generador_horario.entidad.SolicitudCambioHorario;

import java.util.List;

public interface SolicitudCambioHorarioService {

    void observar(Long idHorario, Long idDocente, String comentario);

    void rechazar(Long idHorario, Long idDocente, String comentario);

    List<SolicitudCambioHorario> listarPorDocente(Long idDocente);

    List<SolicitudCambioHorario> listarTodas();

    void responder(Long idSolicitud, Long idAdministrador, String estado, String comentarioAdministrador);
}
