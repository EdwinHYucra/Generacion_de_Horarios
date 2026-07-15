document.addEventListener("DOMContentLoaded", () => {
    // Modal de edición de carrera.
    const modalEditar = document.getElementById("modalEditarCarrera");
    const cerrarModalEditar = () => modalEditar?.classList.add("hidden");
    document.querySelectorAll(".btnEditarCarrera").forEach(boton => {
        boton.addEventListener("click", () => {
            document.getElementById("editarCarreraId").value = boton.dataset.id || "";
            document.getElementById("editarCarreraNombre").value = boton.dataset.nombre || "";
            document.getElementById("editarCarreraCodigo").value = boton.dataset.codigo || "";
            modalEditar?.classList.remove("hidden");
            setTimeout(() => document.getElementById("editarCarreraNombre")?.focus(), 0);
        });
    });
    document.querySelectorAll("[data-cerrar-modal-carrera]").forEach(elemento => elemento.addEventListener("click", cerrarModalEditar));
    document.addEventListener("keydown", evento => {
        if (evento.key === "Escape") cerrarModalEditar();
    });

    const entrada = document.getElementById("buscarCursoCarrera");
    const agregar = document.getElementById("agregarCursoCarrera");
    const sugerencias = document.getElementById("sugerenciasCursosCarrera");
    const seleccionados = document.getElementById("cursosSeleccionadosCarrera");
    const vacio = document.getElementById("cursosCarreraVacio");
    if (!entrada || !agregar || !sugerencias || !seleccionados) return;

    const cursos = Array.isArray(cursosCarreraData) ? cursosCarreraData : [];
    let cursoActivo = null;
    const normalizar = valor => String(valor || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();

    // Muestra coincidencias reales del catálogo de cursos.
    const mostrarSugerencias = () => {
        const texto = normalizar(entrada.value.trim());
        sugerencias.innerHTML = "";
        cursoActivo = null;
        if (!texto) {
            sugerencias.classList.remove("is-visible");
            return;
        }
        cursos.filter(curso => normalizar(`${curso.nombre} ${curso.codigo}`).includes(texto))
            .filter(curso => !seleccionados.querySelector(`[data-id="${curso.idCurso}"]`))
            .slice(0, 8)
            .forEach(curso => {
                const boton = document.createElement("button");
                boton.type = "button";
                boton.textContent = `${curso.nombre} (${curso.codigo})`;
                boton.addEventListener("click", () => {
                    cursoActivo = curso;
                    entrada.value = curso.nombre;
                    sugerencias.classList.remove("is-visible");
                });
                sugerencias.appendChild(boton);
            });
        sugerencias.classList.toggle("is-visible", sugerencias.childElementCount > 0);
    };

    const actualizarVacio = () => vacio?.classList.toggle("hidden", seleccionados.children.length > 0);
    const quitar = boton => {
        boton.closest("[data-id]")?.remove();
        actualizarVacio();
    };

    entrada.addEventListener("input", mostrarSugerencias);
    seleccionados.querySelectorAll(".carrera-quitar-curso").forEach(boton => boton.addEventListener("click", () => quitar(boton)));
    agregar.addEventListener("click", () => {
        if (!cursoActivo) {
            const texto = normalizar(entrada.value.trim());
            cursoActivo = cursos.find(curso => normalizar(curso.nombre) === texto || normalizar(curso.codigo) === texto) || null;
        }
        if (!cursoActivo || seleccionados.querySelector(`[data-id="${cursoActivo.idCurso}"]`)) return;

        const chip = document.createElement("span");
        chip.dataset.id = cursoActivo.idCurso;
        chip.innerHTML = `<span></span><button type="button" class="carrera-quitar-curso" aria-label="Quitar curso">×</button><input type="hidden" name="cursoIds">`;
        chip.querySelector("span").textContent = cursoActivo.nombre;
        chip.querySelector("input").value = cursoActivo.idCurso;
        chip.querySelector("button").addEventListener("click", evento => quitar(evento.currentTarget));
        seleccionados.appendChild(chip);
        entrada.value = "";
        cursoActivo = null;
        sugerencias.classList.remove("is-visible");
        actualizarVacio();
    });
    document.addEventListener("click", evento => {
        if (!evento.target.closest(".carreras-agregar-curso")) sugerencias.classList.remove("is-visible");
    });
    actualizarVacio();
});
