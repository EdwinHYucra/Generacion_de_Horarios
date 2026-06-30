function abrirModal(id) {
    const modal = document.getElementById(id);

    if (modal) {
        modal.classList.add("modal--show");
        modal.setAttribute("aria-hidden", "false");
    }
}

function cerrarModal(id) {
    const modal = document.getElementById(id);

    if (modal) {
        modal.classList.remove("modal--show");
        modal.setAttribute("aria-hidden", "true");
    }
}

function cerrarModalDelete(id) {
    const modal = document.getElementById(id);

    if (modal) {
        modal.classList.remove("is-open");
        modal.setAttribute("aria-hidden", "true");
    }
}

function abrirModalDelete(idModal, actionUrl, nombreTexto, idNombreElemento) {
    const modal = document.getElementById(idModal);
    const form = modal ? modal.querySelector("form") : null;
    const nombreElemento = document.getElementById(idNombreElemento);

    if (nombreElemento) {
        nombreElemento.textContent = nombreTexto || "seleccionado";
    }

    if (form && actionUrl) {
        form.setAttribute("action", actionUrl);
    }

    if (modal) {
        modal.classList.add("is-open");
        modal.setAttribute("aria-hidden", "false");
    }
}

function filtrarTabla(inputId, rowSelector, counterId, atributos) {
    const input = document.getElementById(inputId);
    const filas = document.querySelectorAll(rowSelector);
    const contador = document.getElementById(counterId);

    const texto = input ? input.value.toLowerCase().trim() : "";
    let visibles = 0;

    filas.forEach(function (fila) {
        let coincide = false;

        atributos.forEach(function (atributo) {
            const valor = (fila.dataset[atributo] || "").toLowerCase();

            if (valor.includes(texto)) {
                coincide = true;
            }
        });

        fila.style.display = coincide ? "" : "none";

        if (coincide) {
            visibles++;
        }
    });

    if (contador) {
        contador.textContent = visibles.toString();
    }
}

function filtrarDocentes() {
    const textoBusqueda = document.getElementById("inputBuscarDocente")?.value.toLowerCase().trim() || "";
    const carrera = document.getElementById("filtroCarrera")?.value || "";
    const estado = document.getElementById("filtroEstado")?.value || "";
    const filas = document.querySelectorAll(".docente-row");

    let visibles = 0;

    filas.forEach(function (fila) {
        const nombre = (fila.dataset.nombre || "").toLowerCase();
        const codigo = (fila.dataset.codigo || "").toLowerCase();
        const dni = (fila.dataset.dni || "").toLowerCase();
        const carreraFila = fila.dataset.carrera || "";
        const estadoFila = fila.dataset.estado || "";

        const coincideTexto =
            nombre.includes(textoBusqueda) ||
            codigo.includes(textoBusqueda) ||
            dni.includes(textoBusqueda);

        const coincideCarrera = carrera === "" || carreraFila === carrera;
        const coincideEstado = estado === "" || estadoFila === estado;

        const visible = coincideTexto && coincideCarrera && coincideEstado;

        fila.style.display = visible ? "" : "none";

        if (visible) {
            visibles++;
        }
    });

    const cantidad = document.getElementById("cantidadDocentes");

    if (cantidad) {
        cantidad.textContent = visibles.toString();
    }
}

function ordenarDocentes() {
    const orden = document.getElementById("filtroOrden")?.value || "";
    const tbody = document.getElementById("tablaDocentesBody");

    if (!tbody || orden === "") {
        return;
    }

    const filas = Array.from(tbody.querySelectorAll(".docente-row"));

    filas.sort(function (a, b) {
        const nombreA = (a.dataset.nombre || "").toLowerCase();
        const nombreB = (b.dataset.nombre || "").toLowerCase();

        if (orden === "az") {
            return nombreA.localeCompare(nombreB);
        }

        if (orden === "za") {
            return nombreB.localeCompare(nombreA);
        }

        return 0;
    });

    filas.forEach(function (fila) {
        tbody.appendChild(fila);
    });

    filtrarDocentes();
}

