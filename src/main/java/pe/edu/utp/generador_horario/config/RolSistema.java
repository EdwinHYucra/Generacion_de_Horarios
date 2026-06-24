package pe.edu.utp.generador_horario.config;

/**
 * Roles reconocidos por Spring Security y por la tabla {@code usuario}.
 */
public final class RolSistema {

    public static final String SUPERADMIN = "SUPERADMIN";
    public static final String ADMIN = "ADMIN";
    public static final String DOCENTE = "DOCENTE";

    public static final String AUTHORITY_SUPERADMIN = "ROLE_" + SUPERADMIN;
    public static final String AUTHORITY_ADMIN = "ROLE_" + ADMIN;
    public static final String AUTHORITY_DOCENTE = "ROLE_" + DOCENTE;

    private RolSistema() {
    }
}
