package pe.edu.utp.generador_horario.service.implementations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneracionAsyncService;
import pe.edu.utp.generador_horario.service.interfaces.HorarioGeneradoService;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HorarioGeneracionAsyncServiceImpl implements HorarioGeneracionAsyncService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HorarioGeneracionAsyncServiceImpl.class);

    private final TaskExecutor taskExecutor;
    private final HorarioGeneradoService horarioGeneradoService;
    private final Set<Long> docentesEnProceso = ConcurrentHashMap.newKeySet();
    private final Set<Long> docentesConReintento = ConcurrentHashMap.newKeySet();

    public HorarioGeneracionAsyncServiceImpl(
            @Qualifier("horarioTaskExecutor") TaskExecutor taskExecutor,
            HorarioGeneradoService horarioGeneradoService) {
        this.taskExecutor = taskExecutor;
        this.horarioGeneradoService = horarioGeneradoService;
    }

    @Override
    public boolean programarGeneracion(Long idDocente) {
        if (idDocente == null) {
            return false;
        }

        if (!docentesEnProceso.add(idDocente)) {
            docentesConReintento.add(idDocente);
            return false;
        }

        taskExecutor.execute(() -> generar(idDocente));
        return true;
    }

    @Override
    public boolean estaEnProceso(Long idDocente) {
        return idDocente != null && docentesEnProceso.contains(idDocente);
    }

    private void generar(Long idDocente) {
        long inicio = System.currentTimeMillis();
        try {
            LOGGER.info("Generacion automatica iniciada. docenteId={}", idDocente);
            int opciones = horarioGeneradoService.generarSiTieneInsumos(idDocente);
            LOGGER.info("Generacion automatica finalizada. docenteId={}, opciones={}, duracionMs={}",
                    idDocente, opciones, System.currentTimeMillis() - inicio);
        } catch (RuntimeException ex) {
            LOGGER.error("Fallo la generacion automatica. docenteId={}, duracionMs={}",
                    idDocente, System.currentTimeMillis() - inicio, ex);
        } finally {
            docentesEnProceso.remove(idDocente);
            if (docentesConReintento.remove(idDocente)) {
                programarGeneracion(idDocente);
            }
        }
    }
}
