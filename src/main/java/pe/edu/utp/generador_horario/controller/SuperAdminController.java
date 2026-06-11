package pe.edu.utp.generador_horario.controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.AdminRegistroDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.servicio.AdminServicio;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/superadmin")
public class SuperAdminController {

    private final AdminServicio adminServicio;
    private final UsuarioDAO usuarioDAO;

    public SuperAdminController(AdminServicio adminServicio, UsuarioDAO usuarioDAO) {
        this.adminServicio = adminServicio;
        this.usuarioDAO = usuarioDAO;
    }

    // ─── Dashboard ──────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Usuario> admins = adminServicio.listarAdmins();
        model.addAttribute("admins", admins);
        model.addAttribute("totalAdmins", admins.size());
        return "superadmin/dashboard";
    }

    // ─── Listar admins ──────────────────────────────────────
    @GetMapping("/admins")
    public String listarAdmins(Model model) {
        model.addAttribute("admins", adminServicio.listarAdmins());
        return "superadmin/listar_admins";
    }

    // ─── Formulario registro ────────────────────────────────
    @GetMapping("/admins/nuevo")
    public String formularioRegistro(Model model) {
        model.addAttribute("adminDTO", new AdminRegistroDTO());
        return "superadmin/registrar_admin";
    }

    // ─── Guardar nuevo admin ────────────────────────────────
    @PostMapping("/admins/guardar")
    public String guardarAdmin(
            @Valid @ModelAttribute("adminDTO") AdminRegistroDTO dto,
            BindingResult result,
            Authentication auth,
            RedirectAttributes redirectAttrs,
            Model model) {

        if (result.hasErrors()) {
            return "superadmin/registrar_admin";
        }

        try {
            Optional<Usuario> superAdmin = usuarioDAO.buscarPorEmail(auth.getName());
            Long superAdminId = superAdmin.get().getId();
            adminServicio.registrarAdmin(dto, superAdminId);
            redirectAttrs.addFlashAttribute("exito", "Administrador registrado correctamente");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "superadmin/registrar_admin";
        }

        return "redirect:/superadmin/admins";
    }

    // ─── Formulario edición ─────────────────────────────────
    @GetMapping("/admins/editar/{id}")
    public String formularioEdicion(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = adminServicio.buscarPorId(id);
        if (usuario.isEmpty()) {
            return "redirect:/superadmin/admins";
        }
        model.addAttribute("usuario", usuario.get());
        return "superadmin/editar_admin";
    }

    // ─── Actualizar admin ───────────────────────────────────
    @PostMapping("/admins/actualizar")
    public String actualizarAdmin(@ModelAttribute Usuario usuario,
                                   RedirectAttributes redirectAttrs) {
        adminServicio.actualizarAdmin(usuario);
        redirectAttrs.addFlashAttribute("exito", "Administrador actualizado correctamente");
        return "redirect:/superadmin/admins";
    }

    // ─── Desactivar admin ───────────────────────────────────
    @GetMapping("/admins/desactivar/{id}")
    public String desactivarAdmin(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        adminServicio.desactivarAdmin(id);
        redirectAttrs.addFlashAttribute("exito", "Administrador desactivado");
        return "redirect:/superadmin/admins";
    }

    // ─── Activar admin ──────────────────────────────────────
    @GetMapping("/admins/activar/{id}")
    public String activarAdmin(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        adminServicio.activarAdmin(id);
        redirectAttrs.addFlashAttribute("exito", "Administrador activado");
        return "redirect:/superadmin/admins";
    }
}