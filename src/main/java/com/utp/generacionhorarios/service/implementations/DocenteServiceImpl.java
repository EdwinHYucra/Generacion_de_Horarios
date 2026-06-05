package com.utp.generacionhorarios.service.implementations;

import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.repository.DocenteRepository;
import com.utp.generacionhorarios.service.interfaces.DocenteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocenteServiceImpl implements DocenteService {

    private final DocenteRepository docenteRepository;

    public DocenteServiceImpl(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    @Override
    public List<Docente> listarDocentes() {
        return docenteRepository.findAll();
    }

    @Override
    public Docente obtenerPorId(Long id) {
        return docenteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Docente no encontrado con ID: " + id));
    }

    @Override
    public Docente guardarDocente(Docente docente) {
        if (docente.getEstado() == null) {
            docente.setEstado(false);
        }

        return docenteRepository.save(docente);
    }

    @Override
    public void desactivarDocente(Long id) {
        Docente docente = obtenerPorId(id);
        docente.setEstado(false);
        docenteRepository.save(docente);
    }
}