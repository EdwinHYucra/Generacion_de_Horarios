const textosEstado = {
    disponibilidad: ["Registrada", "Pendiente de registro"],
    cursos: ["Seleccionados", "Pendientes de selección"],
    horario: ["Asignado", "Pendiente de asignación"]
};

async function actualizarEstadoDashboard() {
    try {
        const response = await fetch("/docente/dashboard/estado", {cache: "no-store"});
        if (!response.ok) return;
        const estado = await response.json();
        Object.entries(estado).forEach(([clave, completo]) => {
            const item = document.querySelector(`[data-estado="${clave}"]`);
            if (!item) return;
            item.classList.toggle("is-complete", completo);
            item.classList.toggle("is-pending", !completo);
            item.querySelector(".docente-status-item__icon").textContent = completo ? "✓" : "…";
            item.querySelector("p").textContent = textosEstado[clave][completo ? 0 : 1];
        });
    } catch (error) {
        console.debug("No se pudo actualizar el estado del dashboard.", error);
    }
}

actualizarEstadoDashboard();
setInterval(actualizarEstadoDashboard, 5000);
