package pe.edu.utp.generador_horario.dto;

/**
 * Resultado uniforme de una validacion de restriccion del generador.
 *
 * <p>Permite que cada regla explique por que una asignacion fue rechazada sin
 * obligar al generador a conocer detalles de esa regla.</p>
 */
public class ResultadoRestriccionDTO {

    private final boolean valido;
    private final String codigo;
    private final String mensaje;

    private ResultadoRestriccionDTO(boolean valido, String codigo, String mensaje) {
        this.valido = valido;
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    /**
     * Crea una respuesta positiva para continuar evaluando restricciones.
     */
    public static ResultadoRestriccionDTO valido() {
        return new ResultadoRestriccionDTO(true, "OK", "Asignacion valida.");
    }

    /**
     * Crea una respuesta negativa con codigo y mensaje de negocio.
     */
    public static ResultadoRestriccionDTO invalido(String codigo, String mensaje) {
        return new ResultadoRestriccionDTO(false, codigo, mensaje);
    }

    public boolean isValido() {
        return valido;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }
}
