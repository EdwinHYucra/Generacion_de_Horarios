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

CREATE TABLE docente (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id      BIGINT NOT NULL UNIQUE,
    codigo_docente  VARCHAR(20) UNIQUE,
    especialidad    VARCHAR(150),
    CONSTRAINT fk_docente_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
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
    codigo           VARCHAR(20)  NOT NULL,
    nombres          VARCHAR(80)  NOT NULL,
    apellidos        VARCHAR(80)  NOT NULL,
    dni              VARCHAR(8)   NOT NULL,
    correo           VARCHAR(120) NOT NULL,
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

-----------  DISPONIBILDAD DEL DOCENTE    -------------
CREATE TABLE disponibilidad_docente (
    id_disponibilidad BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_docente BIGINT NOT NULL,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_disponibilidad_docente
        FOREIGN KEY (id_docente) REFERENCES docentes(id_docente)
);

CREATE TABLE docente_curso (
    id_docente_curso BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_docente BIGINT NOT NULL,
    id_curso BIGINT NOT NULL,
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_docente_curso_docente
        FOREIGN KEY (id_docente) REFERENCES docentes(id_docente),
    CONSTRAINT fk_docente_curso_curso
        FOREIGN KEY (id_curso) REFERENCES cursos(id_curso),
    CONSTRAINT uq_docente_curso UNIQUE (id_docente, id_curso)
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

    tiempo_minimo_minutos INT NOT NULL
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

INSERT INTO docente (usuario_id, codigo_docente, especialidad)
VALUES (
    (SELECT id FROM usuario WHERE email = 'percy@utp.edu.pe'),
    'DOC001',
    'Ingeniería de Sistemas'
);

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







