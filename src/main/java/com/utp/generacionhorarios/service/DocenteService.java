package com.utp.generacionhorarios.service;

import com.utp.generacionhorarios.entity.Docente;

import java.util.List;

public interface DocenteService {

    List<Docente> listarDocentes();

    Docente obtenerPorId(Long id);

    Docente guardarDocente(Docente docente);

    void desactivarDocente(Long id);
}