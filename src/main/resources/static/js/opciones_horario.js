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

function abrirDetalle(idHorario) {

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
        "Detalle opcion " + idHorario;

    idOpcionObservacion.value =
        idHorario;

    detalleBody.innerHTML = "";

    detalleCursos.innerHTML = "";

    /* ==================================
       SI EXISTE DATA THYMELEAF
       ================================== */

    if (typeof opcionesHorarioData !== "undefined") {

        const opcion =
            opcionesHorarioData.find(
                o => String(o.idHorario) === String(idHorario)
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

            if (opcion.bloques) {
                const cursos = [...new Set(opcion.bloques.map(bloque => bloque.curso))];
                cursos.forEach(curso => {

                    detalleCursos.innerHTML += `
                        <div class="curso-detalle">
                            <strong>${curso}</strong>
                        </div>
                    `;

                });

            }

            opcion.bloques.forEach(bloque => {
                detalleBody.innerHTML += `
                    <tr>
                        <td>${bloque.horaInicio} - ${bloque.horaFin}</td>
                        <td>${bloque.dia === "Lunes" ? bloque.curso : ""}</td>
                        <td>${bloque.dia === "Martes" ? bloque.curso : ""}</td>
                        <td>${bloque.dia === "Miercoles" ? bloque.curso : ""}</td>
                        <td>${bloque.dia === "Jueves" ? bloque.curso : ""}</td>
                        <td>${bloque.dia === "Viernes" ? bloque.curso : ""}</td>
                        <td>${bloque.dia === "Sabado" ? bloque.curso : ""}</td>
                    </tr>
                `;
            });

        }

    }

    modalDetalle.classList.remove("hidden");

}
