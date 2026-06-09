package com.utp.generacionhorarios.service.implementations;

import com.utp.generacionhorarios.entity.Aula;
import com.utp.generacionhorarios.repository.AulaRepository;
import com.utp.generacionhorarios.service.interfaces.AulaService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AulaServiceImpl implements AulaService {

    private final AulaRepository aulaRepository;

    public AulaServiceImpl(AulaRepository aulaRepository) {
        this.aulaRepository = aulaRepository;
    }

    @Override
    public List<Aula> listarAulas() {
        return aulaRepository.findByEstadoTrue();
    }

    @Override
    public Aula obtenerPorId(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aula no encontrada con ID: " + id));
    }

    @Override
    public Aula guardarAula(Aula aula) {
        if (aula.getEstado() == null) {
            aula.setEstado(true);
        }

        if (aula.getCapacidad() == null || aula.getCapacidad() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        }

        if (aula.getSede() == null || aula.getSede().getIdSede() == null) {
            throw new IllegalArgumentException("Debe seleccionar una sede.");
        }

        if (aula.getIdAula() == null) {
            if (aulaRepository.existsByCodigo(aula.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un aula con ese código.");
            }
        } else {
            if (aulaRepository.existsByCodigoAndIdAulaNot(aula.getCodigo(), aula.getIdAula())) {
                throw new IllegalArgumentException("Ya existe otra aula con ese código.");
            }
        }

        return aulaRepository.save(aula);
    }

    @Override
    public void desactivarAula(Long id) {
        Aula aula = obtenerPorId(id);
        aula.setEstado(false);
        aulaRepository.save(aula);
    }
}