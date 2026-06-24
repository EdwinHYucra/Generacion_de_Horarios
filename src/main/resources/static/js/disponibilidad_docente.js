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

    arrastrando = true;
    modoSeleccion = !slot.classList.contains("selected");

    slot.classList.toggle("selected", modoSeleccion);
    actualizarContador();
});

slot.addEventListener("mouseenter", () => {
    if (arrastrando) {
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
    document.querySelectorAll(".slot.selected")
        .forEach(c => c.classList.remove("selected"));

    actualizarContador();
});

document.getElementById("btnConfirmar").addEventListener("click", async () => {
    const bloques = [];

    document.querySelectorAll(".slot.selected").forEach(slot => {
        bloques.push({
            diaSemana: slot.dataset.dia,
            horaInicio: slot.dataset.hora,
            horaFin: sumar15Minutos(slot.dataset.hora)
        });
    });

    try {
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
            alert(await response.text());
        } else {
            alert("Error al guardar.");
        }
    } catch (error) {
        console.error(error);
        alert("Error de conexión.");
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

function seleccionarTurno(inicio, fin) {
    document.querySelectorAll(".slot").forEach(slot => {
        const hora = slot.dataset.hora;

        if (hora >= inicio && hora < fin) {
            slot.classList.add("selected");
        }
    });

    actualizarContador();
}

document.querySelectorAll(".turno-btn").forEach(boton => {
    boton.addEventListener("click", () => {
        seleccionarTurno(
            boton.dataset.inicio,
            boton.dataset.fin
        );
    });
});

generarHorario();
cargarDisponibilidadGuardada();
