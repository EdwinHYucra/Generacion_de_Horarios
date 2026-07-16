# Sistema Generador de Horarios Universitarios
### Grupo 3 — Curso Integrador de Sistemas | UTP Arequipa

---

## Descripción

Sistema web para la gestión y generación automática de horarios universitarios. Desarrollado con Spring Boot, Thymeleaf y MySQL, usando arquitectura en capas con patrón DAO/DTO/Service.

**Estado actual:** Módulo SuperAdmin funcional (Fase 1).

---

## Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.0.7 | Framework backend |
| Spring Security | 4.x | Autenticación y roles |
| Spring JDBC | 4.x | Acceso a datos (JdbcTemplate) |
| Thymeleaf | 3.x | Motor de plantillas HTML |
| MySQL | 8.0 | Base de datos |
| Maven | 3.x | Gestión de dependencias |

---

## Actores del sistema

| Actor | Descripción | Estado |
|---|---|---|
| **SuperAdmin** | Registra y gestiona administradores | ✅ Implementado |
| **Admin** | Gestiona horarios y disponibilidad | 🔄 Próxima fase |
| **Docente** | Registra disponibilidad y consulta horarios | 🔄 Próxima fase |

---

## Estructura del proyecto

```
src/
└── main/
    ├── java/pe/edu/utp/generador_horario/
    │   ├── config/
    │   │   └── SeguridadConfig.java        ← Spring Security (rutas + roles)
    │   ├── controller/
    │   │   ├── AuthController.java          ← Login / logout
    │   │   └── SuperAdminController.java    ← CRUD de administradores
    │   ├── dao/
    │   │   ├── UsuarioDAO.java              ← Interfaz
    │   │   ├── AdminDAO.java                ← Interfaz
    │   │   └── implementacion/
    │   │       ├── UsuarioDAOImpl.java      ← SQL con JdbcTemplate
    │   │       └── AdminDAOImpl.java        ← SQL con JdbcTemplate
    │   ├── dto/
    │   │   ├── SuperAdminDTO.java
    │   │   └── AdminRegistroDTO.java
    │   ├── entidad/
    │   │   ├── Usuario.java
    │   │   └── SuperAdmin.java
    │   ├── servicio/
    │   │   ├── AdminServicio.java           ← Interfaz
    │   │   └── implementacion/
    │   │       └── AdminServicioImpl.java   ← Lógica de negocio
    │   └── util/
    │       └── PasswordUtil.java            ← Cifrado BCrypt
    └── resources/
        ├── static/css/estilos.css
        ├── templates/
        │   ├── auth/login.html
        │   └── superadmin/
        │       ├── dashboard.html
        │       ├── listar_admins.html
        │       ├── registrar_admin.html
        │       └── editar_admin.html
        └── application.properties
```

---

## Requisitos previos

- Java 21 instalado
- MySQL 8.0 instalado y corriendo en el puerto 3306
- Maven (o usar el wrapper `mvnw` incluido)

---

## Instalación y configuración

### 1. Clonar o actualizar la rama

```bash
# Si es la primera vez
git clone https://github.com/EdwinHYucra/Generacion_de_Horarios.git
cd Generacion_de_Horarios
git checkout Alvaro

# Si ya tienes el repo
git checkout Alvaro
git pull origin Alvaro
```

### 2. Crear la base de datos

Abre **MySQL Workbench**, conecta al servidor local y ejecuta el archivo `script_bd.sql` que está en la raíz del proyecto.

```sql
-- O ejecuta manualmente:
source script_bd.sql
```

Esto creará la base de datos `generador_horario` con las 5 tablas y el SuperAdmin inicial.

### 3. Configurar `application.properties`

Abre el archivo `src/main/resources/application.properties` y ajusta tu contraseña de MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/generador_horario?useSSL=false&serverTimezone=America/Lima
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD_AQUI
```

> Si no tienes contraseña configurada en MySQL, deja el campo vacío: `spring.datasource.password=`

### 4. Levantar el proyecto

```bash
# Windows
.\mvnw spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

El servidor arranca en: **http://localhost:8080**

---

## Credenciales de acceso

| Campo | Valor |
|---|---|
| Email | superadmin@sistema.com |
| Contraseña | Admin1234 |

---

## Funcionalidades del módulo SuperAdmin

| Funcionalidad | Ruta |
|---|---|
| Login | `GET /login` |
| Dashboard | `GET /superadmin/dashboard` |
| Listar admins | `GET /superadmin/admins` |
| Registrar admin | `GET /superadmin/admins/nuevo` |
| Guardar admin | `POST /superadmin/admins/guardar` |
| Editar admin | `GET /superadmin/admins/editar/{id}` |
| Actualizar admin | `POST /superadmin/admins/actualizar` |
| Desactivar admin | `GET /superadmin/admins/desactivar/{id}` |
| Activar admin | `GET /superadmin/admins/activar/{id}` |
| Logout | `GET /logout` |

---

## Base de datos

### Diagrama de tablas

```
usuario (tabla central)
├── super_admin  ← rol SUPERADMIN
├── admin        ← rol ADMIN (registrado por SuperAdmin)
├── docente      ← rol DOCENTE
└── credencial_acceso ← códigos generados por SuperAdmin
```

### Tablas creadas

| Tabla | Descripción |
|---|---|
| `usuario` | Tabla central con email, password (BCrypt), rol y estado |
| `super_admin` | Extiende usuario con rol SUPERADMIN |
| `admin` | Extiende usuario con rol ADMIN, referencia al SuperAdmin que lo creó |
| `docente` | Extiende usuario con rol DOCENTE (módulo futuro) |
| `credencial_acceso` | Códigos de acceso generados por SuperAdmin (módulo futuro) |

---

## Flujo de autenticación

```
Navegador → Spring Security → UserDetailsService → MySQL
                ↓
         Verifica BCrypt hash
                ↓
         Redirige según rol → /superadmin/dashboard
```

---

## Arquitectura

```
Controller → Service → DAO → MySQL
    ↑                           ↓
  Vista                    ResultSet
(Thymeleaf)               → Entidad → DTO
```

---

## Contribuidores

| Integrante | Módulo |
|---|---|
| Álvaro | SuperAdmin (base del sistema) |
| Edwin | Líder de grupo |
| Dayanna | Módulo Admin (próxima fase) |
| Melissa | Módulo Docente (próxima fase) |

---

## Notas para el equipo

- El archivo `application.properties` **no debe subirse con contraseñas reales** al repo.
- Cada integrante trabaja en **su propia rama** y hace PR hacia `main` cuando termine.
- Usar commits descriptivos: `feat:`, `fix:`, `docs:`, `refactor:`.
- El script `script_bd.sql` debe ejecutarse **una sola vez** por máquina.

---

*Universidad Tecnológica del Perú — Arequipa | 2026*
