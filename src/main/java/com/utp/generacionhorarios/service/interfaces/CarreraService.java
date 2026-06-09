package com.utp.generacionhorarios.service.interfaces;

import com.utp.generacionhorarios.entity.Carrera;

import java.util.List;

public interface CarreraService {

    List<Carrera> listarCarreras();

    Carrera obtenerPorId(Long id);

    Carrera guardarCarrera(Carrera carrera);

    void desactivarCarrera(Long id);
}