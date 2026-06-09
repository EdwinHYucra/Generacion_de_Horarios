CREATE DATABASE generacion_horarios
CHARACTER SET utf8mb4
COLLATE utf8mb4_spanish_ci;

USE generacion_horarios;

DELETE FROM docentes where id_docente >= 1;

ALTER TABLE docentes AUTO_INCREMENT = 1;

INSERT INTO docentes
(codigo, nombres, apellidos, dni, correo, celular, especialidad, carrera, grado_academico, tipo_contrato, observaciones, estado)
VALUES
('C00626', 'Ricardo', 'Maldonado Quispe', '45678912', 'C00626@utp.edu.pe', '987654321', 'Ingeniería de Sistemas', 'Ingeniería de Sistemas', 'Magíster', 'Tiempo completo', 'Docente con disponibilidad para cursos de programación.', 1),
('C00845', 'Ana Sofia', 'Rojas Vera', '45678913', 'C00845@utp.edu.pe', '987654322', 'Ciencias Básicas', 'Ciencias Básicas', 'Titulado', 'Tiempo parcial', 'Docente asignada a cursos generales.', 1),
('C01023', 'Carlos', 'Mendoza Silva', '45678914', 'C01023@utp.edu.pe', '987654323', 'Arquitectura', 'Arquitectura', 'Magíster', 'Por horas', 'Docente actualmente inactivo.', 0);