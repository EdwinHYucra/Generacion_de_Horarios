document.addEventListener("DOMContentLoaded", () => {
    const contenedor = document.getElementById("docenteHorarioSemanal");
    const detalles = Array.isArray(window.docenteDetalleHorario)
        ? window.docenteDetalleHorario
        : (typeof docenteDetalleHorario !== "undefined" ? docenteDetalleHorario : []);

    // Botón superior: permite guardar el horario mediante el diálogo de impresión del navegador.
    document.getElementById("docenteImprimirHorario")?.addEventListener("click", () => window.print());
    if (!contenedor || !detalles.length) return;

    // Un curso se presenta una sola vez y conserva todas sus sesiones semanales.
    const contenedorCursos = document.querySelector(".docente-horario-cursos");
    if (contenedorCursos) {
        const cursos = new Map();
        const ordenDias = ["lunes", "martes", "miercoles", "jueves", "viernes", "sabado", "domingo"];
        const normalizarDia = valor => String(valor || "").normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "").toLowerCase();
        detalles.forEach(detalle => {
            const clave = detalle.idCurso != null ? `curso-${detalle.idCurso}` : `nombre-${detalle.curso}`;
            if (!cursos.has(clave)) cursos.set(clave, []);
            cursos.get(clave).push(detalle);
        });
        contenedorCursos.innerHTML = "";
        Array.from(cursos.values()).forEach((sesiones, indice) => {
            sesiones.sort((a, b) => {
                const diaA = ordenDias.indexOf(normalizarDia(a.dia));
                const diaB = ordenDias.indexOf(normalizarDia(b.dia));
                const posicionA = diaA < 0 ? ordenDias.length : diaA;
                const posicionB = diaB < 0 ? ordenDias.length : diaB;
                return posicionA - posicionB || String(a.horaInicio || "").localeCompare(String(b.horaInicio || ""));
            });
            const desplegable = document.createElement("details");
            desplegable.className = "docente-horario-curso";
            desplegable.open = indice === 0;
            const resumen = document.createElement("summary");
            const flecha = document.createElement("span");
            flecha.className = "docente-horario-curso__flecha";
            flecha.textContent = "›";
            const nombre = document.createElement("strong");
            nombre.textContent = sesiones[0].curso || "Curso";
            const cantidad = document.createElement("small");
            cantidad.className = "docente-horario-curso__cantidad";
            cantidad.textContent = `${sesiones.length} ${sesiones.length === 1 ? "sesión" : "sesiones"}`;
            resumen.append(flecha, nombre, cantidad);

            const listaSesiones = document.createElement("div");
            listaSesiones.className = "docente-horario-curso__sesiones";
            sesiones.forEach(sesion => {
                const articulo = document.createElement("article");
                articulo.className = "docente-horario-sesion";
                const horario = document.createElement("h3");
                horario.textContent = `${sesion.dia} · ${sesion.horaInicio} - ${sesion.horaFin}`;
                const datos = document.createElement("div");
                datos.className = "docente-horario-curso__contenido";
                [["Ubicación", sesion.aula || "Por asignar"], ["Sede", sesion.sede || "Por asignar"], ["Tipo de clase", "Presencial"]]
                    .forEach(([etiqueta, valor]) => {
                        const dato = document.createElement("div");
                        const titulo = document.createElement("span"); titulo.textContent = etiqueta;
                        const contenido = document.createElement("strong"); contenido.textContent = valor;
                        dato.append(titulo, contenido); datos.appendChild(dato);
                    });
                articulo.append(horario, datos); listaSesiones.appendChild(articulo);
            });
            desplegable.append(resumen, listaSesiones); contenedorCursos.appendChild(desplegable);
        });
    }

    const dias = ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"];
    const normalizar = valor => String(valor || "").normalize("NFD").replace(/[\u0300-\u036f]/g, "").toLowerCase();
    const minutos = valor => {
        const [hora, minuto] = String(valor || "00:00").split(":").map(Number);
        return hora * 60 + minuto;
    };
    const inicioDia = 7 * 60;
    const finDia = 22 * 60;
    const altoHora = 52;

    // Cabecera con los siete días de la semana.
    ["Horario", ...dias].forEach(texto => {
        const celda = document.createElement("div");
        celda.className = "docente-horario-celda docente-horario-celda--cabecera";
        celda.textContent = texto;
        contenedor.appendChild(celda);
    });

    // Columna izquierda con las horas del día.
    for (let hora = 7; hora < 22; hora += 1) {
        const celda = document.createElement("div");
        celda.className = "docente-horario-celda docente-horario-celda--hora";
        celda.style.gridColumn = "1";
        celda.textContent = `${String(hora).padStart(2, "0")}:00`;
        contenedor.appendChild(celda);
    }

    // Columnas y bloques calculados con día, inicio y fin recibidos desde Thymeleaf.
    dias.forEach((dia, indice) => {
        const columna = document.createElement("div");
        columna.className = "docente-horario-columna";
        columna.style.gridColumn = String(indice + 2);

        detalles.filter(item => normalizar(item.dia) === normalizar(dia)).forEach(item => {
            const desde = Math.max(inicioDia, minutos(item.horaInicio));
            const hasta = Math.min(finDia, minutos(item.horaFin));
            if (hasta <= desde) return;

            const bloque = document.createElement("article");
            bloque.className = "docente-horario-bloque";
            bloque.style.top = `${((desde - inicioDia) / 60) * altoHora}px`;
            bloque.style.height = `${Math.max(42, ((hasta - desde) / 60) * altoHora - 4)}px`;
            bloque.innerHTML = `<strong></strong><span></span><span></span>`;
            bloque.querySelector("strong").textContent = item.curso || "Curso";
            bloque.querySelectorAll("span")[0].textContent = `${item.horaInicio} - ${item.horaFin}`;
            bloque.querySelectorAll("span")[1].textContent = `Aula: ${item.aula || "Por asignar"}`;
            columna.appendChild(bloque);
        });
        contenedor.appendChild(columna);
    });
});
