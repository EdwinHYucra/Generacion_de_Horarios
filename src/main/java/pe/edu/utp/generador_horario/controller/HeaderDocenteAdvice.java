package pe.edu.utp.generador_horario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.NotificacionHeaderDTO;
import pe.edu.utp.generador_horario.entidad.SolicitudCambioHorario;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.FotoPerfilService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;
import pe.edu.utp.generador_horario.service.interfaces.SolicitudCambioHorarioService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ControllerAdvice(assignableTypes = {DocenteDashboardController.class, DisponibilidadDocenteController.class,
        CursoDocenteController.class, OpcionesHorarioController.class, SolicitudDocenteController.class,
        MiHorarioDocenteController.class})
public class HeaderDocenteAdvice {
    private final UsuarioDAO usuarioDAO; private final DocenteDAO docenteDAO;
    private final SolicitudCambioHorarioService solicitudService; private final HorarioGeneradoService horarioService;
    private final FotoPerfilService fotoPerfilService;

    public HeaderDocenteAdvice(UsuarioDAO usuarioDAO, DocenteDAO docenteDAO,
            SolicitudCambioHorarioService solicitudService, HorarioGeneradoService horarioService,
            FotoPerfilService fotoPerfilService) {
        this.usuarioDAO=usuarioDAO; this.docenteDAO=docenteDAO; this.solicitudService=solicitudService;
        this.horarioService=horarioService; this.fotoPerfilService=fotoPerfilService;
    }

    @ModelAttribute
    public void datosHeader(Map<String,Object> model, Authentication authentication) {
        if (authentication == null) return;
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName()).orElse(null);
        if (usuario == null) return;
        model.putIfAbsent("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.putIfAbsent("rolUsuario", "Docente");
        model.put("fotoPerfilDisponible", fotoPerfilService.existe(usuario.getId()));
        docenteDAO.findByUsuarioId(usuario.getId()).ifPresent(docente -> {
            List<NotificacionHeaderDTO> notificaciones = new ArrayList<>();
            solicitudService.listarPorDocente(docente.getIdDocente()).stream()
                    .filter(s -> s.getComentarioAdministrador() != null && !s.getComentarioAdministrador().isBlank())
                    .limit(4).map(this::desdeSolicitud).forEach(notificaciones::add);
            if (horarioService.buscarAprobadoPorDocente(docente.getIdDocente()).isPresent())
                notificaciones.add(new NotificacionHeaderDTO("Horario aprobado", "Su horario final ya está disponible.", "Actual", "/docente/mi-horario", "aprobado"));
            model.put("notificacionesHeader", notificaciones);
        });
    }

    private NotificacionHeaderDTO desdeSolicitud(SolicitudCambioHorario solicitud) {
        return new NotificacionHeaderDTO("Respuesta de Administración", solicitud.getComentarioAdministrador(),
                solicitud.getFechaResolucion(), "/docente/solicitudes", "respuesta");
    }
}
