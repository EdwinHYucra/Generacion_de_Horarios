# Generador de Horarios Automático — UTP

Sistema web para la generación automática de horarios académicos de la Universidad Tecnológica del Perú, desarrollado con Spring Boot + Thymeleaf + MySQL.

![Java](https://img.shields.io/badge/Java-21-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.6-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-3.x-38BDF8?logo=tailwindcss)
![Progreso](https://img.shields.io/badge/Avance-40%25-yellow)

---

## Descripción

El Generador de Horarios Automático tiene como objetivo optimizar la planificación académica de la UTP mediante la automatización de la asignación de horarios, considerando la disponibilidad de docentes, los cursos programados y los recursos institucionales disponibles.

La aplicación reduce conflictos de programación, mejora la gestión académica y centraliza la administración de recursos en una sola plataforma web.

---

## Tecnologías

| Capa | Tecnologías |
|------|-------------|
| Backend | Java 21, Spring Boot 4.0.6, Spring Security, Spring Data JPA, Hibernate |
| Base de datos | MySQL 8.0 |
| Frontend | Thymeleaf, Tailwind CSS, HTML5, CSS3, JavaScript |
| Herramientas | Maven, Git, GitHub, Visual Studio Code, XAMPP |

---

## Arquitectura del Proyecto

```
src/
└── main/
    ├── java/com/utp/generacionhorarios/
    │   ├── config/          # Configuración de seguridad (Spring Security)
    │   ├── controller/      # Controladores MVC
    │   ├── service/         # Lógica de negocio
    │   ├── repository/      # Acceso a datos (JPA Repositories)
    │   ├── model/           # Entidades JPA
    │   ├── dto/             # Data Transfer Objects
    │   └── security/        # Configuración de autenticación
    └── resources/
        ├── templates/       # Vistas Thymeleaf
        └── static/          # CSS, imágenes, JavaScript
```

---

## Actores del Sistema

- **Administrador** — Gestiona docentes, cursos, aulas y valida los horarios generados.
- **Docente** — Registra su disponibilidad y consulta sus horarios asignados.

---

## Casos de Uso

| ID | Descripción | Estado |
|----|-------------|--------|
| CU-01 | Registrar Docente | Implementado |
| CU-02 | Registrar Curso | Parcialmente implementado |
| CU-03 | Registrar Aula | Pendiente |
| CU-04 | Registrar Disponibilidad de Docente | Implementado |
| CU-05 | Generar Horarios Automáticamente | En desarrollo |
| CU-06 | Detectar Conflictos de Horarios | En desarrollo |
| CU-07 | Visualizar Horarios | Parcialmente implementado |
| CU-08 | Modificar Horarios | Pendiente |

---

## Configuración y Ejecución

### Prerrequisitos

- Java 21 o superior
- XAMPP con MySQL activo
- Git

### Pasos

**1. Clonar el repositorio**
```bash
git clone https://github.com/EdwinHYucra/Generacion_de_Horarios.git
cd Generacion_de_Horarios
```

**2. Crear la base de datos**

Abrir phpMyAdmin (`http://localhost/phpmyadmin`) y ejecutar:
```sql
CREATE DATABASE generador_horarios;
```

**3. Insertar datos iniciales**

Ejecutar el script `src/main/resources/static/Script Inicial.sql` en phpMyAdmin.

**4. Ejecutar el proyecto**
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

**5. Acceder al sistema**
```
http://localhost:8080
```

> **Nota:** Si el entorno local tiene contraseña en MySQL, actualizar el campo `spring.datasource.password` en `src/main/resources/application.properties`.

---

## Estado Actual del Proyecto

Avance estimado: **40 %**

```
[████████████████░░░░░░░░░░░░░░░░░░░░░░░░] 40%
```

### Módulos implementados
- Sistema de autenticación (Spring Security + BCrypt)
- Gestión de usuarios con roles (ADMIN / DOCENTE)
- Gestión de docentes (CRUD completo)
- Registro de disponibilidad docente
- Dashboards para Administrador y Docente
- Base de datos con 10 entidades JPA
- Integración con MySQL mediante Hibernate

### Módulos en desarrollo
- Algoritmo generador de horarios automático
- Detección de conflictos académicos
- Integración completa de la lógica de horarios

### Módulos pendientes
- Gestión completa de aulas
- Modificación manual de horarios
- Validaciones finales e integración completa

---

## Equipo de Desarrollo

| Integrante | Código | Rol |
|------------|--------|-----|
| Alberssi Jorge, Dayanna Simona | U22303776 | Autenticación, Seguridad e Integración de Componentes |
| Huancachoque Yucra, Edwin Eulogio | U24233971 | Gestión de Docentes |
| Montes Acero, Mireya Melissa | U23308222 | Disponibilidad Docente y Gestión de Cursos |
| Quispe Huamani, Álvaro Nando | U19201812 | Base de Datos y Persistencia |

---

## Ramas del Repositorio

| Rama | Responsable | Contenido |
|------|-------------|-----------|
| `main` | Equipo | Código integrado y estable |
| `Dayanna` | Dayanna | Autenticación y dashboards |
| `Edwin` | Edwin | Módulo de docentes |
| `Melissa` | Melissa | Disponibilidad y cursos |
| `Alvaro` | Álvaro | Base de datos, persistencia y módulo de docentes |

---

## Información del Curso

**Curso:** Curso Integrador I: Sistemas Software  
**Sección:** 28636  
**Docente:** Mg. Percy Maldonado Quispe  
**Institución:** Universidad Tecnológica del Perú  
**Sede:** Arequipa, 2026

---

© 2026 Universidad Tecnológica del Perú. Todos los derechos reservados.
