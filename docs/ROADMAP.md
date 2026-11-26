# Roadmap de funcionalidades

- Estado: inventario inicial aprobado
- Fecha: 2026-11-26
- Relación con el flujo SDD: este documento es el inventario de producto; cada funcionalidad que pase a implementarse debe tener su propia spec en `docs/specs/<NNN-slug>/spec.md`.

## Contexto

KallpaBase debe evolucionar de la app base (un contador Compose) hacia una aplicación para **gestionar la cartera de clientes y armar presupuestos**, con **uso en equipo**.

La persistencia sigue el enfoque **B por fases**: el MVP guarda los datos en el dispositivo (`Room` / `DataStore`) con una capa preparada para sincronización; la sincronización con backend, los clientes compartidos y los roles se implementan en Fase 2.

## Convención

- Los IDs `F-NN` son estables: no se renumeran aunque cambie la prioridad.
- Prioridades: `MVP` (necesario para la primera versión), `Fase 2` (planificado después del MVP), `Idea` (propuesta sin decidir).
- Estados: `pendiente`, `spec`, `plan`, `tasks`, `en progreso`, `verificado`, `cerrado`.
- La columna `Spec` se rellena con `docs/specs/<NNN-slug>/spec.md` cuando la funcionalidad entra al ciclo SDD. La numeración `NNN` es secuencial e independiente del ID `F-NN`.

## Módulos

- **Cartera**: clientes e historial.
- **Presupuestos**: armado, cálculo, estados y entrega.
- **Operaciones**: proveedores, stock, maquinarias y recursos imputados.
- **Configuración**: datos del negocio, moneda e impuestos.
- **Colaboración**: usuarios, sincronización y roles.
- **Reportes**: métricas y exportación.
- **Transversal**: capacidades que afectan a varios módulos.

## Inventario

| ID | Funcionalidad | Módulo | Descripción | Valor | Prioridad | Estado | Spec |
| --- | --- | --- | --- | --- | --- | --- | --- |
| F-01 | Gestión de clientes | Cartera | Alta, edición y baja de clientes con datos de contacto | Centralizar la cartera y tener la base para los presupuestos | MVP | verificado | `docs/specs/002-gestion-clientes/spec.md` |
| F-02 | Listado y búsqueda de clientes | Cartera | Listar, filtrar y abrir fichas de clientes | Encontrar rápido un cliente y ver su contexto | MVP | pendiente | — |
| F-03 | Catálogo de productos/servicios | Presupuestos | Ítems reutilizables con descripción y precio base | No reescribir los mismos conceptos en cada presupuesto | Fase 2 | pendiente | — |
| F-04 | Crear presupuesto | Presupuestos | Seleccionar cliente, agregar ítems, cantidades, fecha y validez | Armar presupuestos de forma rápida y consistente | MVP | pendiente | — |
| F-05 | Cálculo de totales | Presupuestos | Subtotal, descuento, impuesto global configurable y total automáticos | Evitar errores de cálculo manual | MVP | pendiente | — |
| F-06 | Editar y duplicar presupuestos | Presupuestos | Modificar borradores y reutilizar uno como base | Ahorrar tiempo en negociaciones | MVP | pendiente | — |
| F-07 | Estados de presupuesto | Presupuestos | Borrador → enviado → aprobado / rechazado / vencido | Seguimiento comercial | Fase 2 | pendiente | — |
| F-08 | Historial por cliente | Cartera | Ver todos los presupuestos de un cliente | Contexto al negociar | Fase 2 | pendiente | — |
| F-09 | Generar PDF | Presupuestos | Documento exportable y listo para compartir | Entrega profesional al cliente | Fase 2 | pendiente | — |
| F-10 | Envío por email / WhatsApp | Presupuestos | Compartir el presupuesto desde la app | Cerrar el ciclo sin salir de la aplicación | Fase 2 | pendiente | — |
| F-11 | Persistencia local | Transversal | `Room` / `DataStore`; los datos sobreviven al cierre de la app, con capa preparada para sincronización | Confiabilidad y base para F-15 | MVP | verificado | `docs/specs/002-gestion-clientes/spec.md` |
| F-12 | Notas y recordatorios | Cartera | Avisos de vencimiento de presupuestos, stock mínimo y mantenimiento | No perder oportunidades ni tareas pendientes | Idea | pendiente | — |
| F-13 | Dashboard comercial | Reportes | Métricas: aprobados, facturación estimada, conversión y valorización de stock | Visión del negocio | Idea | pendiente | — |
| F-14 | Exportar datos (CSV) | Reportes | Backup y portabilidad de clientes y presupuestos | Propiedad de los datos | Idea | pendiente | — |
| F-15 | Multiusuario, sincronización y roles | Colaboración | Clientes compartidos por el equipo, sincronización y roles `admin` / `vendedor` / `solo lectura` | Trabajo en equipo con permisos controlados | Fase 2 | pendiente | — |
| F-16 | Configuración del negocio | Configuración | Moneda, impuesto global configurable, datos del emisor, numeración y validez por defecto | Presupuestos correctos sin rehacerlos cada vez | MVP | pendiente | — |
| F-17 | Múltiples impuestos por línea | Configuración | Tasas distintas por ítem | Casos fiscales complejos | Idea | pendiente | — |
| F-18 | Gestión de proveedores | Operaciones | Alta y edición, contacto, productos suministrados y condiciones de compra | Reposición y trazabilidad | Fase 2 | pendiente | — |
| F-19 | Control de stock | Operaciones | Existencias, entradas y salidas, stock mínimo y alertas | Saber qué se puede presupuestar | Fase 2 | pendiente | — |
| F-20 | Control de maquinarias | Operaciones | Equipos, disponibilidad, mantenimiento y asignación | Planificar trabajos con recursos reales | Fase 2 | pendiente | — |
| F-21 | Imputar recursos a presupuestos | Operaciones | Usar ítems de stock y maquinaria (con tarifa) dentro de un presupuesto | Presupuestos ajustados a los recursos disponibles | Idea | pendiente | — |
| F-22 | Desactivación de clientes | Cartera | Sustituir la baja por un estado `inactivo`, conservando el historial del cliente | No perder presupuestos ni datos al dejar de trabajar con un cliente | Idea | pendiente | — |
| F-23 | Navegación principal | Transversal | Barra inferior con cuatro destinos y barra superior con título y acciones | Acceder a los módulos sin callejones sin salida | MVP | spec | `docs/specs/003-menu-principal/spec.md` |

