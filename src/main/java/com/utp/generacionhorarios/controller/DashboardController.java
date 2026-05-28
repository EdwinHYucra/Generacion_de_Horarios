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

    @PostMapping("/login")
public String procesarLogin(@RequestParam String username,
                            @RequestParam String password) {

    System.out.println("USERNAME: " + username);
    System.out.println("PASSWORD: " + password);

    Usuario usuario = usuarioRepository.findByCorreo(username).orElse(null);

    System.out.println("USUARIO EN BD: " + usuario);

    if (usuario == null) {
        System.out.println("USUARIO NO ENCONTRADO");
        return "redirect:/login?error";
    }

    System.out.println("PASS BD: " + usuario.getPassword());

    if (!usuario.getPassword().equals(password)) {
        System.out.println("PASSWORD INCORRECTA");
        return "redirect:/login?error";
    }

    System.out.println("LOGIN OK");

    return usuario.getRol().equals("ADMIN")
            ? "redirect:/admin/dashboard"
            : "redirect:/docente/dashboard";
}

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "dashboard_administrador";
    }

    @GetMapping("/docente/dashboard")
    public String docenteDashboard() {
        return "dashboard_docente";
    }
    @GetMapping("/logout")
public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/login";
}
}