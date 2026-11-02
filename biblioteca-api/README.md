# Biblioteca API

API REST para gestionar libros, ejemplares, usuarios, préstamos y multas.
Hecha con **Spring Boot 3**, **Spring Data JPA** y **MySQL**.

## Estructura

```
src/main/java/com/biblioteca
├── BibliotecaApiApplication.java   Clase principal
├── config/          CORS, manejo de errores, interceptores y creación del admin inicial
├── controllers/     Endpoints REST: reciben la petición y llaman al service
├── dto/             Datos de entrada/salida que no son tablas (login, registro, disponibilidad)
├── models/          Entidades JPA (tablas) y enums
├── repositories/    Interfaces JpaRepository para consultar la base de datos
├── security/        Inicio de sesión y permisos por rol (sin Spring Security)
└── services/        Lógica y reglas de negocio
```

Flujo de una petición:
`AutenticacionInterceptor → AutorizacionInterceptor → controller → service → repository → base de datos`.

## Seguridad: sesiones y roles

No usa Spring Security. Funciona así:

1. `POST /api/auth/login` revisa documento y contraseña y crea una fila en la tabla `sesiones`
   con un **token** aleatorio que vence en 8 horas (`biblioteca.sesion.horas`).
2. El front envía ese token en cada petición: `Authorization: Bearer <token>`.
3. `AutenticacionInterceptor` busca el token y deja al usuario en la petición
   (los controllers lo reciben con `@RequestAttribute(AutenticacionInterceptor.USUARIO_ACTUAL)`).
   Sin token o con un token vencido responde **401**.
4. `AutorizacionInterceptor` revisa la anotación `@SoloAdmin`. Si el usuario no es ADMIN responde **403**.

Las contraseñas se guardan con hash **PBKDF2-SHA256** (`HasheadorContrasena`), nunca en texto plano,
y el campo `contrasenaHash` no sale en ningún JSON (`@JsonIgnore`).

| Rol | Qué puede hacer |
|-----|-----------------|
| `ADMIN` | Todo: libros, ejemplares, usuarios, préstamos, devoluciones, multas y reportes. No toma préstamos. |
| `ESTUDIANTE` / `DOCENTE` | Ver el catálogo con disponibilidad y **solo sus** préstamos y multas (`/api/mi-cuenta`). |

**Administrador inicial:** al arrancar, si no existe ningún ADMIN se crea uno con los datos de
`application.properties` (por defecto documento `admin`, contraseña `Admin123*`).
Cámbialos con las variables de entorno `ADMIN_DOCUMENTO`, `ADMIN_NOMBRE` y `ADMIN_CONTRASENA`.

Los usuarios creados antes de agregar el login no tienen contraseña y no pueden iniciar sesión:
elimínalos y créalos de nuevo desde la pantalla de usuarios (como admin) o con el registro.

## Reglas de negocio

| Regla | Descripción | Dónde |
|-------|-------------|-------|
| RN01 | Máximo de préstamos activos: estudiante 3, docente 5 | `PrestamoService.prestar` |
| RN02 | Plazo: estudiante 7 días, docente 15 días | `PrestamoService.prestar` |
| RN03 | Multa de $1.000 por día de atraso | `MultaService.generarMultaSiHayRetraso` |
| RN04 | Con multas pendientes no se puede prestar | `PrestamoService.prestar` |
| RN05 | Un ejemplar perdido queda a nombre del último usuario que lo tuvo | `EjemplarService.marcarComoPerdido` |
| RN06 | Solo se prestan ejemplares DISPONIBLES | `PrestamoService.prestar` |
| RN07 | Una multa solo se paga una vez | `MultaService.pagarMulta` |

Si una regla no se cumple, el service lanza `IllegalArgumentException` y la API responde
`400` con `{"mensaje": "..."}`. Si algo no existe, la respuesta es `404`.

## Endpoints

