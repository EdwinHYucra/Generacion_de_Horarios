-- Limpia datos generados por pruebas del flujo de horarios y validacion admin.
-- Mantiene intactos los datos base del script_bd.sql:
-- usuarios, docentes, cursos, carreras, aulas, sedes, ciclos y evaluaciones historicas.
-- Borra disponibilidad, seleccion de cursos del ciclo activo, propuestas generadas
-- y solicitudes/comentarios de cambio para probar el flujo real desde cero.

USE generador_horario;

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM historial_solicitud_horario
WHERE id_historial >= 0;

DELETE FROM comentario_horario
WHERE id_comentario >= 0;

DELETE FROM horario_generado_detalle
WHERE id_detalle >= 0;

DELETE FROM horario_generado
WHERE id_horario >= 0;

DELETE FROM disponibilidad_docente
WHERE id_disponibilidad >= 0;

DELETE FROM docente_curso
WHERE id_docente_curso >= 0
  AND id_ciclo_academico = (
      SELECT id_ciclo_academico
      FROM ciclos_academicos
      WHERE nombre = '2026-I'
  );

ALTER TABLE comentario_horario AUTO_INCREMENT = 1;
ALTER TABLE historial_solicitud_horario AUTO_INCREMENT = 1;
ALTER TABLE horario_generado_detalle AUTO_INCREMENT = 1;
ALTER TABLE horario_generado AUTO_INCREMENT = 1;
ALTER TABLE disponibilidad_docente AUTO_INCREMENT = 1;
ALTER TABLE docente_curso AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- Verificacion rapida de integridad de datos demo.
SELECT 'solicitudes_pendientes' AS verificacion, COUNT(*) AS total FROM comentario_horario
UNION ALL
SELECT 'horarios_generados', COUNT(*) FROM horario_generado
UNION ALL
SELECT 'disponibilidad_activa', COUNT(*) FROM disponibilidad_docente
UNION ALL
SELECT 'seleccion_ciclo_activo', COUNT(*)
FROM docente_curso dc
WHERE dc.id_ciclo_academico = (
    SELECT id_ciclo_academico
    FROM ciclos_academicos
    WHERE nombre = '2026-I'
);
