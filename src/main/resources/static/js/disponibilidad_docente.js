const dias = [
    "LUNES",
    "MARTES",
    "MIERCOLES",
    "JUEVES",
    "VIERNES",
    "SABADO",
    "DOMINGO"
];

const grid = document.getElementById("scheduleGrid");

let bloquesSeleccionados = [];

let arrastrando = false;
let modoSeleccion = true;

document.addEventListener("mouseup", () => {
    arrastrando = false;
});

function generarHorario() {
    let hora = 7;
    let minuto = 0;

    while (hora < 22 || (hora === 22 && minuto <= 30)) {
        const horaTexto =
            `${hora.toString().padStart(2, '0')}:${minuto.toString().padStart(2, '0')}`;

        const timeCell = document.createElement("div");
        timeCell.className = "cell time-cell";
        timeCell.textContent = horaTexto;
        grid.appendChild(timeCell);

        dias.forEach(dia => {
            const slot = document.createElement("div");
            slot.className = "cell slot";
            slot.dataset.dia = dia;
            slot.dataset.hora = horaTexto;

            slot.addEventListener("mousedown", (e) => {
    e.preventDefault();
    if (window.seleccionBloqueada) return;

    arrastrando = true;
    modoSeleccion = !slot.classList.contains("selected");

    slot.classList.toggle("selected", modoSeleccion);
    actualizarContador();
});

slot.addEventListener("mouseenter", () => {
    if (arrastrando && !window.seleccionBloqueada) {
        slot.classList.toggle("selected", modoSeleccion);
        actualizarContador();
    }
});

            grid.appendChild(slot);
        });

        minuto += 15;

        if (minuto === 60) {
            minuto = 0;
            hora++;
        }
    }
}

function actualizarContador() {
    bloquesSeleccionados = document.querySelectorAll(".slot.selected");

    document.getElementById("contadorBloques").textContent = bloquesSeleccionados.length;
    document.getElementById("contadorHoras").textContent = (bloquesSeleccionados.length * 0.25).toFixed(1);
    actualizarEstadoTurnos();
}

// Mantiene cada botón marcado solo cuando todo su turno está seleccionado.
function actualizarEstadoTurnos() {
    document.querySelectorAll(".turno-btn").forEach(boton => {
        const slotsTurno = [...document.querySelectorAll(".slot")].filter(slot =>
            slot.dataset.hora >= boton.dataset.inicio && slot.dataset.hora < boton.dataset.fin
        );
        const turnoCompleto = slotsTurno.length > 0
            && slotsTurno.every(slot => slot.classList.contains("selected"));

        boton.classList.toggle("active", turnoCompleto);
        boton.setAttribute("aria-pressed", String(turnoCompleto));
    });
}

function sumar15Minutos(hora) {
    const [h, m] = hora.split(":").map(Number);

    const fecha = new Date();
    fecha.setHours(h);
    fecha.setMinutes(m + 15);

    return fecha.getHours().toString().padStart(2, "0") + ":" +
        fecha.getMinutes().toString().padStart(2, "0");
}

document.getElementById("btnLimpiar").addEventListener("click", () => {
    if (window.seleccionBloqueada) return;
    document.querySelectorAll(".slot.selected")
        .forEach(c => c.classList.remove("selected"));

    actualizarContador();
});

document.getElementById("btnConfirmar").addEventListener("click", async () => {
    if (window.seleccionBloqueada) return;
    const botonConfirmar = document.getElementById("btnConfirmar");
    const bloques = [];

    document.querySelectorAll(".slot.selected").forEach(slot => {
        bloques.push({
            diaSemana: slot.dataset.dia,
            horaInicio: slot.dataset.hora,
            horaFin: sumar15Minutos(slot.dataset.hora)
        });
    });

    try {
        botonConfirmar.disabled = true;
        botonConfirmar.textContent = "Guardando...";
        const response = await fetch("/docente/disponibilidad/guardar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                bloques: bloques
            })
        });

        if (response.ok) {
            mostrarNotificacionDocente(
                "Tu disponibilidad fue aceptada y guardada correctamente.",
                "success",
                "Disponibilidad confirmada"
            );
            setTimeout(() => window.location.assign("/docente/cursos"), 1400);
            return;
        } else {
            mostrarNotificacionDocente(await response.text() || "Error al guardar.", "error");
        }
    } catch (error) {
        console.error(error);
        mostrarNotificacionDocente("No fue posible conectar con el servidor.", "error", "Error de conexión");
    } finally {
        botonConfirmar.disabled = false;
        botonConfirmar.textContent = "Confirmar";
    }
});

async function cargarDisponibilidadGuardada() {
    try {
        const response = await fetch("/docente/disponibilidad/listar");
        const bloques = await response.json();

        bloques.forEach(bloque => {
            document.querySelectorAll(`.slot[data-dia="${bloque.diaSemana}"]`)
                .forEach(slot => {
                    if (slot.dataset.hora >= bloque.horaInicio && slot.dataset.hora < bloque.horaFin) {
                        slot.classList.add("selected");
                    }
                });
        });

        actualizarContador();
    } catch (error) {
        console.error(error);
    }
}

// Primer clic selecciona el turno completo; el siguiente lo deselecciona.
function alternarTurno(inicio, fin) {
    const slotsTurno = [...document.querySelectorAll(".slot")].filter(slot =>
        slot.dataset.hora >= inicio && slot.dataset.hora < fin
    );
    const turnoCompleto = slotsTurno.every(slot => slot.classList.contains("selected"));

    slotsTurno.forEach(slot => slot.classList.toggle("selected", !turnoCompleto));

    actualizarContador();
}

document.querySelectorAll(".turno-btn").forEach(boton => {
    boton.addEventListener("click", () => {
        alternarTurno(
            boton.dataset.inicio,
            boton.dataset.fin
        );
    });
});

if (window.seleccionBloqueada) {
    document.querySelectorAll(".turno-btn, #btnLimpiar, #btnConfirmar").forEach(boton => boton.disabled = true);
    grid.classList.add("is-readonly");
}

generarHorario();
cargarDisponibilidadGuardada();
