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

    const normalizar = valor => (valor || "").toString().normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "").toLowerCase().trim();
    const texto = normalizar(input ? input.value : "");
    let visibles = 0;

    filas.forEach(function (fila) {
        let coincide = false;

        atributos.forEach(function (atributo) {
            const valor = normalizar(fila.dataset[atributo]);

            if (valor.includes(texto)) {
                coincide = true;
            }
        });

        if (rowSelector === ".curso-row") {
            fila.dataset.coincideFiltro = coincide ? "true" : "false";
        } else {
            fila.style.display = coincide ? "" : "none";
        }

        if (coincide) {
            visibles++;
        }
    });

    if (contador) {
        contador.textContent = visibles.toString();
    }

    if (rowSelector === ".curso-row") {
        paginaCursosActual = 1;
        renderizarPaginacionCursos();
    }
}

let paginaCursosActual = 1;

// Paginación de cursos adaptada a la altura útil de la ventana.
function renderizarPaginacionCursos() {
    const filas = Array.from(document.querySelectorAll(".curso-row"));
    if (!filas.length) return;
    const coincidentes = filas.filter(fila => fila.dataset.coincideFiltro !== "false");
    const tarjeta = document.querySelector(".module-table-card--cursos");
    const piePagina = document.querySelector(".footer");
    const altoDisponible = window.innerHeight - (tarjeta?.getBoundingClientRect().top || 0)
        - (piePagina?.offsetHeight || 42) - 72;
    const porPagina = window.innerWidth <= 700 ? 7 : Math.max(7, Math.floor(altoDisponible / 40));
    const totalPaginas = Math.max(1, Math.ceil(coincidentes.length / porPagina));
    paginaCursosActual = Math.min(paginaCursosActual, totalPaginas);
    const inicio = (paginaCursosActual - 1) * porPagina;
    const fin = Math.min(inicio + porPagina, coincidentes.length);

    filas.forEach(fila => { fila.style.display = "none"; });
    coincidentes.slice(inicio, fin).forEach(fila => { fila.style.display = ""; });
    document.getElementById("rangoCursos").textContent = coincidentes.length ? `${inicio + 1} a ${fin}` : "0";
    document.getElementById("cantidadCursos").textContent = coincidentes.length.toString();

    const paginacion = document.getElementById("paginacionCursos");
    paginacion.innerHTML = "";
    paginacion.appendChild(crearBotonPaginaCurso("‹", paginaCursosActual - 1, paginaCursosActual === 1));
    for (let pagina = 1; pagina <= totalPaginas; pagina += 1) {
        const boton = crearBotonPaginaCurso(String(pagina), pagina, false);
        boton.classList.toggle("is-active", pagina === paginaCursosActual);
        paginacion.appendChild(boton);
    }
    paginacion.appendChild(crearBotonPaginaCurso("›", paginaCursosActual + 1, paginaCursosActual === totalPaginas));
}

function crearBotonPaginaCurso(texto, pagina, deshabilitado) {
    const boton = document.createElement("button");
    boton.type = "button";
    boton.textContent = texto;
    boton.disabled = deshabilitado;
    boton.addEventListener("click", () => {
        paginaCursosActual = pagina;
        renderizarPaginacionCursos();
    });
    return boton;
}

let paginaDocentesActual = 1;
let docentesPorPagina = 6;

function filtrarDocentes() {
    const normalizar = valor => (valor || "").toString().normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "").toLowerCase().trim();
    const textoBusqueda = normalizar(document.getElementById("inputBuscarDocente")?.value);
    const carrera = normalizar(document.getElementById("filtroCarrera")?.value);
    const estado = normalizar(document.getElementById("filtroEstado")?.value);
    const filas = document.querySelectorAll(".docente-row");

    let visibles = 0;

    filas.forEach(function (fila) {
        const nombre = normalizar(fila.dataset.nombre);
        const codigo = normalizar(fila.dataset.codigo);
        const dni = normalizar(fila.dataset.dni);
        const carreraFila = normalizar(fila.dataset.carrera);
        const estadoFila = normalizar(fila.dataset.estado);

        const coincideTexto =
            nombre.includes(textoBusqueda) ||
            codigo.includes(textoBusqueda) ||
            dni.includes(textoBusqueda);

        const coincideCarrera = carrera === "" || carreraFila === carrera;
        const coincideEstado = estado === "" || estadoFila === estado;

        const visible = coincideTexto && coincideCarrera && coincideEstado;

        fila.dataset.coincideFiltro = visible ? "true" : "false";

        if (visible) {
            visibles++;
        }
    });

    paginaDocentesActual = 1;
    renderizarPaginacionDocentes();
}

