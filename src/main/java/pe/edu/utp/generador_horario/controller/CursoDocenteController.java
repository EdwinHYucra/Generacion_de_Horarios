package pe.edu.utp.generador_horario.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.SeleccionCursosRequestDTO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;

/**
 * Controlador del modulo docente para seleccionar cursos disponibles.
 */
@Controller
@RequestMapping("/docente/cursos")
public class CursoDocenteController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CursoDocenteController.class);

    private final CursoService cursoService;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;
    private final DocenteCursoDAO docenteCursoDAO;
    private final HorarioGeneradoService horarioGeneradoService;

    public CursoDocenteController(
            CursoService cursoService,
            CicloAcademicoDAO cicloAcademicoDAO,
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO,
            DocenteCursoDAO docenteCursoDAO,
            HorarioGeneradoService horarioGeneradoService) {
        this.cursoService = cursoService;
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
        this.docenteCursoDAO = docenteCursoDAO;
        this.horarioGeneradoService = horarioGeneradoService;
    }

    @GetMapping
    public String mostrarCursos(Model model, Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        Long cicloActivoId = obtenerCicloActivoId();

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        model.addAttribute("moduloActivo", "cursos");
        String carreraDocente = docente.getCarrera();

        String palabraCarrera = carreraDocente;

        if (carreraDocente != null && carreraDocente.toLowerCase().contains("sistemas")) {
            palabraCarrera = "Sistemas";
        } else if (carreraDocente != null && carreraDocente.toLowerCase().contains("civil")) {
            palabraCarrera = "Civil";
        }

        model.addAttribute("cursosCarrera", cursoService.listarCursosDeCarreraPorCarrera(palabraCarrera));
        model.addAttribute("cursosGenerales", cursoService.listarCursosGeneralesPorCarrera(palabraCarrera));
        model.addAttribute("cursosSeleccionados",
                docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId));

        return "docente/cursos";
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<String> guardarCursos(
            @RequestBody SeleccionCursosRequestDTO request,
            Authentication authentication) {

        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        Long cicloActivoId = obtenerCicloActivoId();

        docenteCursoDAO.deleteByDocenteIdAndCicloId(docente.getIdDocente(), cicloActivoId);

        if (request.getCursosSeleccionados() != null) {
            for (Long idCurso : request.getCursosSeleccionados()) {
                docenteCursoDAO.save(docente.getIdDocente(), idCurso, cicloActivoId);
            }
        }

        int cantidad = request.getCursosSeleccionados() == null ? 0 : request.getCursosSeleccionados().size();
        LOGGER.info("Cursos seleccionados actualizados. docenteId={}, cicloId={}, cursos={}",
                docente.getIdDocente(), cicloActivoId, cantidad);

        int opciones = horarioGeneradoService.generarSiTieneInsumos(docente.getIdDocente());

        return ResponseEntity.ok(opciones > 0
                ? "Cursos guardados. Se generaron " + opciones + " opciones de horario."
                : "Cursos guardados. El horario se generara cuando tambien exista disponibilidad registrada.");
    }

    private Long obtenerCicloActivoId() {
        return cicloAcademicoDAO.findIdActivo()
                .orElseThrow(() -> new IllegalStateException("No existe un ciclo academico activo."));
    }
}
