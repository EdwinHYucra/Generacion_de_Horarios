document.addEventListener("DOMContentLoaded", () => {
    const chipsCursosCarrera = document.querySelectorAll(".carreras-cursos-resumen>b[data-total]");
    const cerrarCursosCarrera = () => chipsCursosCarrera.forEach(chip => {
        chip.classList.remove("is-open");
        chip.setAttribute("aria-expanded", "false");
    });
    chipsCursosCarrera.forEach(chip => {
        chip.textContent = `+${Math.max(0, Number(chip.dataset.total) - 1)} más`;
        chip.setAttribute("role", "button");
        chip.setAttribute("aria-expanded", "false");
        chip.addEventListener("click", event => {
            event.stopPropagation();
            const abrir = !chip.classList.contains("is-open");
            cerrarCursosCarrera();
            chip.classList.toggle("is-open", abrir);
            chip.setAttribute("aria-expanded", String(abrir));
        });
    });
    document.addEventListener("click", cerrarCursosCarrera);
    document.addEventListener("keydown", event => { if (event.key === "Escape") cerrarCursosCarrera(); });
    const sidebar = document.querySelector(".sidebar");
    const menu = document.querySelector(".topbar__menu");
    let backdrop = document.querySelector(".sidebar-backdrop");

    // Menú hamburguesa común para todas las vistas administrativas.
    if (sidebar && menu) {
        if (!backdrop) {
            backdrop = document.createElement("button");
            backdrop.type = "button";
            backdrop.className = "sidebar-backdrop";
            backdrop.setAttribute("aria-label", "Cerrar menú");
            document.body.appendChild(backdrop);
        }
        const cerrar = () => {
            sidebar.classList.remove("sidebar--open");
            backdrop.classList.remove("sidebar-backdrop--show");
            menu.setAttribute("aria-expanded", "false");
            document.body.classList.remove("menu-mobile-open");
        };
        const alternar = () => {
            const abrir = !sidebar.classList.contains("sidebar--open");
            sidebar.classList.toggle("sidebar--open", abrir);
            backdrop.classList.toggle("sidebar-backdrop--show", abrir);
            menu.setAttribute("aria-expanded", String(abrir));
            document.body.classList.toggle("menu-mobile-open", abrir);
        };
        menu.addEventListener("click", alternar);
        backdrop.addEventListener("click", cerrar);
        sidebar.querySelectorAll("a").forEach(enlace => enlace.addEventListener("click", cerrar));
        window.addEventListener("resize", () => { if (window.innerWidth > 900) cerrar(); });
        document.addEventListener("keydown", e => { if (e.key === "Escape") cerrar(); });
    }

    // En móvil cada celda muestra el nombre de su columna y funciona como tarjeta.
    document.querySelectorAll("table").forEach(tabla => {
        tabla.classList.add("admin-mobile-ready");
        const titulos = Array.from(tabla.querySelectorAll("thead th")).map(th => th.textContent.trim());
        tabla.querySelectorAll("tbody tr").forEach(fila => {
            Array.from(fila.children).forEach((celda, indice) => celda.dataset.label = titulos[indice] || "Detalle");
        });
    });

    // Gestión de docentes: resumen compacto y detalles desplegables en teléfono.
    document.querySelectorAll(".docente-row").forEach((fila, indice) => {
        const celdaNombre = fila.querySelector("td:first-child");
        if (!celdaNombre) return;
        const boton = document.createElement("button");
        boton.type = "button";
        boton.className = "docente-acordeon-toggle";
        boton.setAttribute("aria-label", "Mostrar datos del docente");
        boton.setAttribute("aria-expanded", "false");
        boton.setAttribute("aria-controls", `docente-detalle-${indice}`);
        boton.innerHTML = "<span></span>";
        fila.id = `docente-detalle-${indice}`;
        celdaNombre.appendChild(boton);
        boton.addEventListener("click", () => {
            const abierto = fila.classList.toggle("docente-row--abierta");
            boton.setAttribute("aria-expanded", String(abierto));
            boton.setAttribute("aria-label", abierto ? "Ocultar datos del docente" : "Mostrar datos del docente");
        });
    });

    // Gestión de cursos: nombre y código visibles; el resto se despliega.
    document.querySelectorAll(".curso-row").forEach((fila, indice) => {
        const celdaResumen = fila.querySelector("td:first-child");
        if (!celdaResumen) return;
        const nombre = document.createElement("span");
        nombre.className = "curso-resumen-nombre";
        nombre.textContent = fila.dataset.nombre || "Curso";
        celdaResumen.prepend(nombre);
        const boton = document.createElement("button");
        boton.type = "button";
        boton.className = "curso-acordeon-toggle";
        boton.setAttribute("aria-label", "Mostrar datos del curso");
        boton.setAttribute("aria-expanded", "false");
        boton.setAttribute("aria-controls", `curso-detalle-${indice}`);
        boton.innerHTML = "<span></span>";
        fila.id = `curso-detalle-${indice}`;
        celdaResumen.appendChild(boton);
        boton.addEventListener("click", () => {
            const abierto = fila.classList.toggle("curso-row--abierta");
            boton.setAttribute("aria-expanded", String(abierto));
            boton.setAttribute("aria-label", abierto ? "Ocultar datos del curso" : "Mostrar datos del curso");
        });
    });

    // Acordeones administrativos: resumen arriba y datos completos al desplegar.
    const acordeones = [
        { selector: ".carrera-row", titulo: fila => fila.dataset.nombre, subtitulo: fila => fila.dataset.codigo },
        { selector: ".asignacion-row", titulo: fila => fila.dataset.curso, subtitulo: fila => fila.dataset.carrera },
        { selector: ".sede-row", titulo: fila => fila.dataset.nombre, subtitulo: fila => fila.dataset.codigo },
        { selector: ".aula-row", titulo: fila => fila.dataset.codigo, subtitulo: fila => fila.dataset.tipo },
        { selector: ".validacion-row", titulo: fila => fila.dataset.docente, subtitulo: fila => fila.dataset.estado },
        { selector: ".solicitud-admin-row", titulo: fila => fila.dataset.docente, subtitulo: fila => `${fila.dataset.tipo || "Solicitud"} · ${fila.dataset.estado || ""}` }
    ];
    acordeones.forEach(configuracion => {
        document.querySelectorAll(configuracion.selector).forEach((fila, indice) => {
            const primeraCelda = fila.querySelector("td:first-child");
            if (!primeraCelda) return;
            fila.classList.add("admin-acordeon-row");
            // Conserva el contenido original para escritorio, pero permite ocultar
            // también nodos de texto directos (por ejemplo, el código IND).
            const contenidoOriginal = document.createElement("span");
            contenidoOriginal.className = "admin-acordeon-original";
            while (primeraCelda.firstChild) contenidoOriginal.appendChild(primeraCelda.firstChild);
            primeraCelda.appendChild(contenidoOriginal);
            const resumen = document.createElement("div");
            resumen.className = "admin-acordeon-resumen";
            const titulo = document.createElement("strong");
            titulo.textContent = configuracion.titulo(fila) || "Registro";
            const subtitulo = document.createElement("small");
            subtitulo.textContent = configuracion.subtitulo(fila) || "";
            resumen.append(titulo, subtitulo);
            const boton = document.createElement("button");
            boton.type = "button";
            boton.className = "admin-acordeon-toggle";
            boton.setAttribute("aria-label", "Mostrar detalles");
            boton.setAttribute("aria-expanded", "false");
            boton.innerHTML = "<span></span>";
            primeraCelda.append(resumen, boton);
            boton.addEventListener("click", () => {
                const abierto = fila.classList.toggle("admin-acordeon-row--abierta");
                boton.setAttribute("aria-expanded", String(abierto));
                boton.setAttribute("aria-label", abierto ? "Ocultar detalles" : "Mostrar detalles");
            });
        });
    });

    const notificaciones = document.getElementById("adminPanelNotificaciones");
    const perfil = document.getElementById("adminPanelPerfil");
    const botonNotificaciones = document.getElementById("adminBtnNotificaciones");
    const botonPerfil = document.getElementById("adminBtnPerfil");
    const cerrarPaneles = () => {
        [notificaciones, perfil].forEach(panel => { if (panel) panel.hidden = true; });
        [botonNotificaciones, botonPerfil].forEach(boton => boton?.setAttribute("aria-expanded", "false"));
    };
    const alternarPanel = (panel, boton) => {
        const mostrar = panel?.hidden;
        cerrarPaneles();
        if (panel && mostrar) { panel.hidden = false; boton.setAttribute("aria-expanded", "true"); }
    };
    botonNotificaciones?.addEventListener("click", e => { e.stopPropagation(); alternarPanel(notificaciones, botonNotificaciones); });
    botonPerfil?.addEventListener("click", e => { e.stopPropagation(); alternarPanel(perfil, botonPerfil); });
    document.querySelectorAll("[data-admin-cerrar]").forEach(boton => boton.addEventListener("click", cerrarPaneles));
    document.addEventListener("click", e => { if (!e.target.closest(".admin-header-panel") && !e.target.closest(".topbar__right")) cerrarPaneles(); });
    document.addEventListener("keydown", e => { if (e.key === "Escape") cerrarPaneles(); });

    const foto = document.getElementById("adminFotoInput");
    foto?.addEventListener("change", () => {
        const archivo = foto.files?.[0];
        if (!archivo) return;
        const lector = new FileReader();
        lector.onload = () => {
            let preview = document.getElementById("adminFotoPreview");
            document.getElementById("adminFotoInicial")?.remove();
            if (!preview) {
                preview = document.createElement("img"); preview.id = "adminFotoPreview";
                document.querySelector(".admin-perfil-foto")?.prepend(preview);
            }
            preview.src = lector.result;
        };
        lector.readAsDataURL(archivo);
    });
});
