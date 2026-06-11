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