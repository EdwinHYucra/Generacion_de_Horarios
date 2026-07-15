document.addEventListener("DOMContentLoaded", () => {
    const sidebar = document.querySelector(".docente-sidebar");
    const boton = document.querySelector(".docente-topbar__menu");
    if (!sidebar || !boton || document.querySelector(".docente-sidebar-backdrop")) return;
    const fondo = document.createElement("button");
    fondo.type = "button"; fondo.className = "docente-sidebar-backdrop";
    fondo.setAttribute("aria-label", "Cerrar menú"); document.body.appendChild(fondo);
    const cerrar = () => { sidebar.classList.remove("is-open"); fondo.classList.remove("is-visible"); boton.setAttribute("aria-expanded", "false"); document.body.classList.remove("docente-menu-open"); };
    const abrir = () => { sidebar.classList.add("is-open"); fondo.classList.add("is-visible"); boton.setAttribute("aria-expanded", "true"); document.body.classList.add("docente-menu-open"); };
    boton.addEventListener("click", () => sidebar.classList.contains("is-open") ? cerrar() : abrir());
    fondo.addEventListener("click", cerrar);
    sidebar.querySelectorAll("a").forEach(enlace => enlace.addEventListener("click", cerrar));
    document.addEventListener("keydown", evento => { if (evento.key === "Escape") cerrar(); });

    // Paneles del header: notificaciones y fotografía de perfil.
    const btnNotificaciones = document.getElementById("docenteBtnNotificaciones");
    const btnPerfil = document.getElementById("docenteBtnPerfil");
    const panelNotificaciones = document.getElementById("docentePanelNotificaciones");
    const panelPerfil = document.getElementById("docentePanelPerfil");
    const cerrarPaneles = () => {
        [panelNotificaciones, panelPerfil].forEach(panel => { if (panel) panel.hidden = true; });
        [btnNotificaciones, btnPerfil].forEach(item => item?.setAttribute("aria-expanded", "false"));
    };
    const alternarPanel = (panel, activador) => {
        const abrirPanel = panel?.hidden;
        cerrarPaneles();
        if (abrirPanel && panel) { panel.hidden = false; activador.setAttribute("aria-expanded", "true"); }
    };
    btnNotificaciones?.addEventListener("click", evento => { evento.stopPropagation(); alternarPanel(panelNotificaciones, btnNotificaciones); });
    btnPerfil?.addEventListener("click", evento => { evento.stopPropagation(); alternarPanel(panelPerfil, btnPerfil); });
    document.querySelectorAll("[data-cerrar-panel]").forEach(botonCerrar => botonCerrar.addEventListener("click", cerrarPaneles));
    document.addEventListener("click", evento => { if (!evento.target.closest(".docente-header-panel") && !evento.target.closest("#docenteBtnNotificaciones") && !evento.target.closest("#docenteBtnPerfil")) cerrarPaneles(); });
    document.addEventListener("keydown", evento => { if (evento.key === "Escape") cerrarPaneles(); });

    const fotoInput = document.getElementById("docenteFotoInput");
    fotoInput?.addEventListener("change", () => {
        const archivo = fotoInput.files?.[0]; if (!archivo) return;
        let preview = document.getElementById("docenteFotoPreview");
        document.getElementById("docenteFotoInicial")?.remove();
        if (!preview) { preview = document.createElement("img"); preview.id = "docenteFotoPreview"; preview.alt = "Vista previa"; fotoInput.closest("form").querySelector(".docente-perfil-foto").prepend(preview); }
        preview.src = URL.createObjectURL(archivo);
    });

    // Modal responsive de soporte técnico del footer.
    const soporteModal = document.getElementById("docenteSoporteModal");
    const abrirSoporte = document.getElementById("docenteAbrirSoporte");
    const cerrarSoporte = () => { if (!soporteModal) return; soporteModal.hidden = true; soporteModal.setAttribute("aria-hidden", "true"); document.body.classList.remove("docente-modal-open"); };
    abrirSoporte?.addEventListener("click", () => { soporteModal.hidden = false; soporteModal.setAttribute("aria-hidden", "false"); document.body.classList.add("docente-modal-open"); });
    soporteModal?.querySelectorAll("[data-cerrar-soporte]").forEach(elemento => elemento.addEventListener("click", cerrarSoporte));
    document.addEventListener("keydown", evento => { if (evento.key === "Escape") cerrarSoporte(); });
});
