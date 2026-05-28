SELECT * FROM generador_de_horarios.usuario;

INSERT INTO usuario(
nombres,
apellidos,
correo,
password,
rol
)
VALUES(
'Dayanna',
'Alberssi',
'admin@utp.edu.pe',
'123456',
1
);
INSERT INTO usuario (nombres, apellidos, correo, password, rol) 
VALUES (
'Juan', 
'Pérez', 
'docente@utp.edu.pe', 
'123456', 
'DOCENTE'
);