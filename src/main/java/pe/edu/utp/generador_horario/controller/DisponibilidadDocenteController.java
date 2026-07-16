package pe.edu.utp.generador_horario.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.CicloAcademicoDAO;
import pe.edu.utp.generador_horario.dao.DocenteCursoDAO;
import pe.edu.utp.generador_horario.dao.DisponibilidadDocenteDAO;
import pe.edu.utp.generador_horario.dto.BloqueDisponibilidadDTO;
import pe.edu.utp.generador_horario.dto.DisponibilidadRequestDTO;
import pe.edu.utp.generador_horario.entidad.Docente;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.interfaces.DisponibilidadDocenteService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneracionAsyncService;

import java.util.List;

@Controller
@RequestMapping("/docente/disponibilidad")
public class DisponibilidadDocenteController {

    private final DisponibilidadDocenteService disponibilidadService;
    private final UsuarioDAO usuarioDAO;
    private final DocenteDAO docenteDAO;
    private final HorarioGeneradoService horarioGeneradoService;
    private final HorarioGeneracionAsyncService horarioGeneracionAsyncService;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final DocenteCursoDAO docenteCursoDAO;
    private final DisponibilidadDocenteDAO disponibilidadDocenteDAO;

    public DisponibilidadDocenteController(
            DisponibilidadDocenteService disponibilidadService,
            UsuarioDAO usuarioDAO,
            DocenteDAO docenteDAO,
            HorarioGeneradoService horarioGeneradoService,
            HorarioGeneracionAsyncService horarioGeneracionAsyncService,
            CicloAcademicoDAO cicloAcademicoDAO,
            DocenteCursoDAO docenteCursoDAO,
            DisponibilidadDocenteDAO disponibilidadDocenteDAO) {
        this.disponibilidadService = disponibilidadService;
        this.usuarioDAO = usuarioDAO;
        this.docenteDAO = docenteDAO;
        this.horarioGeneradoService = horarioGeneradoService;
        this.horarioGeneracionAsyncService = horarioGeneracionAsyncService;
        this.cicloAcademicoDAO = cicloAcademicoDAO;
        this.docenteCursoDAO = docenteCursoDAO;
        this.disponibilidadDocenteDAO = disponibilidadDocenteDAO;
    }

    @GetMapping
    public String mostrarDisponibilidad(Model model, Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("nombreUsuario", usuario.getNombre() + " " + usuario.getApellido());
        model.addAttribute("rolUsuario", "Docente");
        model.addAttribute("moduloActivo", "disponibilidad");
        model.addAttribute("seleccionBloqueada", seleccionCompleta(obtenerDocenteId(authentication)));

        return "docente/disponibilidad";
    }

    @GetMapping("/listar")
    @ResponseBody
    public List<BloqueDisponibilidadDTO> listar(Authentication authentication) {
        return disponibilidadService.listarPorEmail(authentication.getName());
    }

    @PostMapping("/guardar")
    @ResponseBody
    public ResponseEntity<String> guardar(
            @RequestBody DisponibilidadRequestDTO request,
            Authentication authentication) {
        if (seleccionCompleta(obtenerDocenteId(authentication))) {
            return ResponseEntity.status(409).body(
                    "La disponibilidad y los cursos ya fueron confirmados para este ciclo.");
        }

        disponibilidadService.guardarPorEmail(
                authentication.getName(),
                request.getBloques());

        horarioGeneracionAsyncService.programarGeneracion(obtenerDocenteId(authentication));

        return ResponseEntity.ok(
                "Disponibilidad guardada correctamente.");
    }

    private Long obtenerDocenteId(Authentication authentication) {
        Usuario usuario = usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Docente docente = docenteDAO.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        return docente.getIdDocente();
    }

    private boolean seleccionCompleta(Long docenteId) {
        Long cicloId = cicloAcademicoDAO.findIdActivo().orElse(null);
        return cicloId != null
                && !docenteCursoDAO.findCursoIdsByDocenteIdAndCicloId(docenteId, cicloId).isEmpty()
                && !disponibilidadDocenteDAO.findByDocenteIdAndCicloId(docenteId, cicloId).isEmpty();
    }
}
