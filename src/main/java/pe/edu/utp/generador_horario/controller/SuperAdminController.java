package pe.edu.utp.generador_horario.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.config.RutasSistema;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.dto.AdminRegistroDTO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.servicio.AdminServicio;

import java.util.List;
import java.util.Optional;

/**
 * Controlador MVC del modulo SuperAdmin.
 *
 * <p>Permite listar, registrar, actualizar y cambiar el estado de
 * administradores del sistema.</p>
 */
@Controller
@RequestMapping("/superadmin")
public class SuperAdminController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SuperAdminController.class);

    private final AdminServicio adminServicio;
    private final UsuarioDAO usuarioDAO;

    public SuperAdminController(AdminServicio adminServicio, UsuarioDAO usuarioDAO) {
        this.adminServicio = adminServicio;
        this.usuarioDAO = usuarioDAO;
    }

    /**
     * Muestra el resumen del modulo SuperAdmin.
     *
     * @param model modelo de Thymeleaf
     * @return vista de dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Usuario> admins = adminServicio.listarAdmins();
        model.addAttribute("admins", admins);
        model.addAttribute("totalAdmins", admins.size());
        model.addAttribute("moduloActivo", "dashboard");
        return "superadmin/dashboard";
    }

    /**
     * Lista los administradores registrados.
     *
     * @param model modelo de Thymeleaf
     * @return vista de listado de administradores
     */
    @GetMapping("/admins")
    public String listarAdmins(Model model) {
        model.addAttribute("admins", adminServicio.listarAdmins());
        model.addAttribute("moduloActivo", "admins");
        return "superadmin/listar_admins";
    }

    /**
     * Presenta el formulario para registrar un administrador.
     *
     * @param model modelo de Thymeleaf
     * @return vista de registro
     */
    @GetMapping("/admins/nuevo")
    public String formularioRegistro(Model model) {
        model.addAttribute("adminDTO", new AdminRegistroDTO());
        model.addAttribute("moduloActivo", "admins");
        return "superadmin/registrar_admin";
    }

    /**
     * Registra un administrador creado por el SuperAdmin autenticado.
     *
     * @return redireccion al listado de administradores
     */
    @PostMapping("/admins/guardar")
    public String guardarAdmin(
            @Valid @ModelAttribute("adminDTO") AdminRegistroDTO dto,
            BindingResult result,
            Authentication auth,
            RedirectAttributes redirectAttrs,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("moduloActivo", "admins");
            return "superadmin/registrar_admin";
        }

        try {
            Usuario superAdmin = usuarioDAO.buscarPorEmail(auth.getName())
                    .orElseThrow(() -> new IllegalStateException("SuperAdmin autenticado no encontrado"));
            adminServicio.registrarAdmin(dto, superAdmin.getId());
            redirectAttrs.addFlashAttribute("exito", "Administrador registrado correctamente");
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("moduloActivo", "admins");
            return "superadmin/registrar_admin";
        } catch (IllegalStateException e) {
            LOGGER.error("No se pudo registrar administrador: {}", e.getMessage());
            redirectAttrs.addFlashAttribute("error", "No se encontro el SuperAdmin autenticado");
            return "redirect:" + RutasSistema.SUPERADMIN_ADMINS;
        }

        return "redirect:" + RutasSistema.SUPERADMIN_ADMINS;
    }

    /**
     * Presenta el formulario de edicion de administrador.
     *
     * @param id identificador del usuario administrador
     * @param model modelo de Thymeleaf
     * @return vista de edicion o redireccion al listado
     */
    @GetMapping("/admins/editar/{id}")
    public String formularioEdicion(@PathVariable Long id, Model model) {
        Optional<Usuario> usuario = adminServicio.buscarPorId(id);
        if (usuario.isEmpty()) {
            return "redirect:" + RutasSistema.SUPERADMIN_ADMINS;
        }
        model.addAttribute("usuario", usuario.get());
        model.addAttribute("moduloActivo", "admins");
        return "superadmin/editar_admin";
    }

    /**
     * Actualiza los datos basicos del administrador.
     *
     * @return redireccion al listado de administradores
     */
    @PostMapping("/admins/actualizar")
    public String actualizarAdmin(
            @ModelAttribute Usuario usuario,
            RedirectAttributes redirectAttrs) {
        adminServicio.actualizarAdmin(usuario);
        redirectAttrs.addFlashAttribute("exito", "Administrador actualizado correctamente");
        return "redirect:" + RutasSistema.SUPERADMIN_ADMINS;
    }

    /**
     * Desactiva logicamente un administrador.
     *
     * @param id identificador del usuario administrador
     * @return redireccion al listado de administradores
     */
    @GetMapping("/admins/desactivar/{id}")
    public String desactivarAdmin(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        adminServicio.desactivarAdmin(id);
        redirectAttrs.addFlashAttribute("exito", "Administrador desactivado");
        return "redirect:" + RutasSistema.SUPERADMIN_ADMINS;
    }

    /**
     * Reactiva un administrador previamente desactivado.
     *
     * @param id identificador del usuario administrador
     * @return redireccion al listado de administradores
     */
    @GetMapping("/admins/activar/{id}")
    public String activarAdmin(@PathVariable Long id, RedirectAttributes redirectAttrs) {
        adminServicio.activarAdmin(id);
        redirectAttrs.addFlashAttribute("exito", "Administrador activado");
        return "redirect:" + RutasSistema.SUPERADMIN_ADMINS;
    }
}
