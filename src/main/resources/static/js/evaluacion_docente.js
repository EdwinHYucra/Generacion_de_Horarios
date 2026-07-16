const opcionEvaluacion = document.getElementById("opcionEvaluacion");
const idDocente = document.getElementById("idDocente");
const idCurso = document.getElementById("idCurso");
const puntajeInput = document.getElementById("puntajeInput");
const categoriaPreview = document.getElementById("categoriaPreview");
const puntajeRadios = document.querySelectorAll('input[name="puntajeRadio"]');

function actualizarRelacion() {
    const selected = opcionEvaluacion.options[opcionEvaluacion.selectedIndex];
    idDocente.value = selected?.dataset.docente || "";
    idCurso.value = selected?.dataset.curso || "";
}

function clasificarPuntaje(valor) {
    if (valor === "") {
        return "Seleccione un puntaje";
    }

    const puntaje = Number(valor);

    if (Number.isNaN(puntaje) || puntaje < 0 || puntaje > 10) {
        return "Seleccione un puntaje";
    }

    if (puntaje <= 6) {
        return "Categoria: Malo";
    }

    if (puntaje <= 8) {
        return "Categoria: Neutral";
    }

    return "Categoria: Positivo";
}

opcionEvaluacion?.addEventListener("change", actualizarRelacion);
puntajeRadios.forEach(radio => {
    radio.addEventListener("change", () => {
        puntajeInput.value = radio.value;
        categoriaPreview.textContent = clasificarPuntaje(radio.value);

        document.querySelectorAll(".score-option").forEach(option => {
            option.classList.toggle("is-selected", option.contains(radio));
        });
    });
});

actualizarRelacion();
if (puntajeInput) {
    categoriaPreview.textContent = clasificarPuntaje(puntajeInput.value);
}

const radioInicial = [...puntajeRadios].find(radio => radio.checked);
if (radioInicial && puntajeInput) {
    puntajeInput.value = radioInicial.value;
    categoriaPreview.textContent = clasificarPuntaje(radioInicial.value);
}
