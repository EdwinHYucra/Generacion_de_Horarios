package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocenteRepository extends JpaRepository<Docente, Integer> {
    List<Docente> findByEstadoTrue();
}
