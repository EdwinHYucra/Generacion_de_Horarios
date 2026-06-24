package pe.edu.utp.generador_horario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import pe.edu.utp.generador_horario.config.RutasSistema;

/**
 * Controlador de autenticacion y entrada inicial del sistema.
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:" + RutasSistema.SUPERADMIN_DASHBOARD;
    }
}
