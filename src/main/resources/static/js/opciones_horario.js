const coloresCursos = [
    "#e00000",
    "#f7c948",
    "#2fd67a",
    "#3da5f4",
    "#c96a21",
    "#8b5cf6",
    "#14b8a6"
];

const estadoOpciones = {
    opciones: Array.isArray(opcionesHorarioData) ? opcionesHorarioData : [],
    activa: null
};

let temporizadorEstado = null;

document.addEventListener("DOMContentLoaded", () => {
    if (window.lucide) {
        window.lucide.createIcons();
    }

    const sinOpciones = document.getElementById("sinOpciones");
    const panelHorario = document.getElementById("panelHorario");
    const accionesSolicitud = document.getElementById("accionesSolicitud");
    const btnVerDetalle = document.getElementById("btnVerDetalle");
    const btnCerrarDetalle = document.getElementById("cerrarDetalle");
    const modalDetalle = document.getElementById("modalDetalle");
    const modalAviso = document.getElementById("modalAvisoSolicitud");

    if (estadoOpciones.opciones.length === 0) {
        sinOpciones.classList.remove("hidden");
        if (generacionEnProcesoData) {
            iniciarMonitoreoGeneracion();
        }
        return;
    }

    panelHorario.classList.remove("hidden");
    accionesSolicitud.classList.remove("hidden");
    construirSelectorOpciones();
    seleccionarOpcion(estadoOpciones.opciones[0].idHorario);

    btnVerDetalle.addEventListener("click", abrirDetalleActivo);
    btnCerrarDetalle.addEventListener("click", cerrarDetalle);
    modalDetalle.querySelector(".modal-backdrop").addEventListener("click", cerrarDetalle);
    document.getElementById("cerrarAvisoSolicitud").addEventListener("click", cerrarAvisoSolicitud);
    modalAviso.querySelector("[data-cerrar-aviso]").addEventListener("click", cerrarAvisoSolicitud);

    // Permite cerrar los módulos con Escape sin utilizar ventanas del navegador.
    document.addEventListener("keydown", evento => {
        if (evento.key === "Escape" && !modalAviso.classList.contains("hidden")) {
            cerrarAvisoSolicitud();
        }
    });
});

function iniciarMonitoreoGeneracion() {
    if (temporizadorEstado !== null) {
        return;
    }

    temporizadorEstado = window.setInterval(async () => {
        try {
            const response = await fetch("/docente/opciones_horario/estado", {
                headers: {
                    "Accept": "application/json"
                },
                credentials: "same-origin"
            });

            if (!response.ok) {
                return;
            }

            const data = await response.json();
            const opciones = Array.isArray(data.opcionesHorario) ? data.opcionesHorario : [];

            if (opciones.length > 0) {
                window.clearInterval(temporizadorEstado);
                temporizadorEstado = null;
                window.location.reload();
            } else if (!data.generacionEnProceso) {
                window.clearInterval(temporizadorEstado);
                temporizadorEstado = null;
            }
        } catch (error) {
            console.error(error);
        }
    }, 2000);
}

function construirSelectorOpciones() {
    const selector = document.getElementById("opcionesSelector");
    selector.innerHTML = "";

    estadoOpciones.opciones.forEach(opcion => {
        const boton = document.createElement("button");
        boton.type = "button";
        boton.className = "opcion-tab";
        boton.dataset.id = opcion.idHorario;
        boton.textContent = `Opcion ${opcion.opcion}`;
        boton.addEventListener("click", () => seleccionarOpcion(opcion.idHorario));
        selector.appendChild(boton);
    });
}

function seleccionarOpcion(idHorario) {
    const opcion = estadoOpciones.opciones.find(item => String(item.idHorario) === String(idHorario));
    if (!opcion) {
        return;
    }

    estadoOpciones.activa = opcion;
    document.getElementById("idOpcionSeleccionada").value = opcion.idHorario;
    document.getElementById("btnConfirmar").disabled = false;
    document.getElementById("tituloOpcion").textContent = `Opcion ${opcion.opcion}`;
    document.getElementById("comentarioSolicitud").value = "";

    document.querySelectorAll(".opcion-tab").forEach(tab => {
        tab.classList.toggle("active", String(tab.dataset.id) === String(opcion.idHorario));
    });

    renderizarHorario(opcion, document.getElementById("horarioActivoBody"));
    renderizarResumen(opcion);
}