document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".modal__backdrop").forEach(function (backdrop) {
        backdrop.addEventListener("click", function () {
            const modal = backdrop.closest(".modal");

            if (modal) {
                modal.classList.remove("modal--show");
                modal.setAttribute("aria-hidden", "true");
            }
        });
    });

    document.querySelectorAll(".modal-delete__backdrop").forEach(function (backdrop) {
        backdrop.addEventListener("click", function () {
            const modal = backdrop.closest(".modal-delete");

            if (modal) {
                modal.classList.remove("is-open");
                modal.setAttribute("aria-hidden", "true");
            }
        });
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            document.querySelectorAll(".modal.modal--show").forEach(function (modal) {
                modal.classList.remove("modal--show");
                modal.setAttribute("aria-hidden", "true");
            });

            document.querySelectorAll(".modal-delete.is-open").forEach(function (modal) {
                modal.classList.remove("is-open");
                modal.setAttribute("aria-hidden", "true");
            });
        }
    });

    const inputBuscarDocente = document.getElementById("inputBuscarDocente");
    const filtroCarrera = document.getElementById("filtroCarrera");
    const filtroEstado = document.getElementById("filtroEstado");
    const filtroOrden = document.getElementById("filtroOrden");

    if (inputBuscarDocente) inputBuscarDocente.addEventListener("input", filtrarDocentes);
    if (filtroCarrera) filtroCarrera.addEventListener("change", filtrarDocentes);
    if (filtroEstado) filtroEstado.addEventListener("change", filtrarDocentes);
    if (filtroOrden) filtroOrden.addEventListener("change", ordenarDocentes);

    document.querySelectorAll(".btnEliminarDocente").forEach(function (boton) {
        boton.addEventListener("click", function () {
            abrirModalDelete(
                "modalEliminarDocente",
                "/administrador/docentes/eliminar/" + boton.dataset.id,
                boton.dataset.nombre,
                "nombreDocenteEliminar"
            );
        });
    });

    const btnCancelarEliminar = document.getElementById("btnCancelarEliminar");
    if (btnCancelarEliminar) {
        btnCancelarEliminar.addEventListener("click", function () {
            cerrarModalDelete("modalEliminarDocente");
        });
    }

    const inputBuscarCurso = document.getElementById("inputBuscarCurso");
    if (inputBuscarCurso) {
        inputBuscarCurso.addEventListener("input", function () {
            filtrarTabla("inputBuscarCurso", ".curso-row", "cantidadCursos", ["codigo", "nombre", "tipo"]);
        });
    }

    const inputBuscarCarrera = document.getElementById("inputBuscarCarrera");
    if (inputBuscarCarrera) {
        inputBuscarCarrera.addEventListener("input", function () {
            filtrarTabla("inputBuscarCarrera", ".carrera-row", "cantidadCarreras", ["codigo", "nombre"]);
        });
    }

    const inputBuscarAula = document.getElementById("inputBuscarAula");
    if (inputBuscarAula) {
        inputBuscarAula.addEventListener("input", function () {
            filtrarTabla("inputBuscarAula", ".aula-row", "cantidadAulas", ["codigo", "nombre", "tipo", "ubicacion", "sede"]);
        });
    }

    const inputBuscarSede = document.getElementById("inputBuscarSede");
    if (inputBuscarSede) {
        inputBuscarSede.addEventListener("input", function () {
            filtrarTabla("inputBuscarSede", ".sede-row", "cantidadSedes", ["codigo", "nombre", "direccion"]);
        });
    }

    const inputBuscarAsignacion = document.getElementById("inputBuscarAsignacion");
    if (inputBuscarAsignacion) {
        inputBuscarAsignacion.addEventListener("input", function () {
            filtrarTabla("inputBuscarAsignacion", ".asignacion-row", "cantidadAsignaciones", ["carrera", "curso", "codigo"]);
        });
    }
});

function abrirModalOpciones(boton) {
    const docenteId = boton.dataset.docenteId;
    const docente = boton.dataset.docente || "Docente";
    const opciones = opcionesPorDocente ? opcionesPorDocente[docenteId] : [];
    const modal = document.getElementById("modalOpcionesHorario");

    document.getElementById("modalDocenteTitulo").textContent = "Horarios generados: " + docente;
    renderizarTabsOpcionesAdmin(opciones || []);
    renderizarOpcionAdmin((opciones || [])[0]);

    if (modal) {
        modal.classList.add("modal--show");
        modal.setAttribute("aria-hidden", "false");
    }
}

