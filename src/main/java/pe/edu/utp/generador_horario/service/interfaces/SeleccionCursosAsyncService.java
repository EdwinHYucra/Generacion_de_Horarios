package pe.edu.utp.generador_horario.service.interfaces;

import java.util.List;

public interface SeleccionCursosAsyncService {

    boolean programarConfirmacion(Long idDocente, List<Long> cursosSeleccionados);

    boolean estaEnProceso(Long idDocente);
}
