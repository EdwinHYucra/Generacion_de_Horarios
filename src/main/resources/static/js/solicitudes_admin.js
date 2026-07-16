document.querySelectorAll(".solicitud-respuesta-form").forEach(formulario => {
    const comentario = formulario.querySelector("textarea[name='comentarioAdministrador']");
    const mensaje = formulario.querySelector(".solicitud-respuesta-error");

    formulario.addEventListener("submit", evento => {
        const valido = comentario.value.trim().length >= 3;
        mensaje.hidden = valido;
        comentario.classList.toggle("is-invalid", !valido);

        if (!valido) {
            evento.preventDefault();
            comentario.focus();
        }
    });

    comentario.addEventListener("input", () => {
        if (comentario.value.trim().length >= 3) {
            mensaje.hidden = true;
            comentario.classList.remove("is-invalid");
        }
    });
});
