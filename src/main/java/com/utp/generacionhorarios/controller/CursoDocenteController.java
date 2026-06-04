package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.dto.SeleccionCursosDTO;
import com.utp.generacionhorarios.service.CursoDocenteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/docente/cursos")
public class CursoDocenteController {

    private final CursoDocenteService cursoDocenteService;

    public CursoDocenteController(CursoDocenteService cursoDocenteService) {
        this.cursoDocenteService = cursoDocenteService;
    }

    @GetMapping
    public String mostrarCursos(Model model, HttpSession session) {

        Long docenteId = obtenerDocenteIdDesdeSesion(session);
        String nombreDocente = obtenerNombreUsuarioAutenticado();

        model.addAttribute("nombreDocente", nombreDocente);
        model.addAttribute("rolDocente", "Docente");

        model.addAttribute("docenteId", docenteId);
        model.addAttribute("cursosCarrera", cursoDocenteService.obtenerCursosCarrera());
        model.addAttribute("cursosGenerales", cursoDocenteService.obtenerCursosGenerales());
        model.addAttribute("cursosSeleccionados",
                cursoDocenteService.obtenerCursosSeleccionadosPorDocente(docenteId));
        model.addAttribute("seleccionCursosDTO", new SeleccionCursosDTO());

        return "cursos";
    }

    @PostMapping("/guardar")
    public String guardarCursos(
            @ModelAttribute SeleccionCursosDTO seleccionCursosDTO,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session) {

        try {
            Long docenteId = obtenerDocenteIdDesdeSesion(session);
            seleccionCursosDTO.setDocenteId(docenteId);

            cursoDocenteService.guardarCursosSeleccionados(seleccionCursosDTO);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Cursos seleccionados correctamente.");

            return "redirect:/docente/cursos";

        } catch (IllegalArgumentException e) {

            Long docenteId = obtenerDocenteIdDesdeSesion(session);
            String nombreDocente = obtenerNombreUsuarioAutenticado();

            model.addAttribute("nombreDocente", nombreDocente);
            model.addAttribute("rolDocente", "Docente");

            model.addAttribute("error", e.getMessage());
            model.addAttribute("docenteId", docenteId);
            model.addAttribute("cursosCarrera", cursoDocenteService.obtenerCursosCarrera());
            model.addAttribute("cursosGenerales", cursoDocenteService.obtenerCursosGenerales());
            model.addAttribute("cursosSeleccionados",
                    cursoDocenteService.obtenerCursosSeleccionadosPorDocente(docenteId));
            model.addAttribute("seleccionCursosDTO", seleccionCursosDTO);

            return "cursos";
        }
    }

    private Long obtenerDocenteIdDesdeSesion(HttpSession session) {
        Object docenteId = session.getAttribute("docenteId");

        if (docenteId != null) {
            return Long.valueOf(docenteId.toString());
        }

        return 1L;
    }

    private String obtenerNombreUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            return "Juan Pérez";
        }

        return auth.getName();
    }
}