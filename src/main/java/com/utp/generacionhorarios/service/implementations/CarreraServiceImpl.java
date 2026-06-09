package com.utp.generacionhorarios.service.implementations;

import com.utp.generacionhorarios.entity.Carrera;
import com.utp.generacionhorarios.repository.CarreraRepository;
import com.utp.generacionhorarios.service.interfaces.CarreraService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraServiceImpl implements CarreraService {

    private final CarreraRepository carreraRepository;

    public CarreraServiceImpl(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Override
    public List<Carrera> listarCarreras() {
        return carreraRepository.findByEstadoTrue();
    }

    @Override
    public Carrera obtenerPorId(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada con ID: " + id));
    }

    @Override
    public Carrera guardarCarrera(Carrera carrera) {
        if (carrera.getEstado() == null) {
            carrera.setEstado(true);
        }

        if (carrera.getIdCarrera() == null) {
            if (carreraRepository.existsByCodigo(carrera.getCodigo())) {
                throw new IllegalArgumentException("Ya existe una carrera con ese código.");
            }

            if (carreraRepository.existsByNombre(carrera.getNombre())) {
                throw new IllegalArgumentException("Ya existe una carrera con ese nombre.");
            }
        } else {
            if (carreraRepository.existsByCodigoAndIdCarreraNot(carrera.getCodigo(), carrera.getIdCarrera())) {
                throw new IllegalArgumentException("Ya existe otra carrera con ese código.");
            }

            if (carreraRepository.existsByNombreAndIdCarreraNot(carrera.getNombre(), carrera.getIdCarrera())) {
                throw new IllegalArgumentException("Ya existe otra carrera con ese nombre.");
            }
        }

        return carreraRepository.save(carrera);
    }

    @Override
    public void desactivarCarrera(Long id) {
        Carrera carrera = obtenerPorId(id);
        carrera.setEstado(false);
        carreraRepository.save(carrera);
    }
}