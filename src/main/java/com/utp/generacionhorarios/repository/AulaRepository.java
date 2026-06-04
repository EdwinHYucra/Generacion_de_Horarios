package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findByEstadoTrue();
    List<Aula> findByTipoAndEstadoTrue(Aula.TipoAula tipo);
}
