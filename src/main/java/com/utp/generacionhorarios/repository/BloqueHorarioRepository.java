package com.utp.generacionhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.generacionhorarios.entity.BloqueHorario;

public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Integer> {
}
