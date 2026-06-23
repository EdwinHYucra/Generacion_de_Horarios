document.addEventListener("DOMContentLoaded", () => {

    const btnConfirmar = document.getElementById("btnConfirmar");
    const inputOpcionSeleccionada = document.getElementById("idOpcionSeleccionada");

    const modalDetalle = document.getElementById("modalDetalle");
    const modalObservacion = document.getElementById("modalObservacion");

    const btnCerrarDetalle = document.getElementById("cerrarDetalle");
    const btnCerrarObservacion = document.getElementById("cerrarObservacion");
    const btnCancelarObservacion = document.getElementById("cancelarObservacion");

    let opcionSeleccionada = null;

    /* ==========================
       SELECCIONAR HORARIO
       ========================== */

    document.querySelectorAll(".btn-seleccionar").forEach(btn => {

        btn.addEventListener("click", () => {

            document
                .querySelectorAll(".opcion-card")
                .forEach(card => card.classList.remove("selected"));

            const card = btn.closest(".opcion-card");

            card.classList.add("selected");

            opcionSeleccionada = btn.dataset.id;

            inputOpcionSeleccionada.value = opcionSeleccionada;

            btnConfirmar.disabled = false;
        });

    });

    /* ==========================
       ABRIR DETALLE
       ========================== */

    document.querySelectorAll(".btn-ver-detalle").forEach(btn => {

        btn.addEventListener("click", () => {

            const idOpcion = btn.dataset.id;

            abrirDetalle(idOpcion);

        });

    });

    /* ==========================
       CERRAR DETALLE
       ========================== */

    btnCerrarDetalle.addEventListener("click", () => {

        modalDetalle.classList.add("hidden");

    });

    /* ==========================
       ABRIR OBSERVACION
       ========================== */

    const btnObservacion = document.getElementById("btnObservacion");

    btnObservacion.addEventListener("click", () => {

        modalObservacion.classList.remove("hidden");

    });

    /* ==========================
       CERRAR OBSERVACION
       ========================== */

    btnCerrarObservacion.addEventListener("click", () => {

        modalObservacion.classList.add("hidden");

    });

    btnCancelarObservacion.addEventListener("click", () => {

        modalObservacion.classList.add("hidden");

    });

    /* ==========================
       GUARDAR OBSERVACION
       ========================== */

    document
        .getElementById("formObservacion")
        .addEventListener("submit", function (e) {

            e.preventDefault();

            const tipo =
                document.getElementById("tipoObservacion").value;

            const comentario =
                document.getElementById("comentarioObservacion").value;

            if (!tipo) {

                alert("Seleccione un tipo de observación.");

                return;
            }

            if (!comentario.trim()) {

                alert("Ingrese un comentario.");

                return;
            }

            console.log("Observación registrada");

            console.log({
                opcion: opcionSeleccionada,
                tipo,
                comentario
            });

            alert("Observación enviada correctamente.");

            this.reset();

            modalObservacion.classList.add("hidden");

        });

    /* ==========================
       CERRAR CON BACKDROP
       ========================== */

    document
        .querySelectorAll(".modal-backdrop")
        .forEach(backdrop => {

            backdrop.addEventListener("click", () => {

                modalDetalle.classList.add("hidden");
                modalObservacion.classList.add("hidden");

            });

        });

});


/* ==========================================
   DETALLE DE HORARIO
   ========================================== */

function abrirDetalle(idOpcion) {

    const modalDetalle =
        document.getElementById("modalDetalle");

    const tituloDetalle =
        document.getElementById("tituloDetalle");

    const detalleBody =
        document.getElementById("detalleHorarioBody");

    const detalleCursos =
        document.getElementById("detalleCursos");

    const detalleCreditos =
        document.getElementById("detalleCreditos");

    const detalleCarga =
        document.getElementById("detalleCarga");

    const detalleSedes =
        document.getElementById("detalleSedes");

    const detalleAulas =
        document.getElementById("detalleAulas");

    const idOpcionObservacion =
        document.getElementById("idOpcionObservacion");

    tituloDetalle.textContent =
        "Detalle Opción " + idOpcion;

    idOpcionObservacion.value =
        idOpcion;

    detalleBody.innerHTML = "";

    detalleCursos.innerHTML = "";

    /* ==================================
       SI EXISTE DATA THYMELEAF
       ================================== */

    if (typeof opcionesHorarioData !== "undefined") {

        const opcion =
            opcionesHorarioData.find(
                o => String(o.idOpcion) === String(idOpcion)
            );

        if (opcion) {

            detalleCreditos.textContent =
                opcion.creditos || 0;

            detalleCarga.textContent =
                (opcion.cargaSemanal || 0) + " h";

            detalleSedes.textContent =
                opcion.sedes || 0;

            detalleAulas.textContent =
                opcion.aulas || 0;

            if (opcion.cursos) {

                opcion.cursos.forEach(curso => {

                    detalleCursos.innerHTML += `
                        <div class="curso-detalle">
                            <strong>${curso.nombre}</strong>
                            <span>${curso.codigo}</span>
                        </div>
                    `;

                });

            }

        }

    }

    /* ==================================
       DEMO TABLA
       ================================== */

    const horasDemo = [
        "07:00",
        "09:00",
        "11:00",
        "13:00",
        "15:00",
        "17:00"
    ];

    horasDemo.forEach(hora => {

        detalleBody.innerHTML += `
            <tr>
                <td>${hora}</td>
                <td></td>
                <td class="mini-bloque">BD</td>
                <td></td>
                <td class="mini-bloque">RED</td>
                <td></td>
                <td></td>
            </tr>
        `;

    });

    modalDetalle.classList.remove("hidden");

}