package com.utp.generacionhorarios.repository;

import com.utp.generacionhorarios.model.SolicitudCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudCambioRepository extends JpaRepository<SolicitudCambio, Integer> {
    List<SolicitudCambio> findByDocenteId(Integer docenteId);
    List<SolicitudCambio> findByEstado(SolicitudCambio.EstadoCambio estado);
}
