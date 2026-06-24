const opcionEvaluacion = document.getElementById("opcionEvaluacion");
const idDocente = document.getElementById("idDocente");
const idCurso = document.getElementById("idCurso");
const puntajeInput = document.getElementById("puntajeInput");
const categoriaPreview = document.getElementById("categoriaPreview");

function actualizarRelacion() {
    const selected = opcionEvaluacion.options[opcionEvaluacion.selectedIndex];
    idDocente.value = selected?.dataset.docente || "";
    idCurso.value = selected?.dataset.curso || "";
}

function clasificarPuntaje(valor) {
    const puntaje = Number(valor);

    if (!puntaje || puntaje < 1 || puntaje > 20) {
        return "Seleccione un puntaje";
    }

    if (puntaje <= 15) {
        return "Categoria: Malo";
    }

    if (puntaje <= 18) {
        return "Categoria: Neutral";
    }

    return "Categoria: Positivo";
}

opcionEvaluacion?.addEventListener("change", actualizarRelacion);
puntajeInput?.addEventListener("input", () => {
    categoriaPreview.textContent = clasificarPuntaje(puntajeInput.value);
});

actualizarRelacion();
if (puntajeInput) {
    categoriaPreview.textContent = clasificarPuntaje(puntajeInput.value);
}
