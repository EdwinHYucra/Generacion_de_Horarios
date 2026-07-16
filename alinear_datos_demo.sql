-- Alinea datos demo en una base ya creada sin recrearla.
-- Agrega las relaciones faltantes de cursos generales con Ingenieria Industrial
-- y muestra cursos/carreras que pudieran quedar sin relacion.

USE generador_horario;

INSERT INTO carrera_curso (id_carrera, id_curso, ciclo, estado)
SELECT ca.id_carrera, cu.id_curso, 1, TRUE
FROM carreras ca
JOIN cursos cu ON cu.codigo IN ('GEN101', 'GEN102', 'GEN103', 'GEN104', 'GEN105')
WHERE ca.codigo = 'IND'
  AND NOT EXISTS (
      SELECT 1
      FROM carrera_curso cc
      WHERE cc.id_carrera = ca.id_carrera
        AND cc.id_curso = cu.id_curso
  );

-- Cursos activos sin carrera asociada.
SELECT cu.codigo, cu.nombre
FROM cursos cu
LEFT JOIN carrera_curso cc ON cc.id_curso = cu.id_curso AND cc.estado = TRUE
WHERE cu.estado = TRUE
  AND cc.id_carrera IS NULL
ORDER BY cu.codigo;

-- Docentes cuya carrera no existe exactamente en la tabla carreras.
SELECT d.codigo, d.nombres, d.apellidos, d.carrera
FROM docentes d
LEFT JOIN carreras ca ON LOWER(ca.nombre) = LOWER(d.carrera)
WHERE d.estado = TRUE
  AND d.carrera IS NOT NULL
  AND ca.id_carrera IS NULL
ORDER BY d.codigo;
