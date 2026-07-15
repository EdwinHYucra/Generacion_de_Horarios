package pe.edu.utp.generador_horario.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Service
public class FotoPerfilService {
    private final Path directorio = Path.of("uploads", "perfiles").toAbsolutePath().normalize();

    public boolean existe(Long usuarioId) {
        return Files.isRegularFile(ruta(usuarioId));
    }

    public void guardar(Long usuarioId, MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) throw new IllegalArgumentException("Seleccione una imagen.");
        if (archivo.getSize() > 3L * 1024 * 1024) throw new IllegalArgumentException("La imagen no debe superar los 3 MB.");
        try {
            if (ImageIO.read(archivo.getInputStream()) == null) throw new IllegalArgumentException("El archivo seleccionado no es una imagen válida.");
            Files.createDirectories(directorio);
            Files.copy(archivo.getInputStream(), ruta(usuarioId), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalArgumentException("No fue posible guardar la imagen.");
        }
    }

    public Optional<Resource> cargar(Long usuarioId) {
        try {
            Path archivo = ruta(usuarioId);
            return Files.isRegularFile(archivo) ? Optional.of(new UrlResource(archivo.toUri())) : Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private Path ruta(Long usuarioId) { return directorio.resolve("docente-" + usuarioId + ".img"); }
}
