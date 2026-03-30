# BLINDAJE DIGITAL — ESPECIFICACIONES TÉCNICAS
> Documento de referencia para el desarrollo. Toda feature nueva debe seguir estos estándares sin excepción.

---

## STACK TECNOLÓGICO

| Componente | Tecnología |
|---|---|
| Backend | Java 17 + Spring Boot 3.2.5 |
| Base de datos | PostgreSQL 16 |
| ORM | Hibernate / Spring Data JPA |
| Seguridad | Spring Security + JWT (jjwt 0.12.5) |
| WebSocket | Spring WebSocket + STOMP + SockJS |
| OCR | Tesseract (instalado en Docker) + ImageMagick |
| Infraestructura | Docker + Docker Compose |
| Build | Gradle 8.7 |

---

## ARQUITECTURA DEL PROYECTO

```
src/main/java/com/blindaje/
├── config/
│   ├── security/           → JWT, filtros, Spring Security
│   └── websocket/          → WebSocket config, interceptor, event listener
├── core/
│   └── notification/       → Sistema de notificaciones (domain, repository, service, publisher)
├── integrations/
│   └── ocr/                → OCR con Tesseract (domain, repository, service, controller)
├── modules/
│   ├── auth/               → Login, DTOs de auth
│   ├── task/               → Tareas del guardia
│   ├── user/               → Usuarios, roles
│   └── visit/              → Visitas, acompañantes
└── shared/
    └── exception/          → GlobalExceptionHandler, BusinessException
```

---

## ROLES DEL SISTEMA

| Rol | Descripción |
|---|---|
| `ADMIN` | Acceso total. Gestión de usuarios, visitas, tareas |
| `GUARD` | Panel de guardia. Tareas, visitas del tenant, notificaciones |
| `RESIDENT` | Panel de residente. Crear visitas, ver sus visitas, acompañantes |
| `SUPERVISOR` | Definido, no implementado aún |

---

## SEGURIDAD — REGLAS OBLIGATORIAS

### JWT
- Algoritmo: HS384
- Expiración: 24 horas (86400000 ms)
- Clave secreta: variable de entorno `JWT_SECRET` (nunca hardcodeada)
- Payload del token: `sub` (username), `role`, `userId`, `tenantId`, `iat`, `exp`
- Refresh token: pendiente de implementar

### Filtros de seguridad (orden obligatorio)
```
RateLimitFilter → JwtAuthenticationFilter → UsernamePasswordAuthenticationFilter
```

### Rate Limiting
- Solo aplica a `POST /api/auth/login`
- Máximo 10 requests por IP por minuto
- Responde `429` con mensaje al superarlo
- Implementado en `RateLimitFilter` (registrado como `@Bean` en `SecurityConfig`)

### Rutas públicas (no requieren token)
```
/api/auth/**
/api/ocr/**
/api/test-data/**
/ws/**
```

### Rutas protegidas por rol (SecurityConfig)
```
/api/users/**  → solo ADMIN
/api/users     → solo ADMIN
```

### Control de roles por endpoint (@PreAuthorize)
Todo controller DEBE tener `@PreAuthorize` en cada endpoint. Reglas actuales:

| Endpoint | Rol requerido |
|---|---|
| `POST /api/users/setup` | ADMIN |
| `GET /api/users` | ADMIN |
| `DELETE /api/users/{id}` | ADMIN |
| `POST /api/visits` | RESIDENT |
| `GET /api/visits/mis-visitas` | RESIDENT |
| `POST /api/visits/{id}/acompanantes` | RESIDENT |
| `GET /api/visits` | GUARD, ADMIN |
| `POST /api/tasks` | GUARD |
| `GET /api/tasks/mis-tareas` | GUARD |
| `PATCH /api/tasks/{id}/estado` | GUARD |
| `PATCH /api/tasks/{id}/observaciones` | GUARD |
| `GET /api/tasks` | ADMIN |
| `GET /api/notificaciones/pendientes` | Cualquier autenticado |
| `PATCH /api/notificaciones/{id}/leer` | Cualquier autenticado |

### Contraseñas
- Siempre encriptadas con BCrypt
- Nunca se exponen en responses (usar DTOs de respuesta)
- Mínimo 6 caracteres (validado en DTO)

---

## MULTI-TENANCY

- Cada usuario tiene un `tenantId` en la DB y en el token JWT
- **Toda consulta a la DB que liste datos debe filtrar por `tenantId`**
- El `tenantId` se extrae siempre del token, nunca del body del request
- Nunca confiar en el `tenantId` que mande el cliente

---

## VALIDACIONES EN DTOs

Todo DTO de entrada DEBE tener validaciones con Jakarta Validation. Siempre usar `@Valid` en el controller.

### Reglas por tipo de campo

