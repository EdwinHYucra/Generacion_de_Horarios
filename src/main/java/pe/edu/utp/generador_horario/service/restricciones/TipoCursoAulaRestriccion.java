package pe.edu.utp.generador_horario.service.restricciones;

import org.springframework.stereotype.Component;
import pe.edu.utp.generador_horario.dto.AsignacionHorarioCandidataDTO;
import pe.edu.utp.generador_horario.dto.ResultadoRestriccionDTO;

import java.util.List;
import java.util.Locale;

/**
 * Valida que el aula candidata sea compatible con el tipo de curso.
 *
 * <p>Por ahora la regla dura es: un curso de laboratorio debe programarse en
 * un aula/laboratorio. Los cursos teoricos y practicos pueden usar aula normal
 * o laboratorio; la preferencia fina se resolvera luego en el algoritmo.</p>
 */
@Component
public class TipoCursoAulaRestriccion implements RestriccionHorario {

    private static final String CODIGO = "TIPO_CURSO_AULA";
    private static final String CURSO_LABORATORIO = "LABORATORIO";
    private static final String AULA_LABORATORIO = "LABORATORIO";

    @Override
    public ResultadoRestriccionDTO validar(
            AsignacionHorarioCandidataDTO candidata,
            List<AsignacionHorarioCandidataDTO> asignacionesActuales) {

        if (candidata == null || candidata.getTipoCurso() == null || candidata.getTipoAula() == null) {
            return ResultadoRestriccionDTO.valido();
        }

        String tipoCurso = normalizar(candidata.getTipoCurso());
        String tipoAula = normalizar(candidata.getTipoAula());

        if (CURSO_LABORATORIO.equals(tipoCurso) && !AULA_LABORATORIO.equals(tipoAula)) {
            return ResultadoRestriccionDTO.invalido(
                    CODIGO,
                    "El curso de laboratorio requiere un aula de tipo laboratorio.");
        }

        return ResultadoRestriccionDTO.valido();
    }

    private String normalizar(String valor) {
        return valor.trim().toUpperCase(Locale.ROOT);
    }
}
