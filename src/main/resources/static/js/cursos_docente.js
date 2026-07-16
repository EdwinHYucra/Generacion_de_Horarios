const checksCursos = document.querySelectorAll(".curso-check");
const resumenLista = document.getElementById("resumenLista");
const totalCursos = document.getElementById("totalCursos");
const totalHoras = document.getElementById("totalHoras");
const btnGuardarCursos = document.getElementById("btnGuardarCursos");
const limiteHorasMensaje = document.getElementById("limiteHorasMensaje");
const maxHorasSemanales = Number(window.maxHorasSemanales || 40);
const cursosBloqueados = window.cursosBloqueados === true || window.cursosBloqueados === "true";
const buscarCursos = document.getElementById("buscarCursos");
const GUARDAR_CURSOS_TIMEOUT_MS = 20000;

document.querySelectorAll(".curso-toggle").forEach(boton => {
    boton.addEventListener("click", () => {
        const lista = document.getElementById(boton.getAttribute("aria-controls"));
        const estaAbierto = boton.getAttribute("aria-expanded") === "true";

        boton.setAttribute("aria-expanded", String(!estaAbierto));
        boton.textContent = estaAbierto ? "Ver cursos" : "Ocultar cursos";
        lista.hidden = estaAbierto;
    });
});

buscarCursos.addEventListener("input", () => {
    const termino = buscarCursos.value.trim().toLocaleLowerCase("es");

    document.querySelectorAll(".curso-item").forEach(item => {
        const coincide = item.textContent.toLocaleLowerCase("es").includes(termino);
        item.hidden = termino.length > 0 && !coincide;
    });

    if (termino.length > 0) {
        document.querySelectorAll(".curso-toggle").forEach(boton => {
            const lista = document.getElementById(boton.getAttribute("aria-controls"));
            boton.setAttribute("aria-expanded", "true");
            boton.textContent = "Ocultar cursos";
            lista.hidden = false;
        });
    }
});

function actualizarResumen() {
    const seleccionados = document.querySelectorAll(".curso-check:checked");

    resumenLista.innerHTML = "";

    let horas = 0;

    if (seleccionados.length === 0) {
        resumenLista.innerHTML = '<p class="resumen-vacio">Aun no seleccionaste cursos.</p>';
    }

    seleccionados.forEach(check => {
        const nombre = check.dataset.nombre;
        const horasCurso = Number(check.dataset.horas || 0);

        horas += horasCurso;

        const item = document.createElement("div");
        item.className = "resumen-item";
        item.innerHTML = `
            <strong>${nombre}</strong>
            <small>${horasCurso} horas semanales</small>
        `;

        resumenLista.appendChild(item);
    });

    totalCursos.textContent = seleccionados.length;
    totalHoras.textContent = horas;

    if (cursosBloqueados) {
        limiteHorasMensaje.textContent = "Seleccion confirmada. No se puede modificar.";
        btnGuardarCursos.disabled = true;
    } else if (horas > maxHorasSemanales) {
        limiteHorasMensaje.textContent = `La carga supera el maximo permitido de ${maxHorasSemanales} horas semanales.`;
        btnGuardarCursos.disabled = true;
    } else {
        limiteHorasMensaje.textContent = "";
        btnGuardarCursos.disabled = false;
    }
}

checksCursos.forEach(check => {
    check.addEventListener("change", actualizarResumen);
});

btnGuardarCursos.addEventListener("click", async () => {
    if (cursosBloqueados) return;

    const cursosSeleccionados = [];
    const horasSeleccionadas = Number(totalHoras.textContent || 0);

    if (horasSeleccionadas > maxHorasSemanales) {
        mostrarNotificacionDocente(
            `La carga supera ${maxHorasSemanales} horas semanales.`,
            "error",
            "Seleccion no valida"
        );
        return;
    }

    document.querySelectorAll(".curso-check:checked").forEach(check => {
        cursosSeleccionados.push(Number(check.value));
    });

    let timeoutId = null;
    let redirigiendo = false;

    try {
        btnGuardarCursos.disabled = true;
        btnGuardarCursos.textContent = "Guardando...";

        const controller = new AbortController();
        timeoutId = window.setTimeout(() => controller.abort(), GUARDAR_CURSOS_TIMEOUT_MS);

        const response = await fetch("/docente/cursos/guardar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            credentials: "same-origin",
            signal: controller.signal,
            body: JSON.stringify({
                cursosSeleccionados: cursosSeleccionados
            })
        });

        window.clearTimeout(timeoutId);
        timeoutId = null;

        if (response.ok) {
            redirigiendo = true;
            window.location.assign("/docente/opciones_horario");
            return;
        }

        mostrarNotificacionDocente(await response.text(), "error");
    } catch (error) {
        console.error(error);

        if (error.name === "AbortError") {
            mostrarNotificacionDocente(
                "La solicitud esta tardando demasiado. Intenta nuevamente o revisa Opciones de horario en unos segundos.",
                "error",
                "Tiempo de espera agotado"
            );
        } else {
            mostrarNotificacionDocente("No fue posible conectar con el servidor.", "error", "Error de conexion");
        }
    } finally {
        if (timeoutId !== null) {
            window.clearTimeout(timeoutId);
        }

        if (!redirigiendo) {
            btnGuardarCursos.disabled = false;
            btnGuardarCursos.textContent = "Confirmar seleccion";
        }
    }
});

actualizarResumen();
