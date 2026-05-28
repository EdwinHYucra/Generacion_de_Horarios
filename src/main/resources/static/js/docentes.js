document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("modalDocente");
    const btnAbrir = document.getElementById("btnAbrirModal");
    const btnCerrar = document.getElementById("btnCerrarModal");
    const btnCancelar = document.getElementById("btnCancelarModal");

    const modalEliminar = document.getElementById("modalEliminarDocente");
    const formEliminar = document.getElementById("formEliminarDocente");
    const nombreDocenteEliminar = document.getElementById("nombreDocenteEliminar");
    const btnCancelarEliminar = document.getElementById("btnCancelarEliminar");
    const botonesEliminar = document.querySelectorAll(".btnEliminarDocente");

    const inputBuscar = document.getElementById("inputBuscarDocente");
    const filtroCarrera = document.getElementById("filtroCarrera");
    const filtroOrden = document.getElementById("filtroOrden");
    const filtroEstado = document.getElementById("filtroEstado");
    const cantidadDocentes = document.getElementById("cantidadDocentes");

    const estaEnModoEdicion = modal && modal.classList.contains("is-open");

    function abrirModal() {
        modal.classList.add("is-open");
        modal.setAttribute("aria-hidden", "false");
        document.body.style.overflow = "hidden";
    }

    function cerrarModal() {
        modal.classList.remove("is-open");
        modal.setAttribute("aria-hidden", "true");
        document.body.style.overflow = "";

        if (estaEnModoEdicion) {
            window.location.href = "/docentes";
        }
    }

    if (modal && estaEnModoEdicion) {
        document.body.style.overflow = "hidden";
    }

    if (btnAbrir) btnAbrir.addEventListener("click", abrirModal);
    if (btnCerrar) btnCerrar.addEventListener("click", cerrarModal);
    if (btnCancelar) btnCancelar.addEventListener("click", cerrarModal);

    if (modal) {
        modal.addEventListener("click", function (event) {
            if (event.target === modal) {
                cerrarModal();
            }
        });
    }

    function abrirModalEliminar(id, nombre) {
        if (!modalEliminar || !formEliminar) return;

        formEliminar.action = "/docentes/eliminar/" + id;
        nombreDocenteEliminar.textContent = nombre || "seleccionado";

        modalEliminar.classList.add("is-open");
        modalEliminar.setAttribute("aria-hidden", "false");
        document.body.style.overflow = "hidden";
    }

    function cerrarModalEliminar() {
        if (!modalEliminar) return;

        modalEliminar.classList.remove("is-open");
        modalEliminar.setAttribute("aria-hidden", "true");
        document.body.style.overflow = "";
    }

    botonesEliminar.forEach(function (boton) {
        boton.addEventListener("click", function () {
            const id = boton.dataset.id;
            const nombre = boton.dataset.nombre;
            abrirModalEliminar(id, nombre);
        });
    });

    if (btnCancelarEliminar) {
        btnCancelarEliminar.addEventListener("click", cerrarModalEliminar);
    }

    if (modalEliminar) {
        modalEliminar.addEventListener("click", function (event) {
            if (event.target === modalEliminar) {
                cerrarModalEliminar();
            }
        });
    }

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            if (modal && modal.classList.contains("is-open")) {
                cerrarModal();
            }

            if (modalEliminar && modalEliminar.classList.contains("is-open")) {
                cerrarModalEliminar();
            }
        }
    });

    function normalizarTexto(texto) {
        return (texto || "")
            .toString()
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "")
            .trim();
    }

    function obtenerFilasDocentes() {
        return Array.from(document.querySelectorAll(".docente-row"));
    }

    function aplicarFiltros() {
        const busqueda = normalizarTexto(inputBuscar ? inputBuscar.value : "");
        const carrera = filtroCarrera ? filtroCarrera.value : "";
        const estado = filtroEstado ? filtroEstado.value : "";

        const filas = obtenerFilasDocentes();
        let visibles = 0;

        filas.forEach(function (fila) {
            const nombre = normalizarTexto(fila.dataset.nombre);
            const codigo = normalizarTexto(fila.dataset.codigo);
            const carreraFila = fila.dataset.carrera || "";
            const estadoFila = fila.dataset.estado || "";

            const coincideBusqueda =
                nombre.includes(busqueda) ||
                codigo.includes(busqueda);

            const coincideCarrera =
                carrera === "" || carreraFila === carrera;

            const coincideEstado =
                estado === "" || estadoFila === estado;

            const mostrar = coincideBusqueda && coincideCarrera && coincideEstado;

            fila.style.display = mostrar ? "" : "none";

            if (mostrar) visibles++;
        });

        if (cantidadDocentes) {
            cantidadDocentes.textContent = visibles.toString();
        }
    }

    function aplicarOrden() {
        if (!filtroOrden) return;

        const orden = filtroOrden.value;
        const tbody = document.getElementById("tablaDocentesBody");
        const filas = obtenerFilasDocentes();

        if (!tbody || orden === "") return;

        filas.sort(function (a, b) {
            const nombreA = normalizarTexto(a.dataset.nombre);
            const nombreB = normalizarTexto(b.dataset.nombre);

            if (orden === "az") return nombreA.localeCompare(nombreB);
            if (orden === "za") return nombreB.localeCompare(nombreA);

            return 0;
        });

        filas.forEach(function (fila) {
            tbody.appendChild(fila);
        });

        aplicarFiltros();
    }

    if (inputBuscar) inputBuscar.addEventListener("input", aplicarFiltros);
    if (filtroCarrera) filtroCarrera.addEventListener("change", aplicarFiltros);
    if (filtroEstado) filtroEstado.addEventListener("change", aplicarFiltros);
    if (filtroOrden) filtroOrden.addEventListener("change", aplicarOrden);
});