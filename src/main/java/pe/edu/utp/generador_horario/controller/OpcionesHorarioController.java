package pe.edu.utp.generador_horario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.OpcionesHorarioDTO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;
import pe.edu.utp.generador_horario.service.interfaces.SolicitudCambioHorarioService;

import java.util.Comparator;
import java.util.List;

@Controller
public class OpcionesHorarioController {

    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;
    private final HorarioGeneradoService horarioGeneradoService;
    private final SolicitudCambioHorarioService solicitudCambioHorarioService;

    public OpcionesHorarioController(
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO,
            HorarioGeneradoService horarioGeneradoService,
            SolicitudCambioHorarioService solicitudCambioHorarioService) {

        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
        this.horarioGeneradoService = horarioGeneradoService;
        this.solicitudCambioHorarioService = solicitudCambioHorarioService;
    }

    @GetMapping("/docente/opciones_horario")
    public String mostrarVista(Model model, Authentication authentication) {
        Usuario usuario = obtenerUsuario(authentication);
        Docente docente = obtenerDocente(usuario);

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        model.addAttribute("moduloActivo", "opciones_horario");
        model.addAttribute("diasSemana", List.of("Lunes", "Martes", "Miercoles", "Jueves", "Viernes"));

        List<OpcionesHorarioDTO> opciones =
                horarioGeneradoService.listarOpcionesPendientesPorDocente(docente.getIdDocente());

        if (opciones.isEmpty()) {
            horarioGeneradoService.generarSiTieneInsumos(docente.getIdDocente());
            opciones = horarioGeneradoService.listarOpcionesPendientesPorDocente(docente.getIdDocente());
        }

        model.addAttribute("bloquesHora", obtenerHorasDeInicio(opciones));
        model.addAttribute("opcionesHorario", opciones);

        return "docente/opciones_horario";
    }

    @PostMapping("/docente/opciones_horario/confirmar")
    public String confirmar(
            @RequestParam("idHorario") Long idHorario,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerUsuario(authentication);
        Docente docente = obtenerDocente(usuario);

        horarioGeneradoService.confirmarSeleccionDocente(idHorario, docente.getIdDocente());
        redirectAttributes.addFlashAttribute("mensajeExito", "Propuesta aprobada por docente. Queda pendiente de aprobacion administrativa.");
        return "redirect:/docente/solicitudes";
    }

    @PostMapping("/docente/opciones_horario/observar")
    public String observar(
            @RequestParam("idHorario") Long idHorario,
            @RequestParam("comentario") String comentario,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerUsuario(authentication);
        Docente docente = obtenerDocente(usuario);

        solicitudCambioHorarioService.observar(idHorario, docente.getIdDocente(), comentario);
        redirectAttributes.addFlashAttribute("mensajeExito", "Observacion enviada a administracion.");
        return "redirect:/docente/solicitudes";
    }

    @PostMapping("/docente/opciones_horario/rechazar")
    public String rechazar(
            @RequestParam("idHorario") Long idHorario,
            @RequestParam("comentario") String comentario,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = obtenerUsuario(authentication);
        Docente docente = obtenerDocente(usuario);

        solicitudCambioHorarioService.rechazar(idHorario, docente.getIdDocente(), comentario);
        redirectAttributes.addFlashAttribute("mensajeExito", "Rechazo enviado a administracion.");
        return "redirect:/docente/solicitudes";
    }

    private Usuario obtenerUsuario(Authentication authentication) {
        return usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private Docente obtenerDocente(Usuario usuario) {
        return docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
    }

    private List<String> obtenerHorasDeInicio(List<OpcionesHorarioDTO> opciones) {
        List<String> horas = opciones.stream()
                .flatMap(opcion -> opcion.getBloques().stream())
                .map(bloque -> bloque.getHoraInicio())
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();

        return horas.isEmpty()
                ? List.of("07:00", "09:00", "11:00", "13:00")
                : horas;
    }
}
