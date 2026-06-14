package pe.edu.utp.generador_horario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardViewController {

    @GetMapping("/administrador/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("moduloActivo", "dashboard");
        return "dashboard/index";
    }
}