| Método | Ruta | Quién | Descripción |
|--------|------|-------|-------------|
| POST | `/api/auth/registro` | Público | Crear cuenta de ESTUDIANTE o DOCENTE |
| POST | `/api/auth/login` | Público | Iniciar sesión, devuelve `{ token, usuario }` |
| POST | `/api/auth/logout` | Con sesión | Cerrar la sesión actual |
| GET | `/api/mi-cuenta` | Con sesión | Mis datos |
| GET | `/api/mi-cuenta/prestamos` | Con sesión | Mis préstamos |
| GET | `/api/mi-cuenta/multas` | Con sesión | Mis multas |
| GET | `/api/libros?titulo=&autor=&isbn=` | Con sesión | Listar o buscar libros |
| GET | `/api/libros/disponibilidad?titulo=&autor=&isbn=` | Con sesión | Catálogo con ejemplares disponibles |
| GET | `/api/libros/{id}` | Con sesión | Obtener libro |
| POST / PUT / DELETE | `/api/libros`, `/api/libros/{id}` | ADMIN | Crear / actualizar / eliminar libro |
| GET / POST | `/api/libros/{id}/ejemplares` | ADMIN | Ejemplares de un libro / agregar ejemplar |
| GET | `/api/ejemplares` | ADMIN | Listar ejemplares |
| GET | `/api/ejemplares/inventario` | ADMIN | Cantidad de ejemplares por estado |
| PUT | `/api/ejemplares/{id}/danado` | ADMIN | Marcar como dañado |
| PUT | `/api/ejemplares/{id}/perdido` | ADMIN | Marcar como perdido |
| GET / POST | `/api/usuarios` | ADMIN | Listar / crear usuarios (con contraseña) |
| GET / PUT / DELETE | `/api/usuarios/{id}` | ADMIN | Obtener / actualizar / eliminar |
| GET | `/api/usuarios/{id}/prestamos` | ADMIN | Préstamos de un usuario |
| GET | `/api/usuarios/{id}/multas` | ADMIN | Multas de un usuario |
| GET | `/api/prestamos` | ADMIN | Listar préstamos |
| GET | `/api/prestamos/vencidos` | ADMIN | Préstamos vencidos |
| POST | `/api/prestamos?usuarioId=1&ejemplarId=1` | ADMIN | Prestar |
| PUT | `/api/prestamos/{id}/devolver` | ADMIN | Devolver (genera multa si hay atraso) |
| GET | `/api/multas` | ADMIN | Listar multas |
| PUT | `/api/multas/{id}/pagar` | ADMIN | Pagar multa |

## Ejemplos

```bash
# Iniciar sesión como admin y guardar el token
curl -X POST localhost:8081/api/auth/login -H "Content-Type: application/json" \
  -d '{"documento":"admin","contrasena":"Admin123*"}'
# → {"token":"AbC...","usuario":{...}}

# Usar el token en las demás peticiones
curl -X POST localhost:8081/api/libros -H "Authorization: Bearer AbC..." \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Clean Code","autor":"Robert C. Martin","isbn":"978-0132350884"}'

# Registrarse como estudiante (no necesita token)
curl -X POST localhost:8081/api/auth/registro -H "Content-Type: application/json" \
  -d '{"nombre":"Ana","documento":"123","contrasena":"secreta1","tipo":"ESTUDIANTE"}'
```

## Cómo ejecutar

1. Tener MySQL corriendo en `localhost:3306` con el usuario `root` y la contraseña `1234`
   (o cambiarlos en `src/main/resources/application.properties`, o con las variables de entorno
   `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`).
2. Ejecutar:

```bash
mvn spring-boot:run   # http://localhost:8081
mvn test              # pruebas de los services con Mockito, sin base de datos
```

Todo se crea solo al arrancar, no hay que ejecutar SQL a mano:

- La base de datos `biblioteca_db` (`createDatabaseIfNotExist=true` en la URL).
- Las tablas (`spring.jpa.hibernate.ddl-auto=update`), incluida `sesiones`.
- El usuario administrador inicial.