function renderizarHorario(opcion, tbody) {
    const horas = obtenerHoras(opcion);
    tbody.innerHTML = "";

    horas.forEach(hora => {
        const fila = document.createElement("tr");
        fila.appendChild(crearCeldaHora(hora));

        diasSemanaData.forEach(dia => {
            const celda = document.createElement("td");
            obtenerBloquesPorInicio(opcion, dia, hora).forEach(bloque => {
                celda.appendChild(crearBloqueHorario(bloque, obtenerColorCurso(bloque.curso, opcion)));
            });
            fila.appendChild(celda);
        });

        tbody.appendChild(fila);
    });
}

function crearCeldaHora(hora) {
    const celda = document.createElement("td");
    celda.className = "hora";
    celda.textContent = hora;
    return celda;
}

function crearBloqueHorario(bloque, color) {
    const div = document.createElement("div");
    div.className = "bloque-horario";
    div.style.background = mezclarColor(color, 0.18);
    div.style.borderColor = mezclarColor(color, 0.45);
    div.style.color = color;

    const curso = document.createElement("strong");
    curso.textContent = bloque.curso;

    const horario = document.createElement("span");
    horario.textContent = `${bloque.horaInicio} - ${bloque.horaFin}`;

    const aula = document.createElement("span");
    aula.textContent = `${bloque.aula} | ${bloque.sede}`;

    div.appendChild(curso);
    div.appendChild(horario);
    div.appendChild(aula);
    return div;
}

function renderizarResumen(opcion) {
    const cursos = obtenerCursosUnicos(opcion);
    const resumenCursos = document.getElementById("resumenCursos");
    resumenCursos.innerHTML = "";

    cursos.forEach((curso, index) => {
        const item = document.createElement("div");
        item.className = "curso-chip";

        const color = document.createElement("span");
        color.className = "curso-color";
        color.style.background = coloresCursos[index % coloresCursos.length];

        const texto = document.createElement("span");
        texto.textContent = curso;

        item.appendChild(color);
        item.appendChild(texto);
        resumenCursos.appendChild(item);
    });

    const metricas = calcularMetricas(opcion);
    document.getElementById("metricaBloques").textContent = metricas.bloques;
    document.getElementById("metricaCarga").textContent = `${metricas.horasAcademicas} h acad.`;
    document.getElementById("metricaDias").textContent = metricas.dias;
    document.getElementById("metricaAulas").textContent = metricas.aulas;
}

function abrirDetalleActivo() {
    const opcion = estadoOpciones.activa;
    if (!opcion) {
        return;
    }

    document.getElementById("tituloDetalle").textContent = `Opcion ${opcion.opcion}`;
    renderizarHorario(opcion, document.getElementById("detalleHorarioBody"));
    renderizarDetalleCursos(opcion);

    const metricas = calcularMetricas(opcion);
    document.getElementById("detalleCarga").textContent = `${metricas.horasAcademicas} h acad.`;
    document.getElementById("detalleSedes").textContent = metricas.sedes;
    document.getElementById("detalleAulas").textContent = metricas.aulas;
    document.getElementById("modalDetalle").classList.remove("hidden");
    const cuerpoModal = document.querySelector("#modalDetalle .modal-body");
    const tablaDetalle = document.querySelector("#modalDetalle .detalle-horario");
    if (cuerpoModal) cuerpoModal.scrollTop = 0;
    if (tablaDetalle) tablaDetalle.scrollLeft = 0;
}

function cerrarDetalle() {
    document.getElementById("modalDetalle").classList.add("hidden");
}

