
package com.utp.generacionhorarios.controller;

import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;

/**
 * Controlador encargado del inicio de sesión
 * y la redirección de usuarios según su rol.
 *
 * Gestiona el acceso a los dashboards de
 * administrador y docente.
 *
 * @author Dayanna
 */

@Controller
public class DashboardController {

    // =========================
    // LOGIN
    // =========================

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // =========================
    // REDIRECCIÓN POR ROL
    // =========================

    /**
     * Redirecciona al usuario autenticado
     * al dashboard correspondiente según
     * el rol asignado en el sistema.
     *
     * @return redirección al dashboard de
     *         administrador o docente
     */

    @GetMapping("/redireccionar")
    public String redireccionarSegunRol() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        boolean esAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (esAdmin) {
            return "redirect:/admin/dashboard";
        }

        return "redirect:/docente/dashboard";
    }

    // =========================
    // ADMIN DASHBOARD
    // =========================

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {

        return "dashboard_administrador";
    }

    // =========================
    // DOCENTE DASHBOARD
    // =========================

    @GetMapping("/docente/dashboard")
    public String docenteDashboard(
            Model model,
            Authentication auth) {

        model.addAttribute(
                "username",
                auth.getName());

        return "dashboard_docente";
    }
}
