package com.utp.generacionhorarios.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.utp.generacionhorarios.entity.Usuario;
import com.utp.generacionhorarios.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private final UsuarioRepository usuarioRepository;

    public DashboardController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username,
                                @RequestParam String password,
                                HttpSession session) {

        Usuario usuario = usuarioRepository.findByCorreo(username).orElse(null);

        if (usuario == null) {
            return "redirect:/login?error";
        }

        if (!usuario.getPassword().equals(password)) {
            return "redirect:/login?error";
        }

        // GUARDAR SESIÓN
        session.setAttribute("usuario", usuario);

        // REDIRECCIÓN POR ROL
        if ("ADMIN".equals(usuario.getRol())) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/docente/dashboard";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // =========================
    // ADMIN DASHBOARD (PROTEGIDO)
    // =========================
    @GetMapping("/admin/dashboard")
    public String adminDashboard(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null || !"ADMIN".equals(usuario.getRol())) {
            return "redirect:/login";
        }

        return "dashboard_administrador";
    }

    // =========================
    // DOCENTE DASHBOARD (PROTEGIDO)
    // =========================
    @GetMapping("/docente/dashboard")
    public String docenteDashboard(HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null || !"DOCENTE".equals(usuario.getRol())) {
            return "redirect:/login";
        }

        return "dashboard_docente";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}