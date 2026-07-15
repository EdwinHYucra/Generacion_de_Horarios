package pe.edu.utp.generador_horario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;
import pe.edu.utp.generador_horario.service.interfaces.DocenteService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;

@Controller
public class DashboardViewController {

    private final DocenteService docenteService;
    private final CursoService cursoService;
    private final HorarioGeneradoService horarioGeneradoService;

    public DashboardViewController(
            DocenteService docenteService,
            CursoService cursoService,
            HorarioGeneradoService horarioGeneradoService) {
        this.docenteService = docenteService;
        this.cursoService = cursoService;
        this.horarioGeneradoService = horarioGeneradoService;
    }

    @GetMapping("/administrador/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("moduloActivo", "dashboard");
        model.addAttribute("totalDocentes", docenteService.listarDocentes().size());
        model.addAttribute("totalCursosActivos", cursoService.listarCursos().stream()
                .filter(curso -> Boolean.TRUE.equals(curso.getEstado()))
                .count());
        model.addAttribute("totalHorariosPendientes", horarioGeneradoService.listarResumenes().stream()
                .filter(horario -> "APROBADA_DOCENTE".equals(horario.getEstado()))
                .count());
        return "dashboard/index";
    }
}

