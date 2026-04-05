# PENDIENTES DE DESARROLLO - BLINDAJE DIGITAL

Este documento lista todas las funcionalidades que faltan por implementar en el proyecto, basadas en el archivo `Especificaciones_a_seguir.md` y el análisis del código existente.

---

## 1. PENDIENTES DE IMPLEMENTAR (ALTA PRIORIDAD)

### Configuración de Producción
- [x] Deshabilitar `show-sql` en producción (`spring.jpa.show-sql=false`)
- [x] Configurar `spring.jpa.open-in-view=false`

### Seguridad
- [x] Implementar Refresh Token (expiración silenciosa sin re-login)
- [x] Remover logs de debug (`System.out.println`) del `JwtAuthenticationFilter`
- [x] Remover `e.printStackTrace()` del `UserController`

### Visitas
- [x] Implementar entry/exit time del visitante (endpoints del guardia)

---

## 2. PENDIENTES DE IMPLEMENTAR (MEDIA PRIORIDAD)

### Seguridad
- [ ] Rate limiting en más endpoints (no solo login)
- [ ] Paginación en endpoints que devuelven listas
- [ ] Endpoint para cambiar contraseña
- [ ] Endpoint para cambiar estado de usuario (ACTIVE/INACTIVE/SUSPENDED)
- [ ] Logs estructurados con SLF4J (reemplazar System.out.println)

---

## 3. MÓDULOS POR COMPLETAR

### Módulo Propiedades (`modules/property/`)
- [ ] CRUD completo de propiedades
- [ ] Endpoints API para gestión de propiedades
- [ ] Servicio `PropiedadService` vacío requiere implementación completa
---
### Módulo Emergencias (`modules/emergency/`)
- [x] Implementar lógica de gestión de emergencias
- [x] Endpoints para crear, listar, actualizar emergencias
- [x] Servicio `EmergenciaService` vacío requiere implementación completa
---
### Módulo Incidentes (`modules/incident/`)
- [ ] Implementar lógica de gestión de incidentes
- [ ] Endpoints para crear, listar, actualizar incidentes
- [ ] Servicio `IncidenteService` vacío requiere implementación completa

### Módulo Paquetería (`modules/packagemodule/`)
- [ ] Implementar lógica de gestión de paquetes
- [ ] Endpoints para registrar, listar, entregar paquetes
- [ ] Servicio `PaqueteService` vacío requiere implementación completa

### Módulo Proveedores (`modules/provider/`)
- [ ] Implementar lógica de gestión de proveedores/visitas de trabajo
- [ ] Endpoints para registrar entrada/salida de proveedores
- [ ] Servicio `ProviderService` vacío requiere implementación completa
---
### Módulo Rondas (`modules/round/`)
- [x] Implementar lógica de gestión de rondas de guardia
- [x] Endpoints para crear, iniciar, completar rondas
- [x] Gestión de checkpoints
- [x] Servicio `RoundService` vacío requiere implementación completa
---
### Módulo Turnos (`core/turn/`)
- [ ] Implementar lógica de turnos de guardia
- [ ] Endpoints para gestión de horarios de guardias
- [ ] Servicio `TurnoService` vacío requiere implementación completa

---

## 4. INTEGRACIONES (ADAPTERS)

### Cámaras (`integrations/camera/`)
- [ ] Implementar lógica de integración con cámaras de seguridad
- [ ] Endpoint para flujo de video en vivo (si aplica)
- [ ] Servicio `CameraService` vacío requiere implementación completa

### LPR - License Plate Recognition (`integrations/lpr/`)
- [ ] Implementar integración con sistema de reconocimiento de patentes
- [ ] Adapter `LprAdapter` vacío requiere implementación completa

### GPS (`integrations/gps/`)
- [ ] Implementar integración con sistema de tracking GPS
- [ ] Adapter `GpsAdapter` vacío requiere implementación completa

### Reconocimiento Facial (`integrations/facial/`)
- [ ] Implementar integración con sistema de reconocimiento facial
- [ ] Adapter `ReconocimientoFacialAdapter` vacío requiere implementación completa

### Gestión de Imágenes (`integrations/image/`)
- [ ] Implementar lógica de gestión de imágenes
- [ ] Almacenamiento y recuperación de imágenes
- [ ] Servicio `imageService` vacío requiere implementación completa

---

## 5. CORE - FUNCIONALIDADES COMPARTIDAS

### Reportes (`core/reporting/`)
- [ ] Implementar generación de reportes
- [ ] DTO `ReporteResponse` definido pero sin lógica
- [ ] Servicio `ReporteService` vacío requiere implementación completa
- [ ] Endpoints para consultar reportes

---

## 6. BAJA PRIORIDAD / FUTURO

- [ ] Exportación CSV de reportes
- [ ] Integración con sistemas externos
- [ ] Mejora de documentación API (OpenAPI/Swagger)
- [ ] Tests unitarios y de integración
- [ ] Cache de consultas frecuentes
- [ ] Monitorización y métricas (Prometheus/Grafana)

---

## RESUMEN ESTADÍSTICO

| Categoría | Cantidad de items pendientes |
|-----------|------------------------------|
| Alta prioridad | 0 |
| Media prioridad | 5 |
| Módulos por completar | 5 |
| Integraciones | 5 |
| Core | 1 |
| Baja prioridad/Futuro | 6 |
| **Total** | **22** |

---

*Documento generado a partir del análisis de `Especificaciones_a_seguir.md` y el código fuente del proyecto.*