package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.entity.EvaluacionDocente;
import com.utp.generacionhorarios.repository.EvaluacionDocenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluacionDocenteService {

    private final EvaluacionDocenteRepository evaluacionRepository;

    public boolean puedeAsignarse(Long docenteId, Long cursoId) {
        return !evaluacionRepository.existeEvaluacionBaja(
                docenteId, cursoId, EvaluacionDocente.NOTA_MINIMA);
    }

    public List<EvaluacionDocente> obtenerEvaluacionesDocente(Long docenteId) {
        return evaluacionRepository.findByDocente(docenteId);
    }

    public EvaluacionDocente guardar(EvaluacionDocente evaluacion) {
        return evaluacionRepository.save(evaluacion);
    }
}