## Decisiones tomadas

| Decisión | Detalle |
| --- | --- |
| Persistencia | Enfoque B por fases: local-first en el MVP y backend con sincronización en Fase 2. |
| Equipo | Los clientes son compartidos por todo el equipo, no privados por usuario. |
| Roles | Necesarios, pero en Fase 2 junto con F-15; no bloquean el MVP. |
| PDF y envío | Generación de PDF (F-09) y envío por email/WhatsApp (F-10) en Fase 2. |
| Impuestos | Un impuesto global configurable en el MVP (F-05 / F-16); múltiples tasas por línea queda como Idea (F-17). |
| Operaciones | Proveedores, stock y maquinarias en Fase 2; imputar recursos a presupuestos queda como Idea (F-21). |
| Clientes | El MVP mantiene la baja de F-01 (spec 002); marcar como inactivo en lugar de eliminar se evalúa como Idea (F-22), conservando el historial. |
| Navegación | Menú inferior de cuatro destinos; los módulos sin pantalla se muestran visibles pero deshabilitados con indicador `Próximamente` / `Fase 2` (F-23). |

## Navegación principal

La pantalla principal es un `Scaffold` de Material 3 con una `NavigationBar` inferior de cuatro destinos y un `TopAppBar` con el título del destino activo.

| Destino | Módulo | Estado inicial | Funcionalidades asociadas |
| --- | --- | --- | --- |
| Clientes | Cartera | habilitado (spec 002 verificada) | F-01, F-02, F-22 |
| Presupuestos | Presupuestos | deshabilitado (`Próximamente`) | F-04…F-10, F-17 |
| Operaciones | Operaciones | deshabilitado (`Fase 2`) | F-18…F-21 |
| Configuración | Configuración | deshabilitado (`Próximamente`) | F-16, F-17 |

El `TopAppBar` muestra las acciones `Buscar` y `Sincronizar`, además de un overflow con `Reportes`, `Exportar` y `Acerca de`. Todas permanecen deshabilitadas hasta que exista la funcionalidad correspondiente.

**Regla de habilitación:** un destino o acción se habilita únicamente cuando su pantalla está implementada y su spec tiene estado `verificado`. Al habilitarse, se actualizan esta tabla y el estado de la fila correspondiente del inventario.

**Decisiones derivadas:** el destino inicial es `Clientes`; el botón `Nuevo cliente` permanece dentro de `ClientsScreen` (no se mueve a un FAB global); no se añade `Navigation Compose` hasta que haya deep links o un back stack de varios niveles. El detalle verificable está en la spec [`003-menu-principal`](specs/003-menu-principal/spec.md).

## Dependencias entre funcionalidades

- **F-19 ↔ F-03**: el catálogo de productos/servicios puede alimentarse del stock en lugar de duplicar conceptos.
- **F-21 ↔ F-04 / F-05**: si un presupuesto consume stock o maquinaria, el cálculo de totales debe considerar esas tarifas.
- **F-18 ↔ F-19**: cada producto puede tener un proveedor asociado para reposiciones.
- **F-11 → F-15**: el modelo de datos local debe estar preparado para la futura sincronización.
- **F-12 / F-13**: los recordatorios y el dashboard pueden aprovechar stock, maquinarias y estados de presupuesto.

## Preguntas abiertas

Se resuelven al escribir la spec correspondiente; no bloquean el inventario.

- Proveedor de backend para F-15: Firestore, Supabase o API propia.
- Diseño concreto de roles y permisos por módulo en F-15.
- Formato de numeración de presupuestos en F-16.
- ¿El catálogo (F-03) se deriva del stock (F-19) o se mantiene independiente?
- Política de resolución de conflictos al sincronizar cambios concurrentes en F-15.

## Cómo pasar una funcionalidad a spec

1. Elegir el `ID F-NN` del inventario.
2. Ejecutar `/spec.new <NNN> <slug>` para crear `docs/specs/<NNN-slug>/spec.md` con criterios de aceptación verificables.
3. Actualizar en esta tabla la columna `Spec` con la ruta creada y el `Estado` con la fase del ciclo SDD.
4. Continuar con `/spec.plan` → `/spec.tasks` → `/spec.implement` → `/spec.verify` + `/spec.review` → PR.

Primera funcionalidad ejecutada: **F-01 Gestión de clientes** → `docs/specs/002-gestion-clientes/spec.md` (verificada), con F-11 (persistencia local) como requisito no funcional. En curso: **F-23 Navegación principal** → `docs/specs/003-menu-principal/spec.md`. Siguiente recomendada: **F-02 Listado y búsqueda de clientes** → `004-listado-busqueda-clientes`.
