# 002 — Gestión de clientes

- Estado: verificado
- Rama: `feature/002-gestion-clientes`
- Fecha: 2026-11-26
- Relación: F-01 del roadmap, con F-11 (persistencia local) como requisito no funcional.

## Contexto

KallpaBase evoluciona de la app base (un contador Compose) hacia una aplicación para gestionar la cartera de clientes y armar presupuestos. El roadmap identifica F-01 «Gestión de clientes» como la primera funcionalidad MVP: necesita una cartera centralizada que sirva de base para los presupuestos (F-04) y para el historial por cliente (F-08). La persistencia sigue el enfoque B por fases: datos locales en el dispositivo en el MVP y sincronización con backend en Fase 2 (F-15).

## Problema

Hoy no existe forma de registrar clientes en la app, por lo que los datos de contacto quedan fuera de la herramienta y deben reescribirse al armar un presupuesto. Sin un almacenamiento local, además, la información se perdería al cerrar la aplicación. Se necesita un alta/edición/baja mínima y verificable antes de avanzar a presupuestos.

## Objetivos

- Permitir dar de alta, editar y dar de baja clientes con datos de contacto, con persistencia local.
- Mostrar el listado de clientes guardados después de cada operación, incluido el estado vacío inicial.
- Validar los campos obligatorios y el formato de email antes de guardar.
- Cubrir cada criterio de aceptación con un test unitario, Compose o instrumentado identificable.
- Dejar la capa de datos preparada para la futura sincronización de F-15.

## No-objetivos

- No implementar búsqueda, filtros ni orden de clientes (F-02).
- No crear presupuestos, catálogo de ítems ni cálculo de totales (F-03, F-04, F-05).
- No mostrar historial de presupuestos por cliente (F-08).
- No implementar usuarios, roles, clientes compartidos ni sincronización (F-15).
- No generar PDFs ni enviar emails/WhatsApp (F-09, F-10).
- No exportar/importar datos (F-14) ni registrar auditoría o deshacer bajas.

## Requisitos funcionales

- RF-1: La pantalla `Clientes` muestra el botón `Nuevo cliente` y, sin registros, el estado vacío `Sin clientes`.
- RF-2: El formulario tiene los campos `Nombre` (obligatorio), `Teléfono` (opcional), `Email` (opcional con formato válido) y `Dirección` (opcional), además de los botones `Guardar` y `Cancelar`.
- RF-3: El alta crea un registro con ID no vacío y lo muestra en el listado al volver de `Guardar`.
- RF-4: La edición precarga los datos del cliente, conserva su ID y refleja los cambios en el listado.
- RF-5: La baja pide confirmación (`Eliminar` → `Confirmar`); `Cancelar` deja los datos intactos.
- RF-6: Cada fila del listado muestra `Nombre` y, cuando existan, `Email` o `Teléfono`.
- RF-7: Los datos de alta, edición y baja sobreviven al cierre y reinicio de la aplicación.

## Requisitos no funcionales

