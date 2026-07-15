const docenteNotificacion = document.getElementById("docenteNotificacion");
const docenteNotificacionTitulo = document.getElementById("docenteNotificacionTitulo");
const docenteNotificacionMensaje = document.getElementById("docenteNotificacionMensaje");
const docenteNotificacionCerrar = document.getElementById("docenteNotificacionCerrar");
let docenteNotificacionTemporizador;

/** Muestra mensajes del portal sin utilizar alertas nativas del navegador. */
function mostrarNotificacionDocente(mensaje, tipo = "success", titulo) {
    if (!docenteNotificacion) {
        return;
    }

    clearTimeout(docenteNotificacionTemporizador);
    docenteNotificacion.classList.remove("is-success", "is-error", "is-visible");
    docenteNotificacion.classList.add(tipo === "error" ? "is-error" : "is-success");
    docenteNotificacionTitulo.textContent = titulo
        || (tipo === "error" ? "No se pudo completar" : "Proceso completado");
    docenteNotificacionMensaje.textContent = mensaje;
    docenteNotificacion.setAttribute("aria-hidden", "false");

    requestAnimationFrame(() => docenteNotificacion.classList.add("is-visible"));
    docenteNotificacionTemporizador = setTimeout(cerrarNotificacionDocente, 6000);
}

function cerrarNotificacionDocente() {
    if (!docenteNotificacion) {
        return;
    }
    docenteNotificacion.classList.remove("is-visible");
    docenteNotificacion.setAttribute("aria-hidden", "true");
}

if (docenteNotificacionCerrar) {
    docenteNotificacionCerrar.addEventListener("click", cerrarNotificacionDocente);
}
