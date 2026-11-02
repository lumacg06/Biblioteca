# Biblioteca Front

Front en **Angular 21** (componentes standalone, signals y sin zone.js) para la API `biblioteca-api`.

## Estructura

```
src/app
├── core/                    Lo que usa toda la app (no tiene pantallas)
│   ├── api.config.ts        URL base de la API (/api)
│   ├── models/              Interfaces que reflejan los JSON del backend
│   ├── services/            Un servicio HTTP por recurso + SesionService y NotificacionService
│   ├── interceptors/        auth: agrega el token · error: muestra los errores de la API
│   └── guards/              Qué pantallas puede abrir cada rol
├── shared/                  Piezas reutilizables
│   ├── components/          EstadoBadge, Notificaciones
│   ├── pipes/               PesosPipe ($ 3.000)
│   └── utils/               fechas y dinero
├── features/                Una carpeta por pantalla (se cargan con lazy loading)
│   ├── auth/                Login y registro (sin sesión)
│   ├── mi-cuenta/           Mis préstamos y multas (estudiante / docente)
│   ├── catalogo/            Catálogo con disponibilidad (estudiante / docente)
│   ├── prestamos/           RF03, RF04, RF05            (admin)
│   ├── libros/              RF01, RF06, RF07            (admin)
│   ├── usuarios/            RF02                        (admin)
│   ├── multas/              RN07                        (admin)
│   └── reportes/            RF08                        (admin)
├── app.routes.ts            Rutas y guards
└── app.config.ts            HttpClient + interceptores
```

Flujo: `pantalla → servicio → HttpClient → authInterceptor (token) → errorInterceptor → API`.

## Sesión y roles

- Al iniciar sesión, `SesionService` guarda `{ token, usuario }` en memoria y en `localStorage`.
- `authInterceptor` envía `Authorization: Bearer <token>` en cada petición.
- Si la API responde **401** (token vencido), `errorInterceptor` cierra la sesión y vuelve a `/login`.
- Los guards (`adminGuard`, `autenticadoGuard`, `invitadoGuard`) solo deciden qué pantallas se ven.
  **La seguridad real está en el backend**: aunque alguien edite `localStorage` para decir que es ADMIN,
  la API le responderá 403.

| Rol | Menú |
|-----|------|
| ADMIN | Préstamos, Libros, Usuarios, Multas, Reportes |
| ESTUDIANTE / DOCENTE | Mi cuenta, Catálogo |

Las reglas de negocio (RN01–RN07) **solo** se validan en el backend. El front muestra el
`mensaje` que devuelve la API cuando una regla no se cumple.

## Cómo ejecutar

1. Levantar el backend en `http://localhost:8081` (ver `../biblioteca-api/README.md`).
2. En esta carpeta:

```bash
npm install
npm start          # http://localhost:4200
```

3. Entrar con el admin inicial (documento `admin`, contraseña `Admin123*`) o crear una cuenta en "Regístrate".

`npm start` usa `proxy.conf.json`: el navegador llama a `http://localhost:4200/api/...` y Angular
lo reenvía a `http://localhost:8081/api/...`. Por eso **no hace falta configurar CORS** en desarrollo.
Si cambias `proxy.conf.json`, reinicia `npm start` (el proxy solo se lee al arrancar).

## Compilar para producción

```bash
npm run build      # genera dist/biblioteca-front
```

Sin el proxy, el navegador llama directo al backend; `CorsConfig.java` ya permite `http://localhost:4200`.
