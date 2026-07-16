const checksCursos = document.querySelectorAll(".curso-check");
const resumenLista = document.getElementById("resumenLista");
const totalCursos = document.getElementById("totalCursos");
const totalHoras = document.getElementById("totalHoras");
const btnGuardarCursos = document.getElementById("btnGuardarCursos");
const limiteHorasMensaje = document.getElementById("limiteHorasMensaje");
const maxHorasSemanales = Number(window.maxHorasSemanales || 40);
const buscarCursos = document.getElementById("buscarCursos");

if (window.seleccionBloqueada) {
    checksCursos.forEach(check => check.disabled = true);
    btnGuardarCursos.disabled = true;
    btnGuardarCursos.textContent = "Selección confirmada";
}

// Abre o cierra cada módulo sin modificar las selecciones realizadas.
document.querySelectorAll(".curso-toggle").forEach(boton => {
    boton.addEventListener("click", () => {
        const lista = document.getElementById(boton.getAttribute("aria-controls"));
        const estaAbierto = boton.getAttribute("aria-expanded") === "true";

        boton.setAttribute("aria-expanded", String(!estaAbierto));
        boton.textContent = estaAbierto ? "Ver cursos" : "Ocultar cursos";
        lista.hidden = estaAbierto;
    });
});

// Filtra datos reales de ambos módulos y los expande cuando hay una búsqueda.
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

// Actualiza el resumen con la selección actual del docente.
function actualizarResumen() {
    const seleccionados = document.querySelectorAll(".curso-check:checked");

    resumenLista.innerHTML = "";

    let horas = 0;

    if (seleccionados.length === 0) {
        resumenLista.innerHTML = '<p class="resumen-vacio">Aún no seleccionaste cursos.</p>';
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

    if (horas > maxHorasSemanales) {
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
    if (window.seleccionBloqueada) return;
    const cursosSeleccionados = [];
    const horasSeleccionadas = Number(totalHoras.textContent || 0);

    if (horasSeleccionadas > maxHorasSemanales) {
        mostrarNotificacionDocente(
            `La carga supera ${maxHorasSemanales} horas semanales.`,
            "error",
            "Selección no válida"
        );
        return;
    }

    document.querySelectorAll(".curso-check:checked").forEach(check => {
        cursosSeleccionados.push(Number(check.value));
    });

    try {
        btnGuardarCursos.disabled = true;
        btnGuardarCursos.textContent = "Guardando...";
        const response = await fetch("/docente/cursos/guardar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                cursosSeleccionados: cursosSeleccionados
            })
        });

        if (response.ok) {
            mostrarNotificacionDocente(
                "Tus cursos fueron confirmados. Estamos generando opciones con tus cursos y disponibilidad.",
                "success",
                "Cursos confirmados"
            );
            setTimeout(() => window.location.assign("/docente/opciones_horario"), 1600);
            return;
        } else {
            mostrarNotificacionDocente(await response.text(), "error");
        }
    } catch (error) {
        console.error(error);
        mostrarNotificacionDocente("No fue posible conectar con el servidor.", "error", "Error de conexión");
    } finally {
        btnGuardarCursos.disabled = false;
        btnGuardarCursos.textContent = "Confirmar selección";
    }
});

actualizarResumen();
