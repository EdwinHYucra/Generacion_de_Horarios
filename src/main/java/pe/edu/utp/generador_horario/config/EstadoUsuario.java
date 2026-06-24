package pe.edu.utp.generador_horario.config;

/**
 * Centraliza los estados persistidos para usuarios del sistema.
 *
 * <p>Evita strings magicos dispersos y mantiene consistente el valor usado en
 * servicios, seguridad y consultas SQL.</p>
 */
public final class EstadoUsuario {

    public static final String ACTIVO = "ACTIVO";
    public static final String INACTIVO = "INACTIVO";

    private EstadoUsuario() {
    }
}
