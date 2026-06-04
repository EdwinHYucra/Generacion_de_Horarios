CREATE DATABASE generador_horarios;

SELECT * FROM generador_horarios.curso;

INSERT INTO usuario (
    activo,
    password,
    rol,
    username
)
VALUES (
    1,
    '$2a$10$cMF58ZwKo4UpaaDkKgh.YuQDxNLkr/oV3XhJAN0BtiUkZU0Nca98K',
    'ADMIN',
    'admin'
);
SELECT * FROM usuario;

INSERT INTO usuario (activo, password, rol, username)
VALUES
(1, '$2a$10$cMF58ZwKo4UpaaDkKgh.YuQDxNLkr/oV3XhJAN0BtiUkZU0Nca98K', 'ADMIN', 'dayanna'),
(1, '$2a$10$cMF58ZwKo4UpaaDkKgh.YuQDxNLkr/oV3XhJAN0BtiUkZU0Nca98K', 'ADMIN', 'alvaro'),
(1, '$2a$10$cMF58ZwKo4UpaaDkKgh.YuQDxNLkr/oV3XhJAN0BtiUkZU0Nca98K', 'DOCENTE', 'docente01'),
(1, '$2a$10$cMF58ZwKo4UpaaDkKgh.YuQDxNLkr/oV3XhJAN0BtiUkZU0Nca98K', 'DOCENTE', 'melissa'),
(1, '$2a$10$cMF58ZwKo4UpaaDkKgh.YuQDxNLkr/oV3XhJAN0BtiUkZU0Nca98K', 'DOCENTE', 'edwin');
