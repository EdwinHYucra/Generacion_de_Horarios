package com.utp.generacionhorarios.service.interfaces;

import com.utp.generacionhorarios.entity.CarreraCurso;

import java.util.List;

public interface CarreraCursoService {

    List<CarreraCurso> listarAsignaciones();

    CarreraCurso obtenerPorId(Long id);

    CarreraCurso guardarAsignacion(CarreraCurso carreraCurso);

    void desactivarAsignacion(Long id);
}