function renderizarDetalleCursos(opcion) {
    const contenedor = document.getElementById("detalleCursos");
    contenedor.innerHTML = "";

    opcion.bloques.forEach(bloque => {
        const item = document.createElement("div");
        item.className = "curso-detalle";

        const titulo = document.createElement("strong");
        titulo.textContent = bloque.curso;

        const meta = document.createElement("small");
        meta.textContent = `${bloque.dia} ${bloque.horaInicio} - ${bloque.horaFin} | ${bloque.aula} | ${bloque.sede}`;

        item.appendChild(titulo);
        item.appendChild(meta);
        contenedor.appendChild(item);
    });
}

function prepararSolicitud(form) {
    const opcion = estadoOpciones.activa;
    const comentario = document.getElementById("comentarioSolicitud").value.trim();

    if (!opcion) {
        mostrarAvisoSolicitud(
            "Seleccione una propuesta",
            "Elija una opción de horario antes de enviar una observación o rechazo."
        );
        return false;
    }

    if (!comentario) {
        mostrarAvisoSolicitud(
            "Falta una justificación",
            "Escriba el motivo de la observación o rechazo antes de continuar."
        );
        return false;
    }

    form.querySelector("input[name='idHorario']").value = opcion.idHorario;
    form.querySelector("input[name='comentario']").value = comentario;
    return true;
}

// Módulo centrado para validaciones de observar y rechazar.
function mostrarAvisoSolicitud(titulo, mensaje) {
    document.getElementById("tituloAvisoSolicitud").textContent = titulo;
    document.getElementById("mensajeAvisoSolicitud").textContent = mensaje;
    document.getElementById("modalAvisoSolicitud").classList.remove("hidden");
    document.body.classList.add("modal-abierto");
    setTimeout(() => document.getElementById("cerrarAvisoSolicitud").focus(), 0);
}

function cerrarAvisoSolicitud() {
    document.getElementById("modalAvisoSolicitud").classList.add("hidden");
    document.body.classList.remove("modal-abierto");
    document.getElementById("comentarioSolicitud")?.focus();
}

function obtenerHoras(opcion) {
    const horas = new Set(bloquesHoraData || []);
    (opcion.bloques || []).forEach(bloque => horas.add(bloque.horaInicio));
    return Array.from(horas).sort();
}

function obtenerBloquesPorInicio(opcion, dia, hora) {
    return (opcion.bloques || []).filter(bloque =>
        normalizarDia(bloque.dia) === normalizarDia(dia) && bloque.horaInicio === hora
    );
}

function obtenerCursosUnicos(opcion) {
    return Array.from(new Set((opcion.bloques || []).map(bloque => bloque.curso)));
}

function obtenerColorCurso(curso, opcion) {
    const indice = obtenerCursosUnicos(opcion).indexOf(curso);
    return coloresCursos[Math.max(indice, 0) % coloresCursos.length];
}

function calcularMetricas(opcion) {
    const bloques = opcion.bloques || [];
    const minutos = bloques.reduce((total, bloque) => total + minutosEntre(bloque.horaInicio, bloque.horaFin), 0);

    return {
        bloques: bloques.length,
        horasAcademicas: formatearHorasAcademicas(minutos),
        dias: new Set(bloques.map(bloque => normalizarDia(bloque.dia))).size,
        sedes: new Set(bloques.map(bloque => bloque.sede)).size,
        aulas: new Set(bloques.map(bloque => bloque.aula)).size
    };
}

function formatearHorasAcademicas(minutos) {
    const horas = minutos / 45;
    return Number.isInteger(horas) ? String(horas) : horas.toFixed(1);
}

function minutosEntre(inicio, fin) {
    return horaAMinutos(fin) - horaAMinutos(inicio);
}

function horaAMinutos(hora) {
    const partes = hora.split(":").map(Number);
    return partes[0] * 60 + partes[1];
}

function normalizarDia(dia) {
    return String(dia || "")
        .trim()
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");
}

function mezclarColor(hex, alpha) {
    const clean = hex.replace("#", "");
    const r = parseInt(clean.substring(0, 2), 16);
    const g = parseInt(clean.substring(2, 4), 16);
    const b = parseInt(clean.substring(4, 6), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}