| Tipo | Anotaciones obligatorias |
|---|---|
| String obligatorio | `@NotBlank` |
| String con largo | `@Size(min, max)` |
| Email | `@NotBlank` + `@Email` |
| Número obligatorio | `@NotNull` |
| Fecha futura | `@NotNull` + `@Future` |
| DNI argentino | `@Pattern(regexp = "\\d{7,8}")` |
| Enum | `@NotNull` |
| Lista | `@NotEmpty` + `@Valid` si los items tienen validaciones |

### DTOs validados actualmente
- `LoginRequest` → username, password
- `VisitRequest` → visitorName, visitorDocument, scheduledAt
- `TaskRequest` → title, description, priority, deadline, assignedToGuardId
- `CompanionRequest` → companions (lista), cada item: name, document
- `SetupRequest` (interno en UserController) → username, password, email, fullName, role, tenantId

---

## MANEJO DE ERRORES

Centralizado en `GlobalExceptionHandler` (`com.blindaje.shared.exception`).

| Excepción | Status | Formato |
|---|---|---|
| `MethodArgumentNotValidException` | 400 | `{timestamp, status, errors: {campo: mensaje}}` |
| `BusinessException` | 400 | `{timestamp, status, message}` |
| `RuntimeException` | 400 | `{timestamp, status, message}` |
| `Exception` (genérica) | 500 | `{timestamp, status, message: "Internal server error"}` |

**Nunca** exponer stack traces en producción. **Nunca** retornar mensajes de error genéricos sin estructura.

---

## WEBSOCKET

### Configuración
- Endpoint de conexión: `/ws` (con SockJS)
- Broker topics: `/topic` (broadcast), `/queue` (directo)
- Prefijo app: `/app`
- Prefijo usuario directo: `/user`

### Autenticación WebSocket
- El cliente debe mandar el token JWT en el header `Authorization: Bearer <token>` al conectarse (comando STOMP `CONNECT`)
- El `WebSocketAuthInterceptor` valida el token y setea el `Principal`
- Sin token válido → conexión rechazada

### Topics activos

| Topic | Descripción | Suscriptores |
|---|---|---|
| `/topic/visitas/{tenantId}` | Nueva visita registrada | Guardias del tenant |
| `/user/queue/notificaciones` | Notificación directa | Un usuario específico |

### Cuándo publicar por WebSocket
- **Siempre guardar en DB primero**, luego publicar por WebSocket
- Si el usuario está desconectado, la notificación queda en DB y puede consultarla después
- WebSocket es para tiempo real, DB es para persistencia. Ambas son obligatorias.

---

## NOTIFICACIONES

### Tipos
- **Broadcast** (`notificarTenant`): llega a todos los guardias del tenant → `/topic/visitas/{tenantId}`
- **Directa** (`notificarUsuario`): llega a un usuario específico → `/user/queue/notificaciones`

### Cuándo notificar
| Evento | Tipo | Destinatario |
|---|---|---|
| Residente crea visita | Broadcast | Todos los guardias del tenant |
| Guardia asigna tarea a otro guardia | Directa | El guardia asignado |

### Endpoints de notificaciones
- `GET /api/notificaciones/pendientes` → notificaciones no leídas del usuario autenticado
- `PATCH /api/notificaciones/{id}/leer` → marcar como leída (solo el destinatario)

---

## MÓDULO VISITAS

### Entidades
- `Visit`: visitorName, visitorDocument, vehiclePlate, scheduledAt, status, entryTime, exitTime, tenantId, residentId
- `Companion`: name, document, visit (FK)
- `VisitStatus`: PENDING, APPROVED, IN_PROGRESS, COMPLETED, REJECTED

### Flujo
1. Residente crea visita → estado `APPROVED` automáticamente (pre-autorizada)
2. Residente agrega acompañantes por separado (después de crear la visita)
3. Guardia registra entry/exit time → **pendiente de implementar**

### Reglas
- `entryTime` y `exitTime` solo los puede setear el guardia
- Los acompañantes se crean después de la visita (necesitan el `visitId`)
- `@JsonIgnore` en `Companion.visit` para evitar recursión en serialización

---

## MÓDULO TAREAS

### Entidades
- `Task`: title, description, observations, priority, status, deadline, createdByGuardId, assignedToGuardId, tenantId, createdAt
- `TaskStatus`: PENDING, IN_PROGRESS, COMPLETED
- `TaskPriority`: LOW, MEDIUM, HIGH

### Flujo
1. Guardia crea tarea y la asigna (puede asignársela a sí mismo)
2. Si asigna a otro guardia → notificación WebSocket directa
3. Guardia asignado cambia estado
4. Guardia asignado agrega observaciones al finalizar

