package pe.edu.utp.generador_horario.entidad;

public class SuperAdmin {

    private Long id;
    private Long usuarioId;
    private Usuario usuario;

    public SuperAdmin() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}