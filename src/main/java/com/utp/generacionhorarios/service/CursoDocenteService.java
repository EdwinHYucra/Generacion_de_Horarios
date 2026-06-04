package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.dto.SeleccionCursosDTO;
import com.utp.generacionhorarios.model.Curso;
import com.utp.generacionhorarios.model.Docente;
import com.utp.generacionhorarios.repository.CursoRepository;
import com.utp.generacionhorarios.repository.DocenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CursoDocenteService {

    private final CursoRepository cursoRepository;
    private final DocenteRepository docenteRepository;

    public CursoDocenteService(
            CursoRepository cursoRepository,
            DocenteRepository docenteRepository) {
        this.cursoRepository = cursoRepository;
        this.docenteRepository = docenteRepository;
    }

    public List<Curso> obtenerCursosCarrera() {
        return cursoRepository.findByEstadoTrue();
    }

    public List<Curso> obtenerCursosGenerales() {
        return new ArrayList<>();
    }

    public List<Integer> obtenerCursosSeleccionadosPorDocente(Integer docenteId) {
        Docente docente = docenteRepository.findById(docenteId)
                .orElse(null);

        if (docente == null || docente.getCursos() == null) {
            return new ArrayList<>();
        }

        return docente.getCursos()
                .stream()
                .map(Curso::getId)
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

        Integer docenteId = seleccionCursosDTO.getDocenteId().intValue();

        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new IllegalArgumentException("El docente no existe."));

        List<Integer> cursosIds = seleccionCursosDTO.getCursosSeleccionados()
                .stream()
                .map(Long::intValue)
                .toList();

        List<Curso> cursos = cursoRepository.findAllById(cursosIds);

        if (cursos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron cursos válidos.");
        }

        docente.setCursos(new java.util.HashSet<>(cursos));
        docenteRepository.save(docente);
    }
}