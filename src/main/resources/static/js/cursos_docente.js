const checksCursos = document.querySelectorAll(".curso-check");
const resumenLista = document.getElementById("resumenLista");
const totalCursos = document.getElementById("totalCursos");
const totalHoras = document.getElementById("totalHoras");
const btnGuardarCursos = document.getElementById("btnGuardarCursos");
const limiteHorasMensaje = document.getElementById("limiteHorasMensaje");
const maxHorasSemanales = Number(window.maxHorasSemanales || 40);

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
    const cursosSeleccionados = [];
    const horasSeleccionadas = Number(totalHoras.textContent || 0);

    if (horasSeleccionadas > maxHorasSemanales) {
        alert(`No se puede continuar: la carga supera ${maxHorasSemanales} horas semanales.`);
        return;
    }

    document.querySelectorAll(".curso-check:checked").forEach(check => {
        cursosSeleccionados.push(Number(check.value));
    });

    try {
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
            alert(await response.text());
        } else {
            alert(await response.text());
        }
    } catch (error) {
        console.error(error);
        alert("Error de conexión.");
    }
});

actualizarResumen();
