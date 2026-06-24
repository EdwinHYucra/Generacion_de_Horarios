-- ============================================
-- Sistema Generador de Horarios
-- Script de base de datos - Módulo SuperAdmin
-- Versión: 1.0
-- ============================================

CREATE DATABASE IF NOT EXISTS generador_horario
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE generador_horario;

CREATE TABLE usuario (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre          VARCHAR(100)  NOT NULL,
    apellido        VARCHAR(100)  NOT NULL,
    email           VARCHAR(150)  NOT NULL UNIQUE,
    password        VARCHAR(255)  NOT NULL,
    rol             ENUM('SUPERADMIN','ADMIN','DOCENTE') NOT NULL,
    estado          ENUM('ACTIVO','INACTIVO') DEFAULT 'ACTIVO',
    creado_en       DATETIME      DEFAULT CURRENT_TIMESTAMP,
    actualizado_en  DATETIME      ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE super_admin (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id  BIGINT NOT NULL UNIQUE,
    CONSTRAINT fk_superadmin_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE admin (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id  BIGINT NOT NULL UNIQUE,
    creado_por  BIGINT NOT NULL,
    CONSTRAINT fk_admin_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT fk_admin_creador
        FOREIGN KEY (creado_por) REFERENCES super_admin(id)
);

CREATE TABLE credencial_acceso (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo      VARCHAR(50)  NOT NULL UNIQUE,
    usado       BOOLEAN      DEFAULT FALSE,
    rol_destino ENUM('ADMIN','DOCENTE') NOT NULL,
    creado_por  BIGINT       NOT NULL,
    creado_en   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    expira_en   DATETIME,
    CONSTRAINT fk_credencial_superadmin
        FOREIGN KEY (creado_por) REFERENCES super_admin(id)
);

-- ============================================
-- Modulos de gestion academica portados desde la copia
-- Persistencia JDBC/DAO, sin JPA/Hibernate
-- ============================================

CREATE TABLE sedes (
    id_sede    BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo     VARCHAR(30)  NOT NULL UNIQUE,
    nombre     VARCHAR(120) NOT NULL UNIQUE,
    direccion  VARCHAR(200),
    estado     BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE carreras (
    id_carrera BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo     VARCHAR(20)  NOT NULL UNIQUE,
    nombre     VARCHAR(120) NOT NULL UNIQUE,
    estado     BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE cursos (
    id_curso         BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo           VARCHAR(30)  NOT NULL UNIQUE,
    nombre           VARCHAR(150) NOT NULL UNIQUE,
    horas_semanales  INT          NOT NULL,
    tipo             VARCHAR(30)  NOT NULL,
    estado           BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE docentes (
    id_docente       BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id       BIGINT       NOT NULL UNIQUE,
    codigo           VARCHAR(20)  NOT NULL UNIQUE,
    nombres          VARCHAR(80)  NOT NULL,
    apellidos        VARCHAR(80)  NOT NULL,
    dni              VARCHAR(8)   NOT NULL UNIQUE,
    correo           VARCHAR(120) NOT NULL UNIQUE,
    celular          VARCHAR(15),
    especialidad     VARCHAR(100) NOT NULL,
    carrera          VARCHAR(100),
    grado_academico  VARCHAR(80),
    tipo_contrato    VARCHAR(80),
    observaciones    VARCHAR(300),
    estado           BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_docentes_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE aulas (
    id_aula    BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo     VARCHAR(30)  NOT NULL UNIQUE,
    nombre     VARCHAR(120) NOT NULL,
    tipo       VARCHAR(30)  NOT NULL,
    capacidad  INT          NOT NULL,
    ubicacion  VARCHAR(150),
    id_sede    BIGINT       NOT NULL,
    estado     BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_aulas_sedes
        FOREIGN KEY (id_sede) REFERENCES sedes(id_sede)
);

CREATE TABLE carrera_curso (
    id_carrera_curso BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_carrera       BIGINT  NOT NULL,
    id_curso         BIGINT  NOT NULL,
    ciclo            INT     NOT NULL,
    estado           BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_carrera_curso
        UNIQUE (id_carrera, id_curso),
    CONSTRAINT fk_carrera_curso_carrera
        FOREIGN KEY (id_carrera) REFERENCES carreras(id_carrera),
    CONSTRAINT fk_carrera_curso_curso
        FOREIGN KEY (id_curso) REFERENCES cursos(id_curso)
);

CREATE TABLE ciclos_academicos (
    id_ciclo_academico BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre             VARCHAR(30) NOT NULL UNIQUE,
    fecha_inicio       DATE        NOT NULL,
    fecha_fin          DATE        NOT NULL,
    activo             BOOLEAN     NOT NULL DEFAULT FALSE,
    creado_en          DATETIME    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_ciclo_fechas
        CHECK (fecha_inicio < fecha_fin)
);

-----------  DISPONIBILDAD DEL DOCENTE    -------------
CREATE TABLE disponibilidad_docente (
    id_disponibilidad BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_ciclo_academico BIGINT NOT NULL,
    id_docente BIGINT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_disponibilidad_docente_bloque
        UNIQUE (id_ciclo_academico, id_docente, dia_semana, hora_inicio, hora_fin),
    CONSTRAINT chk_disponibilidad_horas
        CHECK (hora_inicio < hora_fin),
    CONSTRAINT fk_disponibilidad_ciclo
        FOREIGN KEY (id_ciclo_academico) REFERENCES ciclos_academicos(id_ciclo_academico),
    CONSTRAINT fk_disponibilidad_docente
        FOREIGN KEY (id_docente) REFERENCES docentes(id_docente)
);

CREATE TABLE docente_curso (
    id_docente_curso BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_ciclo_academico BIGINT NOT NULL,
    id_docente BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_docente_curso_ciclo
        FOREIGN KEY (id_ciclo_academico) REFERENCES ciclos_academicos(id_ciclo_academico),
    CONSTRAINT fk_docente_curso_docente
        FOREIGN KEY (id_docente) REFERENCES docentes(id_docente),
    CONSTRAINT fk_docente_curso_curso
        FOREIGN KEY (id_curso) REFERENCES cursos(id_curso),
    CONSTRAINT uq_docente_curso UNIQUE (id_ciclo_academico, id_docente, id_curso)
);

CREATE TABLE evaluacion_docente (
    id_evaluacion BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_ciclo_academico BIGINT NOT NULL,
    id_docente BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,
    puntaje TINYINT NOT NULL,
    categoria ENUM('MALO','NEUTRAL','POSITIVO') NOT NULL,
    comentario VARCHAR(500),
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_evaluacion_puntaje
        CHECK (puntaje BETWEEN 1 AND 20),
    CONSTRAINT fk_evaluacion_ciclo
        FOREIGN KEY (id_ciclo_academico) REFERENCES ciclos_academicos(id_ciclo_academico),
    CONSTRAINT fk_evaluacion_docente
        FOREIGN KEY (id_docente) REFERENCES docentes(id_docente),
    CONSTRAINT fk_evaluacion_curso
        FOREIGN KEY (id_curso) REFERENCES cursos(id_curso)
);

-- ============================================
-- HORARIOS GENERADOS
-- ============================================

CREATE TABLE horario_generado (
    id_horario BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_docente BIGINT NOT NULL,
    opcion INT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_generacion DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_horario_docente
        FOREIGN KEY (id_docente)
        REFERENCES docentes(id_docente)
);
-- ============================================
-- DETALLE DEL HORARIO
-- ============================================

CREATE TABLE horario_generado_detalle (
    id_detalle BIGINT PRIMARY KEY AUTO_INCREMENT,

    id_horario BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,
    id_aula BIGINT NOT NULL,

    dia_semana VARCHAR(20) NOT NULL,

    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,

    CONSTRAINT fk_detalle_horario
        FOREIGN KEY (id_horario)
        REFERENCES horario_generado(id_horario),

    CONSTRAINT fk_detalle_curso
        FOREIGN KEY (id_curso)
        REFERENCES cursos(id_curso),

    CONSTRAINT fk_detalle_aula
        FOREIGN KEY (id_aula)
        REFERENCES aulas(id_aula)
);
-- ============================================
-- COMENTARIOS DEL DOCENTE
-- ============================================

CREATE TABLE comentario_horario (
    id_comentario BIGINT PRIMARY KEY AUTO_INCREMENT,

    id_horario BIGINT NOT NULL,
    id_docente BIGINT NOT NULL,

    comentario VARCHAR(500) NOT NULL,

    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comentario_horario
        FOREIGN KEY (id_horario)
        REFERENCES horario_generado(id_horario),

    CONSTRAINT fk_comentario_docente
        FOREIGN KEY (id_docente)
        REFERENCES docentes(id_docente)
);

CREATE TABLE restriccion_sede (
    id_restriccion BIGINT PRIMARY KEY AUTO_INCREMENT,

    sede_origen BIGINT NOT NULL,
    sede_destino BIGINT NOT NULL,

    tiempo_minimo_minutos INT NOT NULL,

    CONSTRAINT uq_restriccion_sede
        UNIQUE (sede_origen, sede_destino),
    CONSTRAINT chk_restriccion_sede_diferente
        CHECK (sede_origen <> sede_destino),
    CONSTRAINT chk_restriccion_tiempo
        CHECK (tiempo_minimo_minutos >= 0),
    CONSTRAINT fk_restriccion_sede_origen
        FOREIGN KEY (sede_origen) REFERENCES sedes(id_sede),
    CONSTRAINT fk_restriccion_sede_destino
        FOREIGN KEY (sede_destino) REFERENCES sedes(id_sede)
);

CREATE TABLE historial_calificacion_docente (
    id_historial BIGINT PRIMARY KEY AUTO_INCREMENT,

    id_docente BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,

    periodo VARCHAR(20) NOT NULL,

    promedio DECIMAL(4,2) NOT NULL,

    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_hist_docente
        FOREIGN KEY(id_docente)
        REFERENCES docentes(id_docente),

    CONSTRAINT fk_hist_curso
        FOREIGN KEY(id_curso)
        REFERENCES cursos(id_curso)
);

CREATE TABLE preferencia_curso_docente (
    id_preferencia BIGINT PRIMARY KEY AUTO_INCREMENT,

    id_docente BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,

    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pref_docente
        FOREIGN KEY(id_docente)
        REFERENCES docentes(id_docente),

    CONSTRAINT fk_pref_curso
        FOREIGN KEY(id_curso)
        REFERENCES cursos(id_curso),

    CONSTRAINT uq_pref_docente_curso
        UNIQUE(id_docente,id_curso)
);


-- SuperAdmin inicial
-- Email: superadmin@sistema.com
-- Password: Admin1234
INSERT INTO usuario (nombre, apellido, email, password, rol, estado)
VALUES (
    'Super',
    'Admin',
    'superadmin@sistema.com',
    '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe',
    'SUPERADMIN',
    'ACTIVO'
);

INSERT INTO super_admin (usuario_id)
VALUES (LAST_INSERT_ID());

SET @super_admin_id = LAST_INSERT_ID();

-- Administrador inicial
-- Email: admin@sistema.com
-- Password: Admin1234
INSERT INTO usuario (nombre, apellido, email, password, rol, estado)
VALUES (
    'Administrador',
    'Sistema',
    'admin@sistema.com',
    '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe',
    'ADMIN',
    'ACTIVO'
);

INSERT INTO admin (usuario_id, creado_por)
VALUES (LAST_INSERT_ID(), @super_admin_id);

-- Ciclos academicos iniciales
INSERT INTO ciclos_academicos (nombre, fecha_inicio, fecha_fin, activo)
VALUES
    ('2025-II', '2025-08-01', '2025-12-20', FALSE),
    ('2026-I', '2026-03-01', '2026-07-31', TRUE);

-- Sedes reales, aulas y reglas base para validar traslados del algoritmo
INSERT INTO sedes (codigo, nombre, direccion, estado)
VALUES
    ('TACNA-ARICA', 'Tacna y Arica', 'Av. Tacna y Arica', TRUE),
    ('PARRA-1', 'Parra 1', 'Av. Parra 1', TRUE),
    ('PARRA-2', 'Parra 2', 'Av. Parra 2', TRUE);

INSERT INTO aulas (codigo, nombre, tipo, capacidad, ubicacion, id_sede, estado)
VALUES
    ('TA-101', 'Aula 101', 'TEORICA', 40, 'Piso 1', (SELECT id_sede FROM sedes WHERE codigo = 'TACNA-ARICA'), TRUE),
    ('TA-LAB1', 'Laboratorio 1', 'LABORATORIO', 28, 'Piso 2', (SELECT id_sede FROM sedes WHERE codigo = 'TACNA-ARICA'), TRUE),
    ('P1-201', 'Aula 201', 'TEORICA', 45, 'Piso 2', (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-1'), TRUE),
    ('P1-LAB2', 'Laboratorio 2', 'LABORATORIO', 30, 'Piso 3', (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-1'), TRUE),
    ('P2-301', 'Aula 301', 'TEORICA', 35, 'Piso 3', (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-2'), TRUE),
    ('P2-LAB3', 'Laboratorio 3', 'LABORATORIO', 26, 'Piso 4', (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-2'), TRUE);

INSERT INTO restriccion_sede (sede_origen, sede_destino, tiempo_minimo_minutos)
VALUES
    ((SELECT id_sede FROM sedes WHERE codigo = 'TACNA-ARICA'), (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-1'), 15),
    ((SELECT id_sede FROM sedes WHERE codigo = 'PARRA-1'), (SELECT id_sede FROM sedes WHERE codigo = 'TACNA-ARICA'), 15),
    ((SELECT id_sede FROM sedes WHERE codigo = 'TACNA-ARICA'), (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-2'), 20),
    ((SELECT id_sede FROM sedes WHERE codigo = 'PARRA-2'), (SELECT id_sede FROM sedes WHERE codigo = 'TACNA-ARICA'), 20),
    ((SELECT id_sede FROM sedes WHERE codigo = 'PARRA-1'), (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-2'), 10),
    ((SELECT id_sede FROM sedes WHERE codigo = 'PARRA-2'), (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-1'), 10);


-- ============================================
--              DOCENTE
--              MODULOS
-- ============================================

-- Correo: percy@utp.edu.pe
-- Contraseña: Admin1234
INSERT INTO usuario (nombre, apellido, email, password, rol, estado)
VALUES (
    'Percy',
    'Maldonado',
    'percy@utp.edu.pe',
    '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe',
    'DOCENTE',
    'ACTIVO'
);

INSERT INTO usuario (nombre, apellido, email, password, rol, estado)
VALUES
    ('Ana', 'Torres', 'ana.torres@utp.edu.pe', '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe', 'DOCENTE', 'ACTIVO'),
    ('Luis', 'Ramos', 'luis.ramos@utp.edu.pe', '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe', 'DOCENTE', 'ACTIVO'),
    ('Maria', 'Quispe', 'maria.quispe@utp.edu.pe', '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe', 'DOCENTE', 'ACTIVO');

INSERT INTO docentes (
    usuario_id, codigo, nombres, apellidos, dni, correo, celular,
    especialidad, carrera, grado_academico, tipo_contrato, observaciones, estado
)
VALUES (
    (SELECT id FROM usuario WHERE email = 'percy@utp.edu.pe'),
    'DOC001',
    'Percy',
    'Maldonado',
    '00000000',
    'percy@utp.edu.pe',
    '999999999',
    'Ingeniería de Sistemas',
    'Ingeniería de Sistemas',
    'Magíster',
    'Tiempo parcial',
    '',
    TRUE
);

UPDATE docentes
SET dni = '00000001',
    celular = '999111111',
    observaciones = 'Docente con evaluacion historica mala en Programacion I.'
WHERE correo = 'percy@utp.edu.pe';

INSERT INTO docentes (
    usuario_id, codigo, nombres, apellidos, dni, correo, celular,
    especialidad, carrera, grado_academico, tipo_contrato, observaciones, estado
)
VALUES
    (
        (SELECT id FROM usuario WHERE email = 'ana.torres@utp.edu.pe'),
        'DOC002',
        'Ana',
        'Torres',
        '00000002',
        'ana.torres@utp.edu.pe',
        '999222222',
        'Base de Datos',
        'Ingenieria de Sistemas',
        'Magister',
        'Tiempo completo',
        'Docente con evaluacion historica positiva.',
        TRUE
    ),
    (
        (SELECT id FROM usuario WHERE email = 'luis.ramos@utp.edu.pe'),
        'DOC003',
        'Luis',
        'Ramos',
        '00000003',
        'luis.ramos@utp.edu.pe',
        '999333333',
        'Gestion Empresarial',
        'Administracion de Empresas',
        'Licenciado',
        'Tiempo parcial',
        'Docente con evaluacion historica neutral.',
        TRUE
    ),
    (
        (SELECT id FROM usuario WHERE email = 'maria.quispe@utp.edu.pe'),
        'DOC004',
        'Maria',
        'Quispe',
        '00000004',
        'maria.quispe@utp.edu.pe',
        '999444444',
        'Contabilidad',
        'Administracion de Empresas',
        'Magister',
        'Tiempo completo',
        'Docente nuevo sin evaluacion del ciclo anterior.',
        TRUE
    );

-- Datos academicos minimos para probar evaluacion publica del ciclo anterior
INSERT INTO carreras (codigo, nombre, estado)
VALUES ('IS', 'Ingeniería de Sistemas', TRUE);

INSERT INTO cursos (codigo, nombre, horas_semanales, tipo, estado)
VALUES ('IS101', 'Programación I', 4, 'Carrera', TRUE);

UPDATE carreras
SET nombre = 'Ingenieria de Sistemas'
WHERE codigo = 'IS';

UPDATE cursos
SET nombre = 'Programacion I',
    tipo = 'LABORATORIO'
WHERE codigo = 'IS101';

INSERT INTO carreras (codigo, nombre, estado)
VALUES ('ADM', 'Administracion de Empresas', TRUE);

INSERT INTO cursos (codigo, nombre, horas_semanales, tipo, estado)
VALUES
    ('IS201', 'Base de Datos', 4, 'LABORATORIO', TRUE),
    ('IS102', 'Matematica I', 3, 'TEORICA', TRUE),
    ('ADM101', 'Gestion Empresarial', 3, 'TEORICA', TRUE),
    ('ADM102', 'Contabilidad General', 4, 'TEORICA', TRUE),
    ('ADM201', 'Marketing Digital', 3, 'LABORATORIO', TRUE);

INSERT INTO carrera_curso (id_carrera, id_curso, ciclo, estado)
VALUES (
    (SELECT id_carrera FROM carreras WHERE codigo = 'IS'),
    (SELECT id_curso FROM cursos WHERE codigo = 'IS101'),
    1,
    TRUE
);

INSERT INTO carrera_curso (id_carrera, id_curso, ciclo, estado)
VALUES
    ((SELECT id_carrera FROM carreras WHERE codigo = 'IS'), (SELECT id_curso FROM cursos WHERE codigo = 'IS102'), 1, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'IS'), (SELECT id_curso FROM cursos WHERE codigo = 'IS201'), 2, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM101'), 1, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM102'), 1, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM201'), 2, TRUE);

INSERT INTO docente_curso (id_ciclo_academico, id_docente, id_curso)
VALUES (
    (SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'),
    (SELECT id_docente FROM docentes WHERE correo = 'percy@utp.edu.pe'),
    (SELECT id_curso FROM cursos WHERE codigo = 'IS101')
);

INSERT INTO docente_curso (id_ciclo_academico, id_docente, id_curso)
VALUES (
    (SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'),
    (SELECT id_docente FROM docentes WHERE correo = 'percy@utp.edu.pe'),
    (SELECT id_curso FROM cursos WHERE codigo = 'IS101')
);

-- Historial adicional del ciclo anterior: base para evaluacion publica
INSERT INTO docente_curso (id_ciclo_academico, id_docente, id_curso)
VALUES
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'ana.torres@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS201')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'luis.ramos@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM101'));

-- Cursos seleccionados para el ciclo activo: insumo de generacion de horarios
INSERT INTO docente_curso (id_ciclo_academico, id_docente, id_curso)
VALUES
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'percy@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS102')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'ana.torres@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS201')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'luis.ramos@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM101')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'maria.quispe@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM102')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'maria.quispe@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM201'));

-- Disponibilidad del ciclo activo. Dos docentes pueden tener el mismo bloque sin conflicto.
INSERT INTO disponibilidad_docente (id_ciclo_academico, id_docente, dia_semana, hora_inicio, hora_fin, estado)
VALUES
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'percy@utp.edu.pe'), 'LUNES', '07:00:00', '08:30:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'percy@utp.edu.pe'), 'MIERCOLES', '09:00:00', '11:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'ana.torres@utp.edu.pe'), 'LUNES', '07:00:00', '08:30:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'ana.torres@utp.edu.pe'), 'MARTES', '10:00:00', '12:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'luis.ramos@utp.edu.pe'), 'MARTES', '08:00:00', '11:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'maria.quispe@utp.edu.pe'), 'JUEVES', '07:00:00', '10:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'maria.quispe@utp.edu.pe'), 'VIERNES', '14:00:00', '17:00:00', TRUE);

-- Evaluaciones del ciclo anterior para probar restricciones durante la generacion
INSERT INTO evaluacion_docente (id_ciclo_academico, id_docente, id_curso, puntaje, categoria, comentario)
VALUES
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'percy@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS101'), 14, 'MALO', 'Debe reforzar acompanamiento.'),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'ana.torres@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS201'), 19, 'POSITIVO', 'Buen dominio del laboratorio.'),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'luis.ramos@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM101'), 17, 'NEUTRAL', 'Desempeno regular.');

-- ============================================
-- Datos ampliados para pruebas integrales
-- ============================================

-- Aulas adicionales para probar conflictos y disponibilidad de ambientes
INSERT INTO aulas (codigo, nombre, tipo, capacidad, ubicacion, id_sede, estado)
VALUES
    ('TA-102', 'Aula 102', 'TEORICA', 42, 'Piso 1', (SELECT id_sede FROM sedes WHERE codigo = 'TACNA-ARICA'), TRUE),
    ('TA-LAB4', 'Laboratorio 4', 'LABORATORIO', 24, 'Piso 3', (SELECT id_sede FROM sedes WHERE codigo = 'TACNA-ARICA'), TRUE),
    ('P1-202', 'Aula 202', 'TEORICA', 38, 'Piso 2', (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-1'), TRUE),
    ('P2-302', 'Aula 302', 'TEORICA', 36, 'Piso 3', (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-2'), TRUE),
    ('P2-LAB5', 'Laboratorio 5', 'LABORATORIO', 25, 'Piso 5', (SELECT id_sede FROM sedes WHERE codigo = 'PARRA-2'), TRUE);

-- Docentes adicionales. Password de prueba: Admin1234
INSERT INTO usuario (nombre, apellido, email, password, rol, estado)
VALUES
    ('Jorge', 'Salazar', 'jorge.salazar@utp.edu.pe', '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe', 'DOCENTE', 'ACTIVO'),
    ('Carla', 'Medina', 'carla.medina@utp.edu.pe', '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe', 'DOCENTE', 'ACTIVO'),
    ('Rosa', 'Flores', 'rosa.flores@utp.edu.pe', '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe', 'DOCENTE', 'ACTIVO'),
    ('Diego', 'Vega', 'diego.vega@utp.edu.pe', '$2a$10$cxkso4pdNGypVJCtLNHlp.vpQBHCd1eCubTng5lIFZChMUC6.2JHe', 'DOCENTE', 'ACTIVO');

INSERT INTO docentes (
    usuario_id, codigo, nombres, apellidos, dni, correo, celular,
    especialidad, carrera, grado_academico, tipo_contrato, observaciones, estado
)
VALUES
    (
        (SELECT id FROM usuario WHERE email = 'jorge.salazar@utp.edu.pe'),
        'DOC005', 'Jorge', 'Salazar', '00000005', 'jorge.salazar@utp.edu.pe', '999555555',
        'Redes y Comunicaciones', 'Ingenieria de Sistemas', 'Magister', 'Tiempo parcial',
        'Docente con evaluacion historica mala en Redes.', TRUE
    ),
    (
        (SELECT id FROM usuario WHERE email = 'carla.medina@utp.edu.pe'),
        'DOC006', 'Carla', 'Medina', '00000006', 'carla.medina@utp.edu.pe', '999666666',
        'Matematica y Estadistica', 'Ingenieria de Sistemas', 'Magister', 'Tiempo completo',
        'Docente con cursos generales compartidos.', TRUE
    ),
    (
        (SELECT id FROM usuario WHERE email = 'rosa.flores@utp.edu.pe'),
        'DOC007', 'Rosa', 'Flores', '00000007', 'rosa.flores@utp.edu.pe', '999777777',
        'Finanzas', 'Administracion de Empresas', 'Magister', 'Tiempo completo',
        'Docente con evaluacion positiva en Finanzas.', TRUE
    ),
    (
        (SELECT id FROM usuario WHERE email = 'diego.vega@utp.edu.pe'),
        'DOC008', 'Diego', 'Vega', '00000008', 'diego.vega@utp.edu.pe', '999888888',
        'Marketing y Transformacion Digital', 'Administracion de Empresas', 'Licenciado', 'Tiempo parcial',
        'Docente nuevo para probar generacion sin historial.', TRUE
    );

-- Cursos adicionales de carrera y cursos generales compartidos
INSERT INTO cursos (codigo, nombre, horas_semanales, tipo, estado)
VALUES
    ('IS202', 'Algoritmos y Estructuras de Datos', 4, 'LABORATORIO', TRUE),
    ('IS301', 'Redes y Comunicaciones', 4, 'LABORATORIO', TRUE),
    ('IS302', 'Arquitectura de Computadoras', 3, 'TEORICA', TRUE),
    ('IS303', 'Ingenieria de Software', 4, 'LABORATORIO', TRUE),
    ('ADM202', 'Costos y Presupuestos', 3, 'TEORICA', TRUE),
    ('ADM301', 'Finanzas Empresariales', 4, 'TEORICA', TRUE),
    ('ADM302', 'Gestion del Talento Humano', 3, 'TEORICA', TRUE),
    ('ADM303', 'Analitica para Negocios', 4, 'LABORATORIO', TRUE),
    ('GEN101', 'Comunicacion Efectiva', 3, 'TEORICA', TRUE),
    ('GEN102', 'Metodologia Universitaria', 3, 'TEORICA', TRUE);

INSERT INTO carrera_curso (id_carrera, id_curso, ciclo, estado)
VALUES
    ((SELECT id_carrera FROM carreras WHERE codigo = 'IS'), (SELECT id_curso FROM cursos WHERE codigo = 'IS202'), 2, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'IS'), (SELECT id_curso FROM cursos WHERE codigo = 'IS301'), 3, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'IS'), (SELECT id_curso FROM cursos WHERE codigo = 'IS302'), 3, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'IS'), (SELECT id_curso FROM cursos WHERE codigo = 'IS303'), 4, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM202'), 2, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM301'), 3, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM302'), 3, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM303'), 4, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'IS'), (SELECT id_curso FROM cursos WHERE codigo = 'GEN101'), 1, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'GEN101'), 1, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'IS'), (SELECT id_curso FROM cursos WHERE codigo = 'GEN102'), 1, TRUE),
    ((SELECT id_carrera FROM carreras WHERE codigo = 'ADM'), (SELECT id_curso FROM cursos WHERE codigo = 'GEN102'), 1, TRUE);

