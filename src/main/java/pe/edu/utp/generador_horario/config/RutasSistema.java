package pe.edu.utp.generador_horario.config;

/**
 * Rutas base y destinos principales usados por controladores y seguridad.
 */
public final class RutasSistema {

    public static final String LOGIN = "/login";
    public static final String LOGIN_ERROR = "/login?error";
    public static final String LOGOUT = "/logout";

    public static final String SUPERADMIN = "/superadmin";
    public static final String SUPERADMIN_DASHBOARD = SUPERADMIN + "/dashboard";
    public static final String SUPERADMIN_ADMINS = SUPERADMIN + "/admins";

    public static final String ADMINISTRADOR = "/administrador";
    public static final String ADMINISTRADOR_DASHBOARD = ADMINISTRADOR + "/dashboard";
    public static final String ADMINISTRADOR_DOCENTES = ADMINISTRADOR + "/docentes";

    public static final String DOCENTE = "/docente";
    public static final String DOCENTE_DASHBOARD = DOCENTE + "/dashboard";

    private RutasSistema() {
    }
}
