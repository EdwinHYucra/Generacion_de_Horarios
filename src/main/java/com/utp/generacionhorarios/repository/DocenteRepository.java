package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    Optional<Docente> findByCodigoUsuario(String codigoUsuario);

    Optional<Docente> findByCodigoDocente(String codigoDocente);
}