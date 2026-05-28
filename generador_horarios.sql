-- ============================================================
--  GENERADOR DE HORARIOS AUTOMÁTICO — UTP
--  Base de datos: MySQL (XAMPP)
--  Curso Integrador I | Grupo 3 | Sección 28636
-- ============================================================

DROP DATABASE IF EXISTS generador_horarios;
CREATE DATABASE generador_horarios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;

USE generador_horarios;

-- ============================================================
--  1. SEMESTRE ACADÉMICO
-- ============================================================
CREATE TABLE semestre (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(20)  NOT NULL UNIQUE,   -- Ej: '2026-1'
    fecha_inicio  DATE         NOT NULL,
    fecha_fin     DATE         NOT NULL,
    estado        ENUM('ACTIVO','CERRADO') NOT NULL DEFAULT 'ACTIVO',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  2. USUARIO (login: Administrador y Docente)
-- ============================================================
CREATE TABLE usuario (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,             -- hash bcrypt
    rol         ENUM('ADMIN','DOCENTE') NOT NULL,
    activo      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  3. DOCENTE
-- ============================================================
CREATE TABLE docente (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  INT          NOT NULL UNIQUE,
    nombres     VARCHAR(100) NOT NULL,
    apellidos   VARCHAR(100) NOT NULL,
    dni         CHAR(8)      NOT NULL UNIQUE,
    email       VARCHAR(120) NOT NULL UNIQUE,
    estado      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_docente_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario(id) ON DELETE RESTRICT
);

-- ============================================================
--  4. CURSO
-- ============================================================
CREATE TABLE curso (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    codigo          VARCHAR(15)  NOT NULL UNIQUE,
    nombre          VARCHAR(120) NOT NULL,
    creditos        TINYINT      NOT NULL,
    horas_teoria    TINYINT      NOT NULL DEFAULT 0,
    horas_practica  TINYINT      NOT NULL DEFAULT 0,
    ciclo           TINYINT,                       -- ciclo sugerido (1-10)
    estado          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  5. AULA
-- ============================================================
CREATE TABLE aula (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(20)  NOT NULL UNIQUE,      -- Ej: 'A-201'
    capacidad   SMALLINT     NOT NULL,
    tipo        ENUM('TEORICA','LABORATORIO','TALLER') NOT NULL,
    piso        TINYINT,
    estado      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
--  6. BLOQUE HORARIO (franjas de tiempo)
-- ============================================================
CREATE TABLE bloque_horario (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(30)  NOT NULL,             -- Ej: 'Bloque 1'
    hora_inicio TIME         NOT NULL,
    hora_fin    TIME         NOT NULL,
    UNIQUE KEY uq_bloque (hora_inicio, hora_fin)
);

-- ============================================================
--  7. DISPONIBILIDAD DEL DOCENTE (CU-04)
-- ============================================================
CREATE TABLE disponibilidad_docente (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    docente_id    INT NOT NULL,
    semestre_id   INT NOT NULL,
    dia_semana    ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO') NOT NULL,
    bloque_id     INT NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_disponibilidad (docente_id, semestre_id, dia_semana, bloque_id),
    CONSTRAINT fk_disp_docente   FOREIGN KEY (docente_id)  REFERENCES docente(id),
    CONSTRAINT fk_disp_semestre  FOREIGN KEY (semestre_id) REFERENCES semestre(id),
    CONSTRAINT fk_disp_bloque    FOREIGN KEY (bloque_id)   REFERENCES bloque_horario(id)
);

-- ============================================================
--  8. DOCENTE_CURSO  (qué cursos puede dictar cada docente)
-- ============================================================
CREATE TABLE docente_curso (
    docente_id  INT NOT NULL,
    curso_id    INT NOT NULL,
    PRIMARY KEY (docente_id, curso_id),
    CONSTRAINT fk_dc_docente FOREIGN KEY (docente_id) REFERENCES docente(id),
    CONSTRAINT fk_dc_curso   FOREIGN KEY (curso_id)   REFERENCES curso(id)
);

-- ============================================================
--  9. HORARIO (cabecera — un horario por semestre) (CU-05)
-- ============================================================
CREATE TABLE horario (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    semestre_id       INT NOT NULL,
    estado            ENUM('BORRADOR','GENERADO','APROBADO') NOT NULL DEFAULT 'BORRADOR',
    fecha_generacion  DATETIME,
    observaciones     TEXT,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_horario_semestre FOREIGN KEY (semestre_id) REFERENCES semestre(id)
);

-- ============================================================
--  10. DETALLE_HORARIO (asignaciones concretas) (CU-05/CU-08)
-- ============================================================
CREATE TABLE detalle_horario (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    horario_id  INT NOT NULL,
    docente_id  INT NOT NULL,
    curso_id    INT NOT NULL,
    aula_id     INT NOT NULL,
    dia_semana  ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO') NOT NULL,
    bloque_id   INT NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Restricción: un docente no puede estar en dos aulas el mismo día/bloque (CU-06)
    UNIQUE KEY uq_docente_turno (horario_id, docente_id, dia_semana, bloque_id),

    -- Restricción: un aula no puede tener dos cursos al mismo tiempo (CU-06)
    UNIQUE KEY uq_aula_turno (horario_id, aula_id, dia_semana, bloque_id),

    CONSTRAINT fk_det_horario FOREIGN KEY (horario_id) REFERENCES horario(id)  ON DELETE CASCADE,
    CONSTRAINT fk_det_docente FOREIGN KEY (docente_id) REFERENCES docente(id),
    CONSTRAINT fk_det_curso   FOREIGN KEY (curso_id)   REFERENCES curso(id),
    CONSTRAINT fk_det_aula    FOREIGN KEY (aula_id)    REFERENCES aula(id),
    CONSTRAINT fk_det_bloque  FOREIGN KEY (bloque_id)  REFERENCES bloque_horario(id)
);

-- ============================================================
--  11. SOLICITUD_CAMBIO (modificaciones manuales — CU-08)
-- ============================================================
CREATE TABLE solicitud_cambio (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    detalle_id      INT NOT NULL,
    docente_id      INT NOT NULL,          -- quien solicita
    motivo          TEXT NOT NULL,
    nuevo_dia       ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO'),
    nuevo_bloque_id INT,
    nuevo_aula_id   INT,
    estado          ENUM('PENDIENTE','APROBADO','RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sol_detalle FOREIGN KEY (detalle_id)      REFERENCES detalle_horario(id),
    CONSTRAINT fk_sol_docente FOREIGN KEY (docente_id)      REFERENCES docente(id),
    CONSTRAINT fk_sol_bloque  FOREIGN KEY (nuevo_bloque_id) REFERENCES bloque_horario(id),
    CONSTRAINT fk_sol_aula    FOREIGN KEY (nuevo_aula_id)   REFERENCES aula(id)
);

-- ============================================================
--  DATOS DE PRUEBA
-- ============================================================

-- Semestre activo
INSERT INTO semestre (nombre, fecha_inicio, fecha_fin, estado) VALUES
('2026-1', '2026-04-07', '2026-07-26', 'ACTIVO');

-- Bloques horarios (UTP estilo)
INSERT INTO bloque_horario (nombre, hora_inicio, hora_fin) VALUES
('Bloque 1', '07:00', '09:00'),
('Bloque 2', '09:00', '11:00'),
('Bloque 3', '11:00', '13:00'),
('Bloque 4', '13:00', '15:00'),
('Bloque 5', '15:00', '17:00'),
('Bloque 6', '17:00', '19:00'),
('Bloque 7', '19:00', '21:00');

-- Aulas
INSERT INTO aula (codigo, capacidad, tipo, piso) VALUES
('A-101', 40, 'TEORICA',     1),
('A-102', 40, 'TEORICA',     1),
('B-201', 35, 'LABORATORIO', 2),
('B-202', 35, 'LABORATORIO', 2),
('C-301', 50, 'TEORICA',     3),
('T-001', 30, 'TALLER',      1);

-- Cursos
INSERT INTO curso (codigo, nombre, creditos, horas_teoria, horas_practica, ciclo) VALUES
('SI101', 'Algoritmos y Programación',       4, 2, 2, 1),
('SI201', 'Estructura de Datos',             4, 2, 2, 2),
('SI301', 'Base de Datos I',                 4, 2, 2, 3),
('SI401', 'Ingeniería de Software I',        4, 2, 2, 4),
('SI501', 'Redes y Comunicaciones',          3, 2, 1, 5),
('SI601', 'Sistemas Operativos',             3, 2, 1, 6);

-- Usuarios
INSERT INTO usuario (username, password, rol) VALUES
('admin',     '$2a$12$placeholder_hash_admin',    'ADMIN'),
('jperez',    '$2a$12$placeholder_hash_docente1', 'DOCENTE'),
('mlopez',    '$2a$12$placeholder_hash_docente2', 'DOCENTE'),
('rgarcia',   '$2a$12$placeholder_hash_docente3', 'DOCENTE');

-- Docentes
INSERT INTO docente (usuario_id, nombres, apellidos, dni, email) VALUES
(2, 'Juan',   'Pérez Torres',  '12345678', 'jperez@utp.edu.pe'),
(3, 'María',  'López Ramos',   '23456789', 'mlopez@utp.edu.pe'),
(4, 'Roberto','García Huanca', '34567890', 'rgarcia@utp.edu.pe');

-- Asignación docente-curso
INSERT INTO docente_curso (docente_id, curso_id) VALUES
(1, 1), (1, 2),   -- Juan dicta SI101 y SI201
(2, 3), (2, 4),   -- María dicta SI301 y SI401
(3, 5), (3, 6);   -- Roberto dicta SI501 y SI601

-- Disponibilidad (semestre 1)
INSERT INTO disponibilidad_docente (docente_id, semestre_id, dia_semana, bloque_id) VALUES
-- Juan: Lunes y Miércoles bloques 1-3
(1, 1, 'LUNES',     1), (1, 1, 'LUNES',     2), (1, 1, 'LUNES',     3),
(1, 1, 'MIERCOLES', 1), (1, 1, 'MIERCOLES', 2), (1, 1, 'MIERCOLES', 3),
-- María: Martes y Jueves bloques 2-4
(2, 1, 'MARTES',  2), (2, 1, 'MARTES',  3), (2, 1, 'MARTES',  4),
(2, 1, 'JUEVES',  2), (2, 1, 'JUEVES',  3), (2, 1, 'JUEVES',  4),
-- Roberto: Viernes y Sábado bloques 5-7
(3, 1, 'VIERNES', 5), (3, 1, 'VIERNES', 6), (3, 1, 'VIERNES', 7),
(3, 1, 'SABADO',  5), (3, 1, 'SABADO',  6), (3, 1, 'SABADO',  7);

-- ============================================================
--  FIN DEL SCRIPT
-- ============================================================
