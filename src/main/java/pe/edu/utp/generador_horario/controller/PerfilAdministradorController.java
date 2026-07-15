package pe.edu.utp.generador_horario.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.generador_horario.dao.UsuarioDAO;
import pe.edu.utp.generador_horario.entidad.Usuario;
import pe.edu.utp.generador_horario.service.FotoPerfilService;

import java.util.concurrent.TimeUnit;

@Controller
public class PerfilAdministradorController {
    private final UsuarioDAO usuarioDAO;
    private final FotoPerfilService fotoPerfilService;

    public PerfilAdministradorController(UsuarioDAO usuarioDAO, FotoPerfilService fotoPerfilService) {
        this.usuarioDAO = usuarioDAO;
        this.fotoPerfilService = fotoPerfilService;
    }

    @PostMapping("/administrador/perfil/foto")
    public String actualizarFoto(@RequestParam("foto") MultipartFile foto, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            fotoPerfilService.guardar(usuario(authentication).getId(), foto);
            redirectAttributes.addFlashAttribute("mensajeExito", "Foto de perfil actualizada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("mensajeError", e.getMessage());
        }
        return "redirect:/administrador/dashboard";
    }

    @GetMapping("/administrador/perfil/foto")
    public ResponseEntity<Resource> foto(Authentication authentication) {
        return fotoPerfilService.cargar(usuario(authentication).getId())
                .map(resource -> ResponseEntity.ok().cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                        .contentType(MediaType.IMAGE_JPEG).body(resource))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Usuario usuario(Authentication authentication) {
        return usuarioDAO.buscarPorEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }
}
