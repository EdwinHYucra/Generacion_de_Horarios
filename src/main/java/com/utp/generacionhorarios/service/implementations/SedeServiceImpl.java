package com.utp.generacionhorarios.service.implementations;

import com.utp.generacionhorarios.entity.Sede;
import com.utp.generacionhorarios.repository.SedeRepository;
import com.utp.generacionhorarios.service.interfaces.SedeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SedeServiceImpl implements SedeService {

    private final SedeRepository sedeRepository;

    public SedeServiceImpl(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }

    @Override
    public List<Sede> listarSedes() {
        return sedeRepository.findByEstadoTrue();
    }

    @Override
    public Sede obtenerPorId(Long id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sede no encontrada con ID: " + id));
    }

    @Override
    public Sede guardarSede(Sede sede) {
        if (sede.getEstado() == null) {
            sede.setEstado(true);
        }

        if (sede.getIdSede() == null) {
            if (sedeRepository.existsByCodigo(sede.getCodigo())) {
                throw new IllegalArgumentException("Ya existe una sede con ese código.");
            }

            if (sedeRepository.existsByNombre(sede.getNombre())) {
                throw new IllegalArgumentException("Ya existe una sede con ese nombre.");
            }
        } else {
            if (sedeRepository.existsByCodigoAndIdSedeNot(sede.getCodigo(), sede.getIdSede())) {
                throw new IllegalArgumentException("Ya existe otra sede con ese código.");
            }

            if (sedeRepository.existsByNombreAndIdSedeNot(sede.getNombre(), sede.getIdSede())) {
                throw new IllegalArgumentException("Ya existe otra sede con ese nombre.");
            }
        }

        return sedeRepository.save(sede);
    }

    @Override
    public void desactivarSede(Long id) {
        Sede sede = obtenerPorId(id);
        sede.setEstado(false);
        sedeRepository.save(sede);
    }
}