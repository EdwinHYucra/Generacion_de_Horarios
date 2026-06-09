package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.repository.DocenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocenteService {

    private final DocenteRepository docenteRepository;

    public List<Docente> listarTodos() {
        return docenteRepository.findAll();
    }

    public Optional<Docente> buscarPorId(Long id) {
        return docenteRepository.findById(id);
    }

    @Transactional
    public Docente guardar(Docente docente) {
        docente.setEstado(true);
        return docenteRepository.save(docente);
    }

    @Transactional
    public Docente actualizar(Long id, Docente datos) {
        Docente existente = docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        existente.setNombres(datos.getNombres());
        existente.setApellidos(datos.getApellidos());
        existente.setDni(datos.getDni());
        existente.setCorreo(datos.getCorreo());
        existente.setCodigo(datos.getCodigo());
        existente.setEspecialidad(datos.getEspecialidad());
        existente.setEstado(datos.getEstado());
        return docenteRepository.save(existente);
    }

    @Transactional
    public void desactivar(Long id) {
        Docente d = docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado"));
        d.setEstado(false);
        docenteRepository.save(d);
    }
}