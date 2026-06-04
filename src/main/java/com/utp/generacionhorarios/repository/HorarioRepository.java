package com.utp.generacionhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.generacionhorarios.entity.Horario;

import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {
    List<Horario> findBySemestreId(Integer semestreId);
}
