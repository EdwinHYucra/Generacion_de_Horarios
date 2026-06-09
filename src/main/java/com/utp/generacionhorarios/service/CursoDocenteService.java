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

    public CursoDocenteService(CursoRepository cursoRepository,
                                DocenteRepository docenteRepository,
                                DocenteCursoRepository docenteCursoRepository) {
        this.cursoRepository = cursoRepository;
        this.docenteRepository = docenteRepository;
        this.docenteCursoRepository = docenteCursoRepository;
    }

    public List<Curso> obtenerCursosCarrera() {
        return cursoRepository.findByTipoAndEstadoTrue("CARRERA");
    }

    public List<Curso> obtenerCursosGenerales() {
        return cursoRepository.findByTipoAndEstadoTrue("GENERAL");
    }

    public List<Long> obtenerCursosSeleccionadosPorDocente(Long docenteId) {
        return docenteCursoRepository.findByDocente_IdDocente(docenteId)
                .stream()
                .map(dc -> dc.getCurso().getIdCurso())
                .toList();
    }

    @Transactional
    public void guardarCursosSeleccionados(SeleccionCursosDTO dto) {
        if (dto.getDocenteId() == null) {
            throw new IllegalArgumentException("No se encontró el docente.");
        }
        if (dto.getCursosSeleccionados() == null || dto.getCursosSeleccionados().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un curso.");
        }

        Long docenteId = dto.getDocenteId();
        Docente docente = docenteRepository.findById(docenteId)
                .orElseThrow(() -> new IllegalArgumentException("El docente no existe."));

        // Eliminar asignaciones previas
        docenteCursoRepository.deleteByDocenteIdDocente(docenteId);

        // Crear nuevas asignaciones
        List<Curso> cursos = cursoRepository.findAllById(dto.getCursosSeleccionados());
        if (cursos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron cursos válidos.");
        }

        for (Curso curso : cursos) {
            DocenteCurso dc = new DocenteCurso();
            dc.setDocente(docente);
            dc.setCurso(curso);
            dc.setFechaCreacion(LocalDateTime.now());
            docenteCursoRepository.save(dc);
        }
    }
}
