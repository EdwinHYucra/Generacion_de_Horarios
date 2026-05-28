// ── SemestreRepository.java ──────────────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.Semestre;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SemestreRepository extends JpaRepository<Semestre, Integer> {
    Optional<Semestre> findByEstado(Semestre.EstadoSemestre estado);
}


// ── UsuarioRepository.java ───────────────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
}


// ── DocenteRepository.java ───────────────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DocenteRepository extends JpaRepository<Docente, Integer> {
    List<Docente> findByEstadoTrue();
}


// ── CursoRepository.java ─────────────────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Integer> {
    List<Curso> findByEstadoTrue();
}


// ── AulaRepository.java ──────────────────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findByEstadoTrue();
    List<Aula> findByTipoAndEstadoTrue(Aula.TipoAula tipo);
}


// ── BloqueHorarioRepository.java ─────────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.BloqueHorario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Integer> {
}


// ── DisponibilidadDocenteRepository.java ─────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.DisponibilidadDocente;
import com.utp.generadorhorarios.model.DisponibilidadDocente.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibilidadDocenteRepository extends JpaRepository<DisponibilidadDocente, Integer> {
    List<DisponibilidadDocente> findByDocenteIdAndSemestreId(Integer docenteId, Integer semestreId);
    List<DisponibilidadDocente> findByDocenteIdAndSemestreIdAndDiaSemana(
        Integer docenteId, Integer semestreId, DiaSemana diaSemana);
}


// ── HorarioRepository.java ───────────────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {
    List<Horario> findBySemestreId(Integer semestreId);
}


// ── DetalleHorarioRepository.java ────────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.DetalleHorario;
import com.utp.generadorhorarios.model.DisponibilidadDocente.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleHorarioRepository extends JpaRepository<DetalleHorario, Integer> {
    List<DetalleHorario> findByHorarioId(Integer horarioId);
    List<DetalleHorario> findByDocenteId(Integer docenteId);

    // Verifica conflicto de docente (CU-06)
    boolean existsByHorarioIdAndDocenteIdAndDiaSemanaAndBloqueId(
        Integer horarioId, Integer docenteId, DiaSemana diaSemana, Integer bloqueId);

    // Verifica conflicto de aula (CU-06)
    boolean existsByHorarioIdAndAulaIdAndDiaSemanaAndBloqueId(
        Integer horarioId, Integer aulaId, DiaSemana diaSemana, Integer bloqueId);
}


// ── SolicitudCambioRepository.java ───────────────────────────
package com.utp.generadorhorarios.repository;

import com.utp.generadorhorarios.model.SolicitudCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudCambioRepository extends JpaRepository<SolicitudCambio, Integer> {
    List<SolicitudCambio> findByDocenteId(Integer docenteId);
    List<SolicitudCambio> findByEstado(SolicitudCambio.EstadoCambio estado);
}
