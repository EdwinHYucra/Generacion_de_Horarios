# Sistema de Generación de Horarios Automático
### Universidad Tecnológica del Perú
**Curso:** Curso Integrador I — Sistemas de Software  
**Docente:** M. Sc. Percy Maldonado Quispe  
**Sección:** 28636 | **Grupo:** 3  
**Ciclo Académico:** 2026-I

---

## Descripción del Proyecto

El presente sistema tiene como finalidad automatizar la generación de horarios académicos en la Universidad Tecnológica del Perú, eliminando el proceso manual que actualmente genera errores de asignación, conflictos de disponibilidad docente y uso ineficiente de aulas.

La solución está desarrollada bajo la arquitectura **MVC (Modelo-Vista-Controlador)** utilizando el framework **Spring Boot**, con persistencia de datos mediante **Spring Data JPA** sobre un motor de base de datos **MySQL**, e interfaz web renderizada con el motor de plantillas **Thymeleaf**.

---

## Objetivos del Sistema

- Registrar y gestionar docentes, cursos y aulas de forma centralizada.
- Registrar la disponibilidad horaria de cada docente por semestre académico.
- Generar horarios automáticamente respetando las restricciones de disponibilidad y capacidad.
- Detectar y prevenir conflictos de asignación en tiempo real.
- Permitir la visualización y modificación controlada de horarios generados.

---

## Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 | Lenguaje principal de desarrollo |
| Spring Boot | 3.x | Framework de aplicación web |
| Spring Data JPA | 3.x | Capa de persistencia y acceso a datos |
| Hibernate | 6.x | Implementación ORM del estándar JPA |
| Thymeleaf | 3.x | Motor de plantillas para vistas HTML |
| MySQL | 8.x | Sistema gestor de base de datos |
| Lombok | 1.18.x | Reducción de código repetitivo |
| Maven | 3.x | Gestión de dependencias y construcción |

---

## Requisitos del Entorno

Antes de ejecutar el proyecto, asegúrese de contar con lo siguiente instalado:

- **Java Development Kit (JDK) 21** — [Descargar](https://www.oracle.com/java/technologies/downloads/)
- **XAMPP** (con servicio MySQL activo) — [Descargar](https://www.apachefriends.org/)
- **IntelliJ IDEA** (recomendado) o cualquier IDE compatible con proyectos Maven
- **Git** — [Descargar](https://git-scm.com/)

---

## Instrucciones de Instalación y Configuración

### Paso 1 — Clonar el repositorio

```bash
git clone https://github.com/EdwinHYucra/Generacion_de_Horarios.git
cd Generacion_de_Horarios
```

### Paso 2 — Crear la base de datos

1. Iniciar **XAMPP** y activar el servicio **MySQL**.
2. Acceder a **phpMyAdmin** mediante `http://localhost/phpmyadmin`.
3. Crear una base de datos con el siguiente nombre:

```sql
CREATE DATABASE generador_horarios CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
```

> Las tablas serán generadas automáticamente por **Hibernate** al iniciar la aplicación, gracias a la configuración `spring.jpa.hibernate.ddl-auto=update`.

### Paso 3 — Configurar la conexión a la base de datos

Verificar el archivo `src/main/resources/application.properties` y ajustar las credenciales si es necesario:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/generador_horarios
spring.datasource.username=root
spring.datasource.password=        # Dejar vacío si usa XAMPP por defecto
```

### Paso 4 — Cargar las dependencias Maven

En **IntelliJ IDEA**: clic derecho sobre el archivo `pom.xml` → **Maven** → **Reload Project**.

### Paso 5 — Ejecutar la aplicación

Ejecutar la clase principal del proyecto:

```
src/main/java/com/utp/generadorhorarios/GeneradorHorariosApplication.java
```

O mediante terminal:

```bash
./mvnw spring-boot:run
```

Una vez iniciada la aplicación, acceder desde el navegador a:

```
http://localhost:8080
```

---

## Estructura del Proyecto

```
src/
└── main/
    ├── java/com/utp/generadorhorarios/
    │   ├── model/          ← Entidades JPA que representan las tablas de la BD
    │   ├── repository/     ← Interfaces de acceso a datos (Spring Data JPA)
    │   ├── service/        ← Lógica de negocio del sistema
    │   └── controller/     ← Controladores MVC (rutas y respuestas HTTP)
    └── resources/
        ├── application.properties  ← Configuración del sistema
        └── templates/              ← Vistas HTML renderizadas con Thymeleaf
```

---

## Modelo de Base de Datos

El esquema de la base de datos está compuesto por las siguientes entidades principales:

| Tabla | Descripción |
|---|---|
| `semestre` | Registro de ciclos académicos |
| `usuario` | Credenciales de acceso al sistema |
| `docente` | Información del personal docente |
| `curso` | Catálogo de cursos por ciclo |
| `aula` | Aulas disponibles con tipo y capacidad |
| `bloque_horario` | Franjas horarias del día |
| `disponibilidad_docente` | Disponibilidad de cada docente por semestre |
| `horario` | Cabecera del horario generado por semestre |
| `detalle_horario` | Asignaciones concretas de docente, curso, aula y bloque |
| `solicitud_cambio` | Registro de solicitudes de modificación de horario |

---

## Control de Versiones — Ramas del Equipo

El repositorio sigue la convención de **una rama por integrante**. Está prohibido realizar commits directamente sobre la rama `main`.

| Integrante | Rama | Responsabilidad |
|---|---|---|
| Edwin Yucra | `Edwin` | Configuración base del proyecto y Spring Boot |
| Dayanna | `Dayanna` | Por definir |
| Melissa | `Melissa` | Por definir |
| Alvaro Nando | `Alvaro` | Modelo de datos: entidades JPA y repositorios |

---

## Casos de Uso Implementados

| Código | Caso de Uso |
|---|---|
| CU-01 | Registrar Docente |
| CU-02 | Registrar Curso |
| CU-03 | Registrar Aula |
| CU-04 | Registrar Disponibilidad Docente |
| CU-05 | Generar Horario Automático |
| CU-06 | Detectar Conflictos de Horario |
| CU-07 | Visualizar Horario |
| CU-08 | Modificar Horario |

---

## Licencia

Proyecto académico desarrollado con fines educativos para la Universidad Tecnológica del Perú. Todos los derechos reservados © 2026.
