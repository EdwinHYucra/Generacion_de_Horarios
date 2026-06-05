package com.utp.generacionhorarios.service.interfaces;

import com.utp.generacionhorarios.entity.Sede;

import java.util.List;

public interface SedeService {

    List<Sede> listarSedes();

    Sede obtenerPorId(Long id);

    Sede guardarSede(Sede sede);

    void desactivarSede(Long id);
}