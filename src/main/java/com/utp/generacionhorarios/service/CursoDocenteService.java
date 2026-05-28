package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.dto.SeleccionCursosDTO;
import com.utp.generacionhorarios.entity.Curso;
import com.utp.generacionhorarios.entity.Docente;
import com.utp.generacionhorarios.entity.DocenteCurso;
import com.utp.generacionhorarios.repository.CursoRepository;
import com.utp.generacionhorarios.repository.DocenteCursoRepository;
import com.utp.generacionhorarios.repository.DocenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CursoDocenteService {

    private final CursoRepository cursoRepository;
    private final DocenteRepository docenteRepository;
    private final DocenteCursoRepository docenteCursoRepository;

    public CursoDocenteService(
            CursoRepository cursoRepository,
            DocenteRepository docenteRepository,
            DocenteCursoRepository docenteCursoRepository) {
        this.cursoRepository = cursoRepository;
        this.docenteRepository = docenteRepository;
        this.docenteCursoRepository = docenteCursoRepository;
    }

    public List<Curso> obtenerCursosCarrera() {
        return cursoRepository.findByTipoAndEstado("CARRERA", 1);
    }

    public List<Curso> obtenerCursosGenerales() {
        return cursoRepository.findByTipoAndEstado("GENERAL", 1);
    }

    public List<Long> obtenerCursosSeleccionadosPorDocente(Long docenteId) {
        return docenteCursoRepository.findByDocenteId(docenteId)
                .stream()
                .map(docenteCurso -> docenteCurso.getCurso().getIdCurso())
                .toList();
    }

    @Transactional
    public void guardarCursosSeleccionados(SeleccionCursosDTO seleccionCursosDTO) {

        if (seleccionCursosDTO.getDocenteId() == null) {
            throw new IllegalArgumentException("No se encontró el docente.");
        }

        if (seleccionCursosDTO.getCursosSeleccionados() == null ||
                seleccionCursosDTO.getCursosSeleccionados().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un curso.");
        }

        Docente docente = docenteRepository.findById(seleccionCursosDTO.getDocenteId())
                .orElseThrow(() -> new IllegalArgumentException("El docente no existe."));

        docenteCursoRepository.deleteByDocenteId(docente.getId());

        List<Curso> cursos = cursoRepository.findAllById(seleccionCursosDTO.getCursosSeleccionados());

        if (cursos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron cursos válidos.");
        }

        List<DocenteCurso> docenteCursos = cursos.stream()
                .map(curso -> {
                    DocenteCurso docenteCurso = new DocenteCurso();
                    docenteCurso.setDocente(docente);
                    docenteCurso.setCurso(curso);
                    docenteCurso.setFechaCreacion(LocalDateTime.now());
                    return docenteCurso;
                })
                .toList();

        docenteCursoRepository.saveAll(docenteCursos);
    }
}