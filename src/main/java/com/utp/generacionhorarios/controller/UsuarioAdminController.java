package com.utp.generacionhorarios.controller;

import com.utp.generacionhorarios.entity.Usuario;
import com.utp.generacionhorarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listar(Model model) {
        List<Usuario> usuarios = usuarioRepository.findAll();
        model.addAttribute("usuarios", usuarios);
        return "gestion_usuarios";
    }

    @GetMapping("/nuevo")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "form_usuario";
    }

    @PostMapping("/nuevo")
    public String guardar(@RequestParam String username,
                          @RequestParam String password,
                          RedirectAttributes ra) {
        try {
            if (usuarioRepository.findByUsername(username).isPresent()) {
                ra.addFlashAttribute("mensaje", "El usuario '" + username + "' ya existe.");
                ra.addFlashAttribute("tipo", "danger");
                return "redirect:/admin/usuarios/nuevo";
            }
            Usuario nuevo = Usuario.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .rol(Usuario.Rol.ADMIN)
                    .activo(true)
                    .build();
            usuarioRepository.save(nuevo);
            ra.addFlashAttribute("mensaje", "Administrador '" + username + "' creado correctamente.");
            ra.addFlashAttribute("tipo", "success");
        } catch (Exception e) {
            ra.addFlashAttribute("mensaje", "Error: " + e.getMessage());
            ra.addFlashAttribute("tipo", "danger");
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Integer id, RedirectAttributes ra) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setActivo(false);
            usuarioRepository.save(u);
        });
        ra.addFlashAttribute("mensaje", "Usuario desactivado.");
        ra.addFlashAttribute("tipo", "warning");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/activar/{id}")
    public String activar(@PathVariable Integer id, RedirectAttributes ra) {
        usuarioRepository.findById(id).ifPresent(u -> {
            u.setActivo(true);
            usuarioRepository.save(u);
        });
        ra.addFlashAttribute("mensaje", "Usuario activado.");
        ra.addFlashAttribute("tipo", "success");
        return "redirect:/admin/usuarios";
    }
}