package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SemestreRepository extends JpaRepository<Semestre, Integer> {
    Optional<Semestre> findByEstado(Semestre.EstadoSemestre estado);
}
