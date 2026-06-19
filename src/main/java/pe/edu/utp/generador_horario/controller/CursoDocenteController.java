package pe.edu.utp.generador_horario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.SeleccionCursosRequestDTO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.CursoService;

@Controller
@RequestMapping("/docente/cursos")
public class CursoDocenteController {

    private final CursoService cursoService;
    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;
    private final DocenteCursoDAO docenteCursoDAO;

    public CursoDocenteController(
            CursoService cursoService,
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO,
            DocenteCursoDAO docenteCursoDAO) {
        this.cursoService = cursoService;
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
        this.docenteCursoDAO = docenteCursoDAO;
    }

    @GetMapping
    public String mostrarCursos(Model model, Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        String carreraDocente = docente.getCarrera();

        String palabraCarrera = carreraDocente;

        if (carreraDocente != null && carreraDocente.toLowerCase().contains("sistemas")) {
            palabraCarrera = "Sistemas";
        } else if (carreraDocente != null && carreraDocente.toLowerCase().contains("civil")) {
            palabraCarrera = "Civil";
        }

        model.addAttribute("cursosCarrera", cursoService.listarCursosDeCarreraPorCarrera(palabraCarrera));
        model.addAttribute("cursosGenerales", cursoService.listarCursosGeneralesPorCarrera(palabraCarrera));
        model.addAttribute("cursosSeleccionados", docenteCursoDAO.findCursoIdsByDocenteId(docente.getIdDocente()));

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

        docenteCursoDAO.deleteByDocenteId(docente.getIdDocente());

        if (request.getCursosSeleccionados() != null) {
            for (Long idCurso : request.getCursosSeleccionados()) {
                docenteCursoDAO.save(docente.getIdDocente(), idCurso);
            }
        }

        return ResponseEntity.ok("Cursos guardados correctamente.");
    }
}