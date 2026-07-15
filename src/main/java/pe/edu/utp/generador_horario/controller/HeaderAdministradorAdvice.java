package pe.edu.utp.generador_horario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.NotificacionHeaderDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.FotoPerfilService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;
import pe.edu.utp.generador_horario.service.interfaces.SolicitudCambioHorarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Datos comunes del header para todas las pantallas del rol Administrador. */
@ControllerAdvice(assignableTypes = {DashboardViewController.class, DocenteViewController.class,
        CursoViewController.class, CarreraViewController.class, CarreraCursoViewController.class,
        SedeViewController.class, AulaViewController.class, HorarioGeneradoViewController.class,
        SolicitudAdminController.class, RestriccionSedeViewController.class})
public class HeaderAdministradorAdvice {
    private final UsuarioDAO usuarioDAO;
    private final SolicitudCambioHorarioService solicitudService;
    private final HorarioGeneradoService horarioService;
    private final FotoPerfilService fotoPerfilService;

    public HeaderAdministradorAdvice(UsuarioDAO usuarioDAO, SolicitudCambioHorarioService solicitudService,
            HorarioGeneradoService horarioService, FotoPerfilService fotoPerfilService) {
        this.usuarioDAO = usuarioDAO;
        this.solicitudService = solicitudService;
        this.horarioService = horarioService;
        this.fotoPerfilService = fotoPerfilService;
    }

    @ModelAttribute
    public void datosHeader(Map<String, Object> model, Authentication authentication) {
        if (authentication == null) return;
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName()).orElse(null);
        if (usuario == null) return;
        model.put("nombreAdmin", usuario.getNombre() + " " + usuario.getApellido());
        model.put("fotoAdminDisponible", fotoPerfilService.existe(usuario.getId()));

        List<NotificacionHeaderDTO> notificaciones = new ArrayList<>();
        solicitudService.listarTodas().stream()
                .filter(s -> "PENDIENTE".equalsIgnoreCase(s.getEstadoSolicitud()))
                .limit(4)
                .map(s -> new NotificacionHeaderDTO("Solicitud pendiente",
                        s.getDocente() + " solicita revisar la opción " + s.getOpcionHorario() + ".",
                        s.getFechaRegistro(), "/administrador/solicitudes", "solicitud"))
                .forEach(notificaciones::add);
        horarioService.listarResumenes().stream()
                .filter(h -> "APROBADA_DOCENTE".equalsIgnoreCase(h.getEstado()))
                .limit(Math.max(0, 6 - notificaciones.size()))
                .map(h -> new NotificacionHeaderDTO("Horario por aprobar",
                        h.getDocente() + " confirmó la opción " + h.getOpcion() + ".",
                        h.getFechaGeneracion(), "/administrador/horarios", "horario"))
                .forEach(notificaciones::add);
        model.put("notificacionesAdmin", notificaciones);
    }
}
