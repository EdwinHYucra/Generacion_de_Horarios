package pe.edu.utp.generador_horario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;

@Controller
public class DocenteDashboardController {

    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final DisponibilidadDocenteDAO disponibilidadDocenteDAO;
    private final DocenteCursoDAO docenteCursoDAO;
    private final HorarioGeneradoService horarioGeneradoService;

    public DocenteDashboardController(
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO,
            CicloAcademicoDAO cicloAcademicoDAO,
            DisponibilidadDocenteDAO disponibilidadDocenteDAO,
            DocenteCursoDAO docenteCursoDAO,
            HorarioGeneradoService horarioGeneradoService) {
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.disponibilidadDocenteDAO = disponibilidadDocenteDAO;
        this.docenteCursoDAO = docenteCursoDAO;
        this.horarioGeneradoService = horarioGeneradoService;
    }

    @GetMapping("/docente/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        Long cicloActivoId = cicloAcademicoDAO.findIdActivo().orElse(null);
        boolean disponibilidadRegistrada = cicloActivoId != null
                && !disponibilidadDocenteDAO
                        .findByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId)
                        .isEmpty();
        boolean cursosSeleccionados = cicloActivoId != null
                && !docenteCursoDAO
                        .findCursoIdsByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId)
                        .isEmpty();
        boolean horarioAsignado = horarioGeneradoService
                .buscarAprobadoPorDocente(docente.getIdDocente())
                .isPresent();

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        model.addAttribute("moduloActivo", "dashboard");
        model.addAttribute("disponibilidadRegistrada", disponibilidadRegistrada);
        model.addAttribute("cursosSeleccionados", cursosSeleccionados);
        model.addAttribute("horarioAsignado", horarioAsignado);

        return "docente/dashboard";
    }
}
