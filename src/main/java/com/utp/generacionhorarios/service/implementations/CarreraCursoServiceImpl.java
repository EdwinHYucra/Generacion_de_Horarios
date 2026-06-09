package com.utp.generacionhorarios.service.implementations;

import com.utp.generacionhorarios.entity.CarreraCurso;
import com.utp.generacionhorarios.repository.CarreraCursoRepository;
import com.utp.generacionhorarios.service.interfaces.CarreraCursoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarreraCursoServiceImpl implements CarreraCursoService {

    private final CarreraCursoRepository carreraCursoRepository;

    public CarreraCursoServiceImpl(CarreraCursoRepository carreraCursoRepository) {
        this.carreraCursoRepository = carreraCursoRepository;
    }

    @Override
    public List<CarreraCurso> listarAsignaciones() {
        return carreraCursoRepository.findByEstadoTrue();
    }

    @Override
    public CarreraCurso obtenerPorId(Long id) {
        return carreraCursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con ID: " + id));
    }

    @Override
    public CarreraCurso guardarAsignacion(CarreraCurso carreraCurso) {
        if (carreraCurso.getEstado() == null) {
            carreraCurso.setEstado(true);
        }

        if (carreraCurso.getCarrera() == null || carreraCurso.getCarrera().getIdCarrera() == null) {
            throw new IllegalArgumentException("Debe seleccionar una carrera.");
        }

        if (carreraCurso.getCurso() == null || carreraCurso.getCurso().getIdCurso() == null) {
            throw new IllegalArgumentException("Debe seleccionar un curso.");
        }

        if (carreraCurso.getCiclo() == null || carreraCurso.getCiclo() < 1 || carreraCurso.getCiclo() > 10) {
            throw new IllegalArgumentException("El ciclo debe estar entre 1 y 10.");
        }

        Long idCarrera = carreraCurso.getCarrera().getIdCarrera();
        Long idCurso = carreraCurso.getCurso().getIdCurso();

        if (carreraCurso.getIdCarreraCurso() == null) {
            if (carreraCursoRepository.existsByCarrera_IdCarreraAndCurso_IdCurso(idCarrera, idCurso)) {
                throw new IllegalArgumentException("Este curso ya está asignado a esa carrera.");
            }
        } else {
            if (carreraCursoRepository.existsByCarrera_IdCarreraAndCurso_IdCursoAndIdCarreraCursoNot(
                    idCarrera,
                    idCurso,
                    carreraCurso.getIdCarreraCurso()
            )) {
                throw new IllegalArgumentException("Este curso ya está asignado a esa carrera.");
            }
        }

        return carreraCursoRepository.save(carreraCurso);
    }

    @Override
    public void desactivarAsignacion(Long id) {
        CarreraCurso carreraCurso = obtenerPorId(id);
        carreraCurso.setEstado(false);
        carreraCursoRepository.save(carreraCurso);
    }
}