- RNF-1: Persistencia local con Room/DataStore según F-11, detrás de una capa de repositorio desacoplada de la UI para permitir F-15 sin reescribir casos de uso.
- RNF-2: Compatibilidad con `minSdk 24`, Compose con Material 3 y tests de UI con `createComposeRule` (API v2).
- RNF-3: Las versiones de dependencias nuevas se declaran en `gradle/libs.versions.toml`, nunca como literales en `build.gradle.kts`.
- RNF-4: Las operaciones de la pantalla funcionan sin conectividad; no se envían datos a red en el MVP.
- RNF-5: Botones y campos exponen semántica accesible (etiquetas visibles/ContentDescription) y pasan `lint`.
- RNF-6: La puerta local `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y `connectedAndroidTest` en el AVD `KallpaBase_API37` quedan en verde antes de cerrar la spec.

## Criterios de aceptación

- [x] CA-1: Dado que no hay clientes guardados, cuando se abre la pantalla `Clientes`, entonces se muestran `Sin clientes` y el botón `Nuevo cliente`. Verificación: `ClientListTest.listaVacia_muestraEstadoVacio` con `createComposeRule` → `./gradlew connectedAndroidTest`.
- [x] CA-2: Dado `Nombre` = `Ana Pérez`, `Teléfono` = `+51 999 000 111` y `Email` = `ana@example.com`, cuando se pulsa `Guardar`, entonces el listado muestra `Ana Pérez` y `ana@example.com` y el repositorio local contiene un registro con ID no vacío. Verificación: `CreateClientUseCaseTest.guardaConId` → `./gradlew test`, y `ClientFormTest.altaValida_actualizaLista` → `connectedAndroidTest`.
- [x] CA-3: Dado `Nombre` en blanco o `Email` = `ana` sin `@`, cuando se pulsa `Guardar`, entonces no se crea ningún registro y se muestra `El nombre es obligatorio` o `El email no es válido` según el caso. Verificación: `ClientValidatorTest.camposInvalidos` → `./gradlew test`, y `ClientFormTest.camposInvalidos_muestraErrores` → `connectedAndroidTest`.
- [x] CA-4: Dado el cliente `Ana Pérez`, cuando se cambia su `Teléfono` a `+51 987 654 321` y se guarda, entonces el listado muestra el teléfono nuevo y el ID del registro permanece igual. Verificación: `UpdateClientUseCaseTest.mantieneId` → `./gradlew test`, y `ClientEditTest.edicion_actualizaLista` → `connectedAndroidTest`.
- [x] CA-5: Dado el cliente `Ana Pérez`, cuando se pulsa `Eliminar` y luego `Cancelar`, entonces el cliente continúa en el listado y sigue en el repositorio local. Verificación: `ClientDeleteTest.cancelar_noElimina` → `connectedAndroidTest`.
- [x] CA-6: Dado el cliente `Ana Pérez`, cuando se pulsa `Eliminar` y `Confirmar`, entonces desaparece del listado y una consulta directa al repositorio por su ID devuelve 0 registros. Verificación: `DeleteClientUseCaseTest.bajaEliminaRegistro` → `./gradlew test`, y `ClientRepositoryTest.bajaEliminaRegistro` → `connectedAndroidTest`.
- [x] CA-7: Dado un cliente guardado con nombre, teléfono, email y dirección, cuando se cierra y reabre la app en `KallpaBase_API37`, entonces aparece en el listado con los cuatro valores idénticos. Verificación: `ClientPersistenceTest.altaSobreviveReinicio` → `./gradlew connectedAndroidTest`.
- [x] CA-8: Dado un cliente eliminado, cuando se reinicia la app, entonces no reaparece en el listado. Verificación: `ClientPersistenceTest.bajaSobreviveReinicio` → `./gradlew connectedAndroidTest`.
- [x] CA-9: Dado el cambio completo, cuando se ejecuta la puerta de calidad, entonces `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` termina en `BUILD SUCCESSFUL` y `./gradlew connectedAndroidTest` pasa en el AVD `KallpaBase_API37`. Verificación: salidas de ambos comandos registradas en `review.md` durante `/spec.verify`.

## Pruebas requeridas

- Unitarias: `ClientValidatorTest` (nombre vacío, email sin `@`, email válido), `CreateClientUseCaseTest`, `UpdateClientUseCaseTest` y `DeleteClientUseCaseTest` con repositorio falso. Ejecutar con `./gradlew test`.
- Compose UI: `ClientListTest`, `ClientFormTest`, `ClientEditTest` y `ClientDeleteTest` con `createComposeRule` (API v2); comprobar textos, errores y actualización del listado. Ejecutar con `./gradlew connectedAndroidTest`.
- Instrumentadas: `ClientRepositoryTest` y `ClientPersistenceTest` contra la base local real, incluidos reinicio de app y ausencia de red. Ejecutar con `./gradlew connectedAndroidTest` en el AVD `KallpaBase_API37` (Pixel 9 x86_64 / Google APIs).

## Riesgos y decisiones abiertas

- Baja física frente a baja lógica (`flag activo`): decidir en `/spec.plan`; CA-6 y CA-8 están redactados para valer con cualquiera de las dos opciones.
- Unicidad de clientes: por defecto se permiten duplicados de nombre/email en el MVP; confirmar si se requiere validación de unicidad antes de implementar.
- Validación de teléfono: se guarda como texto libre; definir en el plan si se valida formato por país o se deja para una spec posterior.
- Campos ampliados (razón social, RUC, notas): no bloquean F-01, pero pueden requerir migración si se incorporan después del primer release.
- Esquema de sincronización futura (ID remoto, `updatedAt`, resolución de conflictos): alinear con F-15 en el plan para evitar una migración posterior.
- La dependencia de Room debe entrar por `gradle/libs.versions.toml`; verificar en el plan su compatibilidad con `minSdk 24` y el efecto en CI.
