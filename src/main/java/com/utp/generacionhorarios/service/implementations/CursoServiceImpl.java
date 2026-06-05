package com.utp.generacionhorarios.service.implementations;

import com.utp.generacionhorarios.entity.Curso;
import com.utp.generacionhorarios.repository.CursoRepository;
import com.utp.generacionhorarios.service.interfaces.CursoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;

    public CursoServiceImpl(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Override
    public List<Curso> listarCursos() {
        return cursoRepository.findByEstadoTrue();
    }

    @Override
    public Curso obtenerPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado con ID: " + id));
    }

    @Override
    public Curso guardarCurso(Curso curso) {
        if (curso.getEstado() == null) {
            curso.setEstado(true);
        }

        if (curso.getHorasSemanales() == null || curso.getHorasSemanales() <= 0) {
            throw new IllegalArgumentException("Las horas semanales deben ser mayores a 0.");
        }

        if (curso.getIdCurso() == null) {
            if (cursoRepository.existsByCodigo(curso.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un curso con ese código.");
            }

            if (cursoRepository.existsByNombre(curso.getNombre())) {
                throw new IllegalArgumentException("Ya existe un curso con ese nombre.");
            }
        } else {
            if (cursoRepository.existsByCodigoAndIdCursoNot(curso.getCodigo(), curso.getIdCurso())) {
                throw new IllegalArgumentException("Ya existe otro curso con ese código.");
            }

            if (cursoRepository.existsByNombreAndIdCursoNot(curso.getNombre(), curso.getIdCurso())) {
                throw new IllegalArgumentException("Ya existe otro curso con ese nombre.");
            }
        }

        return cursoRepository.save(curso);
    }

    @Override
    public void desactivarCurso(Long id) {
        Curso curso = obtenerPorId(id);
        curso.setEstado(false);
        cursoRepository.save(curso);
    }
}