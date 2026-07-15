document.addEventListener("DOMContentLoaded", () => {
    const modales = [...document.querySelectorAll(".login-modal")];
    const cerrar = () => {
        modales.forEach(modal => { modal.hidden = true; modal.setAttribute("aria-hidden", "true"); });
        document.body.classList.remove("login-modal-open");
    };
    const abrir = id => {
        cerrar(); const modal = document.getElementById(id); if (!modal) return;
        modal.hidden = false; modal.setAttribute("aria-hidden", "false"); document.body.classList.add("login-modal-open");
    };
    document.getElementById("abrirAyudaLogin")?.addEventListener("click", () => abrir("modalAyudaLogin"));
    document.querySelectorAll("[data-cerrar-login-modal]").forEach(elemento => elemento.addEventListener("click", cerrar));
    document.addEventListener("keydown", evento => { if (evento.key === "Escape") cerrar(); });
});
