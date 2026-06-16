const checksCursos = document.querySelectorAll(".curso-check");
const resumenLista = document.getElementById("resumenLista");
const totalCursos = document.getElementById("totalCursos");
const totalHoras = document.getElementById("totalHoras");
const btnGuardarCursos = document.getElementById("btnGuardarCursos");

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
}

checksCursos.forEach(check => {
    check.addEventListener("change", actualizarResumen);
});

btnGuardarCursos.addEventListener("click", async () => {
    const cursosSeleccionados = [];

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
            alert("Cursos guardados correctamente.");
        } else {
            alert("Error al guardar los cursos.");
        }
    } catch (error) {
        console.error(error);
        alert("Error de conexión.");
    }
});

actualizarResumen();