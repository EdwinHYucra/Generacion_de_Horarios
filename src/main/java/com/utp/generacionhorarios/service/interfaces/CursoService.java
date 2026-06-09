package com.utp.generacionhorarios.service.interfaces;

import com.utp.generacionhorarios.entity.Curso;

import java.util.List;

public interface CursoService {

    List<Curso> listarCursos();

    Curso obtenerPorId(Long id);

    Curso guardarCurso(Curso curso);

    void desactivarCurso(Long id);
}