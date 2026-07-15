package pe.edu.utp.generador_horario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.HorarioGeneradoResumenDTO;
import pe.edu.utp.generador_horario.dto.HorarioDetalleDTO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;

import java.util.Optional;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Muestra el horario elegido por el docente y su estado de aprobacion.
 */
@Controller
public class MiHorarioDocenteController {

    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;
    private final HorarioGeneradoService horarioGeneradoService;

    public MiHorarioDocenteController(
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO,
            HorarioGeneradoService horarioGeneradoService) {
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
        this.horarioGeneradoService = horarioGeneradoService;
    }

    @GetMapping("/docente/mi-horario")
    public String mostrar(Model model, Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        Optional<HorarioGeneradoResumenDTO> horario =
                horarioGeneradoService.buscarAprobadoPorDocente(docente.getIdDocente());

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        model.addAttribute("moduloActivo", "mi_horario");
        model.addAttribute("horario", horario.orElse(null));
        List<HorarioDetalleDTO> detalleHorario = horario
                .map(item -> horarioGeneradoService.listarDetalles(item.getIdHorario()))
                .orElseGet(List::of);
        model.addAttribute("detalleHorario", detalleHorario);
        model.addAttribute("detalleHorarioAgrupado", agruparPorCurso(detalleHorario));

        return "docente/mi_horario";
    }

    /** Agrupa las sesiones semanales para mostrar cada curso una sola vez. */
    private Map<String, List<HorarioDetalleDTO>> agruparPorCurso(List<HorarioDetalleDTO> detalles) {
        Map<String, List<HorarioDetalleDTO>> agrupados = new LinkedHashMap<>();
        for (HorarioDetalleDTO detalle : detalles) {
            String clave = detalle.getIdCurso() != null ? "curso-" + detalle.getIdCurso() : "nombre-" + detalle.getCurso();
            agrupados.computeIfAbsent(clave, ignorada -> new ArrayList<>()).add(detalle);
        }
        return agrupados;
    }
}
