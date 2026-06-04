package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.model.Docente;
import com.utp.generacionhorarios.model.Usuario;
import com.utp.generacionhorarios.repository.DocenteRepository;
import com.utp.generacionhorarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocenteService {

    private final DocenteRepository docenteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Docente> listarTodos() {
        return docenteRepository.findAll();
    }

    public Optional<Docente> buscarPorId(Integer id) {
        return docenteRepository.findById(id);
    }

    @Transactional
    public Docente guardar(Docente docente, String username, String password) {
        Usuario usuario = Usuario.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .rol(Usuario.Rol.DOCENTE)
                .activo(true)
                .build();
        usuarioRepository.save(usuario);
        docente.setUsuario(usuario);
        docente.setEstado(true);
        return docenteRepository.save(docente);
    }

    @Transactional
    public Docente actualizar(Integer id, Docente datos) {
        Docente existente = docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        existente.setNombres(datos.getNombres());
        existente.setApellidos(datos.getApellidos());
        existente.setDni(datos.getDni());
        existente.setEmail(datos.getEmail());
        existente.setEstado(datos.getEstado());
        return docenteRepository.save(existente);
    }

    @Transactional
    public void desactivar(Integer id) {
        Docente d = docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        d.setEstado(false);
        docenteRepository.save(d);
    }
}