### Reglas
- Solo el guardia asignado puede cambiar estado y agregar observaciones
- Las observaciones se agregan después de ejecutar la tarea, no al crearla

---

## MÓDULO USUARIOS

### Entidades
- `User`: username, password (BCrypt), email, fullName, role, status, tenantId
- `Role`: ADMIN, SUPERVISOR, GUARD, RESIDENT
- `UserStatus`: ACTIVE, INACTIVE, SUSPENDED

### Endpoints
- `POST /api/users/setup` → crear usuario (solo ADMIN)
- `GET /api/users` → listar usuarios del tenant (solo ADMIN)
- `DELETE /api/users/{id}` → eliminar usuario, devuelve datos del eliminado (solo ADMIN)

### Respuesta de usuario
Siempre usar `UserResponse` DTO (nunca exponer la entidad `User` directamente para evitar exponer el password).

---

## OCR (Integración Tesseract)

### Flujo
1. Recibe imagen como `MultipartFile`
2. Guarda en directorio temporal
3. Intenta con `--psm 1` (detección automática de orientación)
4. Si falla, prueba rotaciones manuales (90°, 180°, 270°) con ImageMagick
5. Extrae texto con idiomas `spa+eng`
6. Parsea DNI, apellido y nombre del texto extraído
7. Guarda en DB y limpia archivos temporales

### Parsing DNI argentino
- **Dorso (MRZ)**: `IDARG{DNI}<` en línea 1, `APELLIDO<<NOMBRE<` en línea 3
- **Frente**: etiquetas `Apellido / Surname` y `Nombre / Name`
- Prioridad: MRZ primero, frente como fallback

### Endpoints
- `POST /api/ocr/scan` → procesar imagen (multipart: `imagen`, `propertyId`)
- `GET /api/ocr/scan/{propertyId}` → consultar último scan

---

## VARIABLES DE ENTORNO

| Variable | Descripción | Obligatoria en producción |
|---|---|---|
| `JWT_SECRET` | Clave secreta para firmar tokens JWT | ✅ Sí |
| `SPRING_DATASOURCE_URL` | URL de conexión a PostgreSQL | ✅ Sí |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la DB | ✅ Sí |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la DB | ✅ Sí |

---

## PENDIENTES DE IMPLEMENTAR

### Alta prioridad
- [ ] Deshabilitar `show-sql` en producción (`spring.jpa.show-sql=false`)
- [ ] Configurar `spring.jpa.open-in-view=false`
- [ ] Refresh token (expiración silenciosa sin re-login)
- [ ] Entry/exit time del visitante (endpoints del guardia)
- [ ] Remover logs de debug (`System.out.println`) del `JwtAuthenticationFilter`

### Media prioridad
- [ ] Rate limiting en más endpoints (no solo login)
- [ ] Paginación en endpoints que devuelven listas
- [ ] Endpoint para cambiar contraseña
- [ ] Endpoint para cambiar estado de usuario (ACTIVE/INACTIVE/SUSPENDED)
- [ ] Logs estructurados con SLF4J (reemplazar System.out.println)

### Baja prioridad / futuro
- [ ] Módulo emergencias
- [ ] Módulo incidentes
- [ ] Módulo paquetería
- [ ] Módulo proveedores
- [ ] Módulo rondas
- [ ] Integración LPR
- [ ] Integración GPS
- [ ] Refresh token completo
- [ ] Exportación CSV de reportes

---

## CONVENCIONES DE CÓDIGO

### Controllers
- Siempre tienen `@PreAuthorize` en cada endpoint
- El token se extrae con el método privado `extraerToken(HttpServletRequest)`
- El `userId` y `tenantId` siempre se extraen del token, nunca del body
- Los DTOs de entrada siempre usan `@Valid`

### Services
- Nunca exponen entidades JPA directamente si tienen datos sensibles
- Siempre verifican que el usuario tenga permisos sobre el recurso antes de modificarlo
- Las notificaciones WebSocket se envían después de guardar en DB

### DTOs
- DTOs de entrada: validaciones Jakarta Validation obligatorias
- DTOs de respuesta: nunca incluir campos sensibles (password, etc.)
- Usar DTOs de respuesta separados de las entidades JPA

### Repositorios
- Siempre filtrar por `tenantId` en queries que listen datos
- Nunca devolver datos de otros tenants

---

## DOCKER

### Servicios
- `blindaje-postgres`: PostgreSQL 16, puerto 5432
- `blindaje-backend`: Spring Boot, puerto 8080

### Volúmenes
- `postgres_data`: datos persistentes de PostgreSQL

### Build
- Multi-stage: build con `gradle:8.7-jdk17`, runtime con `eclipse-temurin:17-jdk`
- Tesseract OCR + idioma español + ImageMagick instalados en runtime