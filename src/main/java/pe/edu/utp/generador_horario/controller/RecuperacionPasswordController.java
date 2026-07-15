package pe.edu.utp.generador_horario.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.dao.DocenteDAO;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.AvisoSeguridadEmailService;

import java.time.Instant;

@Controller
public class RecuperacionPasswordController {
    private static final String USUARIO = "recuperacionUsuarioId", EMAIL = "recuperacionEmail",
            EXPIRA = "recuperacionExpira", INTENTOS = "recuperacionIntentos";
    private final UsuarioDAO usuarioDAO; private final DocenteDAO docenteDAO; private final PasswordEncoder passwordEncoder;
    private final AvisoSeguridadEmailService avisoEmailService;

    public RecuperacionPasswordController(UsuarioDAO usuarioDAO, DocenteDAO docenteDAO, PasswordEncoder passwordEncoder,
            AvisoSeguridadEmailService avisoEmailService) {
        this.usuarioDAO=usuarioDAO; this.docenteDAO=docenteDAO; this.passwordEncoder=passwordEncoder;
        this.avisoEmailService=avisoEmailService;
    }

    @GetMapping("/recuperar-contrasena")
    public String formulario() { return "auth/recuperar_contrasena"; }

    @PostMapping("/recuperar-contrasena")
    public String verificar(@RequestParam String usuario, @RequestParam String dni, HttpSession session,
            RedirectAttributes redirectAttributes) {
        int intentos = session.getAttribute(INTENTOS) instanceof Integer valor ? valor : 0;
        if (intentos >= 5) {
            redirectAttributes.addFlashAttribute("errorRecuperacion", "Se alcanzó el límite de intentos. Inténtelo nuevamente más tarde.");
            return "redirect:/recuperar-contrasena";
        }
        session.setAttribute(INTENTOS, intentos + 1);
        String usuarioLimpio = usuario == null ? "" : usuario.trim();
        String dniLimpio = dni == null ? "" : dni.replaceAll("\\D", "");
        Usuario encontrado = usuarioDAO.buscarPorUsuarioInstitucional(usuarioLimpio).orElse(null);
        boolean coincide = encontrado != null && docenteDAO.findByUsuarioId(encontrado.getId())
                .map(docente -> docente.getDni() != null && docente.getDni().equals(dniLimpio)).orElse(false);
        if (!coincide) {
            redirectAttributes.addFlashAttribute("errorRecuperacion", "No fue posible validar los datos ingresados.");
            return "redirect:/recuperar-contrasena";
        }
        session.setAttribute(USUARIO, encontrado.getId()); session.setAttribute(EMAIL, encontrado.getEmail());
        session.setAttribute(EXPIRA, Instant.now().plusSeconds(600).toEpochMilli()); session.removeAttribute(INTENTOS);
        return "redirect:/restablecer-contrasena";
    }

    @GetMapping("/restablecer-contrasena")
    public String restablecer(HttpSession session, Model model) {
        if (!sesionValida(session)) return "redirect:/recuperar-contrasena";
        return "auth/restablecer_contrasena";
    }

    @PostMapping("/restablecer-contrasena")
    public String guardar(@RequestParam String password,
            @RequestParam String confirmarPassword, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sesionValida(session)) {
            limpiar(session); redirectAttributes.addFlashAttribute("errorRecuperacion", "La sesión de recuperación expiró.");
            return "redirect:/recuperar-contrasena";
        }
        if (!password.equals(confirmarPassword)) {
            redirectAttributes.addFlashAttribute("errorPassword", "Las contraseñas no coinciden.");
            return "redirect:/restablecer-contrasena";
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,64}$")) {
            redirectAttributes.addFlashAttribute("errorPassword", "Use entre 8 y 64 caracteres, con mayúscula, minúscula y número.");
            return "redirect:/restablecer-contrasena";
        }
        usuarioDAO.actualizarPassword((Long) session.getAttribute(USUARIO), passwordEncoder.encode(password));
        avisoEmailService.avisarCambioPassword((String) session.getAttribute(EMAIL));
        limpiar(session); redirectAttributes.addFlashAttribute("passwordActualizada", true);
        return "redirect:/login";
    }

    private boolean sesionValida(HttpSession session) {
        Object expira=session.getAttribute(EXPIRA);
        return session.getAttribute(USUARIO)!=null
                && expira instanceof Long && (Long)expira > Instant.now().toEpochMilli();
    }
    private void limpiar(HttpSession session) { session.removeAttribute(USUARIO); session.removeAttribute(EMAIL); session.removeAttribute(EXPIRA); }
}
