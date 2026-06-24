package pe.edu.utp.generador_horario.service.restricciones;

import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;

/**
 * Utilidad interna para comparar bloques horarios del motor de restricciones.
 */
final class HorarioSolapamientoUtil {

    private HorarioSolapamientoUtil() {
    }

    /**
     * Indica si dos asignaciones se cruzan en el tiempo.
     *
     * <p>Si una termina exactamente cuando la otra inicia, no hay cruce. Ese
     * caso queda disponible para reglas como traslado entre sedes.</p>
     */
    static boolean seSolapan(AsignacionHorarioCandidataDTO primera, AsignacionHorarioCandidataDTO segunda) {
        if (primera.getHoraInicio() == null
                || primera.getHoraFin() == null
                || segunda.getHoraInicio() == null
                || segunda.getHoraFin() == null) {
            return false;
        }

        return primera.getHoraInicio().isBefore(segunda.getHoraFin())
                && segunda.getHoraInicio().isBefore(primera.getHoraFin());
    }
}
