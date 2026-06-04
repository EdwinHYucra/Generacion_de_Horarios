package com.utp.generacionhorarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.generacionhorarios.entity.Semestre;

import java.util.Optional;

public interface SemestreRepository extends JpaRepository<Semestre, Integer> {
    Optional<Semestre> findByEstado(Semestre.EstadoSemestre estado);
}