function cerrarModalOpciones() {
    cerrarModal("modalOpcionesHorario");
}

function renderizarTabsOpcionesAdmin(opciones) {
    const contenedor = document.getElementById("modalOpcionesTabs");
    contenedor.innerHTML = "";

    opciones.forEach(function (opcion, index) {
        const boton = document.createElement("button");
        boton.type = "button";
        boton.className = "horario-tab" + (index === 0 ? " is-active" : "");
        boton.textContent = "Opcion " + opcion.opcion;
        boton.addEventListener("click", function () {
            document.querySelectorAll(".horario-tab").forEach(function (tab) {
                tab.classList.remove("is-active");
            });
            boton.classList.add("is-active");
            renderizarOpcionAdmin(opcion);
        });
        contenedor.appendChild(boton);
    });
}

function renderizarOpcionAdmin(opcion) {
    const contenedor = document.getElementById("modalOpcionesContenido");
    if (!opcion) {
        contenedor.innerHTML = '<p class="empty-table">No hay opciones para este docente.</p>';
        return;
    }

    const horas = obtenerHorasAdmin(opcion);
    if (horas.length === 0) {
        contenedor.innerHTML = `
            <div class="horario-modal-summary">
                <div>
                    <strong>Opcion ${opcion.opcion}</strong>
                    <small>Horario #${opcion.idHorario || "-"} - ${opcion.observacion || "PENDIENTE"}</small>
                </div>
                <span>0 bloques generados</span>
            </div>
            <p class="empty-table">Esta propuesta todavia no tiene bloques de horario.</p>
        `;
        return;
    }

    const filas = horas.map(function (hora) {
        const celdas = diasHorarioAdmin.map(function (dia) {
            const bloques = (opcion.bloques || []).filter(function (bloque) {
                return normalizarDiaAdmin(bloque.dia) === normalizarDiaAdmin(dia)
                    && bloque.horaInicio === hora;
            });

            return "<td>" + bloques.map(renderizarBloqueAdmin).join("") + "</td>";
        }).join("");

        return '<tr><td class="horario-hora">' + hora + "</td>" + celdas + "</tr>";
    }).join("");

    const accion = opcion.observacion === "APROBADA_DOCENTE"
        ? `<form action="/administrador/horarios/aprobar/${opcion.idHorario}" method="post"
              class="horario-modal-actions"
              onsubmit="return confirm('Aprobar definitivamente esta opcion de horario?');">
            <button type="submit" class="btn btn--primary">Aprobar opcion</button>
        </form>`
        : `<div class="horario-modal-actions">
            <span class="badge">${opcion.observacion || "PENDIENTE"}</span>
        </div>`;

    contenedor.innerHTML = `
        <div class="horario-modal-summary">
            <div>
                <strong>Opcion ${opcion.opcion}</strong>
                <small>Horario #${opcion.idHorario || "-"} - ${opcion.observacion || "PENDIENTE"}</small>
            </div>
            <span>${(opcion.bloques || []).length} bloques generados</span>
        </div>
        <div class="table-wrapper">
            <table class="horario-modal-table">
                <thead>
                    <tr>
                        <th>Hora</th>
                        ${diasHorarioAdmin.map(function (dia) { return "<th>" + dia + "</th>"; }).join("")}
                    </tr>
                </thead>
                <tbody>${filas}</tbody>
            </table>
        </div>
        ${accion}
    `;
}

function renderizarBloqueAdmin(bloque) {
    return `
        <div class="horario-modal-block">
            <strong>${bloque.curso}</strong>
            <span>${bloque.horaInicio} - ${bloque.horaFin}</span>
            <small>${bloque.aula} | ${bloque.sede}</small>
        </div>
    `;
}

function obtenerHorasAdmin(opcion) {
    const horas = new Set();
    (opcion.bloques || []).forEach(function (bloque) {
        horas.add(bloque.horaInicio);
    });
    return Array.from(horas).sort();
}

function normalizarDiaAdmin(dia) {
    return String(dia || "")
        .trim()
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");
}