-- Historial de cursos dictados en ciclo anterior para evaluacion publica
INSERT INTO docente_curso (id_ciclo_academico, id_docente, id_curso)
VALUES
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'jorge.salazar@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS301')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'carla.medina@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'GEN101')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'rosa.flores@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM301')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'diego.vega@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM201'));

-- Cursos activos seleccionados por docentes para generar horarios
INSERT INTO docente_curso (id_ciclo_academico, id_docente, id_curso)
VALUES
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'percy@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS202')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'ana.torres@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'GEN102')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'luis.ramos@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM202')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'jorge.salazar@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS301')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'jorge.salazar@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS302')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'carla.medina@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'GEN101')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'carla.medina@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS102')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'rosa.flores@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM301')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'rosa.flores@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM302')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'diego.vega@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM201')),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'diego.vega@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM303'));

-- Disponibilidad ampliada del ciclo activo
INSERT INTO disponibilidad_docente (id_ciclo_academico, id_docente, dia_semana, hora_inicio, hora_fin, estado)
VALUES
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'percy@utp.edu.pe'), 'LUNES', '09:00:00', '12:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'ana.torres@utp.edu.pe'), 'MIERCOLES', '07:00:00', '11:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'luis.ramos@utp.edu.pe'), 'JUEVES', '14:00:00', '17:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'jorge.salazar@utp.edu.pe'), 'LUNES', '07:00:00', '11:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'jorge.salazar@utp.edu.pe'), 'MARTES', '15:00:00', '18:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'carla.medina@utp.edu.pe'), 'LUNES', '07:00:00', '08:30:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'carla.medina@utp.edu.pe'), 'MIERCOLES', '10:00:00', '13:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'rosa.flores@utp.edu.pe'), 'MARTES', '07:00:00', '10:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'rosa.flores@utp.edu.pe'), 'JUEVES', '10:00:00', '13:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'diego.vega@utp.edu.pe'), 'VIERNES', '08:00:00', '12:00:00', TRUE),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2026-I'), (SELECT id_docente FROM docentes WHERE correo = 'diego.vega@utp.edu.pe'), 'MIERCOLES', '09:00:00', '12:00:00', TRUE);

-- Evaluaciones ampliadas para forzar y comparar restricciones
INSERT INTO evaluacion_docente (id_ciclo_academico, id_docente, id_curso, puntaje, categoria, comentario)
VALUES
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'jorge.salazar@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'IS301'), 13, 'MALO', 'Presento reclamos recurrentes en laboratorios.'),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'carla.medina@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'GEN101'), 18, 'NEUTRAL', 'Cumple, pero debe mejorar retroalimentacion.'),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'rosa.flores@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM301'), 20, 'POSITIVO', 'Excelente claridad y puntualidad.'),
    ((SELECT id_ciclo_academico FROM ciclos_academicos WHERE nombre = '2025-II'), (SELECT id_docente FROM docentes WHERE correo = 'diego.vega@utp.edu.pe'), (SELECT id_curso FROM cursos WHERE codigo = 'ADM201'), 16, 'NEUTRAL', 'Desempeno aceptable.');







