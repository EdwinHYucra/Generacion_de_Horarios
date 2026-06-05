package com.utp.generacionhorarios.service.interfaces;

import com.utp.generacionhorarios.entity.Aula;

import java.util.List;

public interface AulaService {

    List<Aula> listarAulas();

    Aula obtenerPorId(Long id);

    Aula guardarAula(Aula aula);

    void desactivarAula(Long id);
}