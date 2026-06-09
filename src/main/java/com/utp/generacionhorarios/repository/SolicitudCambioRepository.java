package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.entity.SolicitudCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface SolicitudCambioRepository extends JpaRepository<SolicitudCambio, Long> {

    @Query("SELECT s FROM SolicitudCambio s WHERE s.docente.idDocente = :docenteId")
    List<SolicitudCambio> findByDocenteIdDocente(@Param("docenteId") Long docenteId);

    List<SolicitudCambio> findByEstado(SolicitudCambio.EstadoCambio estado);
}