// Paginación dinámica: muestra seis docentes por página después de aplicar filtros.
function renderizarPaginacionDocentes() {
    const filas = Array.from(document.querySelectorAll(".docente-row"));
    docentesPorPagina = calcularDocentesPorPagina();
    const coincidentes = filas.filter(fila => fila.dataset.coincideFiltro !== "false");
    const totalPaginas = Math.max(1, Math.ceil(coincidentes.length / docentesPorPagina));
    paginaDocentesActual = Math.min(paginaDocentesActual, totalPaginas);
    const inicio = (paginaDocentesActual - 1) * docentesPorPagina;
    const fin = Math.min(inicio + docentesPorPagina, coincidentes.length);

    filas.forEach(fila => { fila.style.display = "none"; });
    coincidentes.slice(inicio, fin).forEach(fila => { fila.style.display = ""; });

    const rango = document.getElementById("rangoDocentes");
    const cantidad = document.getElementById("cantidadDocentes");
    if (rango) rango.textContent = coincidentes.length ? `${inicio + 1} a ${fin}` : "0";
    if (cantidad) cantidad.textContent = coincidentes.length.toString();

    const paginacion = document.getElementById("paginacionDocentes");
    if (!paginacion) return;
    paginacion.innerHTML = "";
    paginacion.appendChild(crearBotonPagina("‹", paginaDocentesActual - 1, paginaDocentesActual === 1));

    for (let pagina = 1; pagina <= totalPaginas; pagina += 1) {
        if (totalPaginas > 6 && pagina > 3 && pagina < totalPaginas) {
            if (pagina === 4) {
                const puntos = document.createElement("span");
                puntos.textContent = "…";
                paginacion.appendChild(puntos);
            }
            continue;
        }
        const boton = crearBotonPagina(String(pagina), pagina, false);
        boton.classList.toggle("is-active", pagina === paginaDocentesActual);
        paginacion.appendChild(boton);
    }
    paginacion.appendChild(crearBotonPagina("›", paginaDocentesActual + 1, paginaDocentesActual === totalPaginas));
}

// Aprovecha la altura disponible sin dejar un bloque vacío innecesario bajo la tabla.
function calcularDocentesPorPagina() {
    const tabla = document.querySelector(".docente-table-card");
    const pieTabla = document.querySelector(".docente-table-footer");
    const piePagina = document.querySelector(".footer");
    if (!tabla || window.innerWidth <= 760) return 6;

    const altoFila = 38;
    const espacioInferior = (pieTabla?.offsetHeight || 36) + (piePagina?.offsetHeight || 42) + 34;
    const altoDisponible = window.innerHeight - tabla.getBoundingClientRect().top - espacioInferior;
    return Math.max(6, Math.floor(altoDisponible / altoFila));
}

function crearBotonPagina(texto, pagina, deshabilitado) {
    const boton = document.createElement("button");
    boton.type = "button";
    boton.textContent = texto;
    boton.disabled = deshabilitado;
    boton.addEventListener("click", () => {
        paginaDocentesActual = pagina;
        renderizarPaginacionDocentes();
    });
    return boton;
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
    // Navegación móvil: sidebar lateral oculto con fondo de cierre.
    /* El menú lateral común se inicializa una sola vez desde admin_layout.js. */
    const sidebar = null;
    const botonMenu = document.querySelector(".topbar__menu");
    let fondoMenu = document.querySelector(".sidebar-backdrop");
    if (sidebar && botonMenu) {
        if (!fondoMenu) {
            fondoMenu = document.createElement("button");
            fondoMenu.type = "button"; fondoMenu.className = "sidebar-backdrop";
            fondoMenu.setAttribute("aria-label", "Cerrar menú"); document.body.appendChild(fondoMenu);
        }
        const cerrarMenu = () => { sidebar.classList.remove("sidebar--open"); fondoMenu.classList.remove("sidebar-backdrop--show"); botonMenu.setAttribute("aria-expanded", "false"); document.body.classList.remove("menu-mobile-open"); };
        const abrirMenu = () => { sidebar.classList.add("sidebar--open"); fondoMenu.classList.add("sidebar-backdrop--show"); botonMenu.setAttribute("aria-expanded", "true"); document.body.classList.add("menu-mobile-open"); };
        botonMenu.setAttribute("aria-expanded", "false");
        botonMenu.addEventListener("click", () => sidebar.classList.contains("sidebar--open") ? cerrarMenu() : abrirMenu());
        fondoMenu.addEventListener("click", cerrarMenu);
        sidebar.querySelectorAll("a").forEach(enlace => enlace.addEventListener("click", cerrarMenu));
        document.addEventListener("keydown", evento => { if (evento.key === "Escape") cerrarMenu(); });
    }
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
    if (document.getElementById("tablaDocentesBody")) filtrarDocentes();

    // Recalcula las filas visibles cuando cambia el tamaño de la ventana.
    let temporizadorRedimension;
    window.addEventListener("resize", function () {
        window.clearTimeout(temporizadorRedimension);
        temporizadorRedimension = window.setTimeout(function () {
            if (document.getElementById("tablaDocentesBody")) renderizarPaginacionDocentes();
            if (document.getElementById("tablaCursosBody")) renderizarPaginacionCursos();
        }, 120);
    });

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
    if (document.getElementById("tablaCursosBody")) {
        filtrarTabla("inputBuscarCurso", ".curso-row", "cantidadCursos", ["codigo", "nombre", "tipo"]);
    }
    document.getElementById("btnBuscarCurso")?.addEventListener("click", function () {
        filtrarTabla("inputBuscarCurso", ".curso-row", "cantidadCursos", ["codigo", "nombre", "tipo"]);
    });

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

    const accion = opcion.observacion === "EN_REVISION"
        ? `<div class="horario-modal-actions"><a class="btn btn--primary" href="/administrador/horarios/editar/${opcion.idHorario}">Expandir y editar</a></div>`
        : opcion.observacion === "APROBADA_DOCENTE"
        ? `<form action="/administrador/horarios/aprobar/${opcion.idHorario}" method="post"
              class="horario-modal-actions">
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
