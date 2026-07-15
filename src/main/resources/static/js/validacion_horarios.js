document.addEventListener("DOMContentLoaded", () => {
    const filas = Array.from(document.querySelectorAll(".validacion-row"));
    const buscador = document.getElementById("buscarValidacion");
    const botones = Array.from(document.querySelectorAll("#filtrosValidacion button"));
    const contador = document.getElementById("cantidadValidaciones");
    let filtro = "todos";
    const normalizar = valor => String(valor || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

    // Combina búsqueda por docente/carrera y estado seleccionado.
    const aplicarFiltros = () => {
        const texto = normalizar(buscador?.value);
        let visibles = 0;
        filas.forEach(fila => {
            const estado = normalizar(fila.dataset.estado);
            const coincideTexto = normalizar(`${fila.dataset.docente} ${fila.dataset.carrera}`).includes(texto);
            const coincideEstado = filtro === "todos"
                || (filtro === "aprobado" && estado === "aprobado")
                || (filtro === "pendiente" && estado !== "aprobado" && estado !== "descartado");
            const visible = coincideTexto && coincideEstado;
            fila.style.display = visible ? "" : "none";
            if (visible) visibles += 1;
        });
        if (contador) contador.textContent = `${visibles} propuestas`;
    };

    buscador?.addEventListener("input", aplicarFiltros);
    botones.forEach(boton => boton.addEventListener("click", () => {
        filtro = boton.dataset.estado;
        botones.forEach(item => item.classList.toggle("is-active", item === boton));
        aplicarFiltros();
    }));
    aplicarFiltros();

    // Acciones: rechazo mediante modal propio, sin alertas nativas del navegador.
    const modalRechazo = document.getElementById("modalRechazoValidacion");
    const formRechazo = document.getElementById("formRechazoValidacion");
    const textoRechazo = document.getElementById("textoRechazoValidacion");
    document.querySelectorAll(".validacion-rechazar").forEach(boton => boton.addEventListener("click", () => {
        formRechazo.action = `/administrador/horarios/rechazar/${boton.dataset.horarioId}`;
        textoRechazo.textContent = `Se rechazará el horario elegido por ${boton.dataset.docente}.`;
        modalRechazo.classList.remove("hidden");
        modalRechazo.setAttribute("aria-hidden", "false");
    }));
    modalRechazo?.querySelectorAll("[data-cerrar-rechazo]").forEach(elemento => elemento.addEventListener("click", () => {
        modalRechazo.classList.add("hidden");
        modalRechazo.setAttribute("aria-hidden", "true");
    }));
});
