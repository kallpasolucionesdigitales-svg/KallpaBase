# 004 — Listado y búsqueda de clientes

- Estado: verificado
- Rama: `feature/004-listado-busqueda-clientes`
- Fecha: 2026-11-26
- Relación: F-02 del roadmap (módulo `Cartera`), sobre la base verificada de F-01/F-11 (spec 002) y F-23 (spec 003).

## Contexto

La spec 002 ya permite dar de alta, editar y eliminar clientes con persistencia local, y la spec 003 situó esa pantalla dentro del destino `Clientes` del menú principal. El listado actual muestra todas las filas en el orden que devuelve Room y la acción `Buscar` del `TopAppBar` está deshabilitada a la espera de esta funcionalidad. Con una cartera que crece, localizar un cliente por nombre, email, teléfono o dirección obliga a recorrer toda la lista; el roadmap prioriza F-02 como MVP para encontrar rápido un cliente y ver su contexto antes de avanzar a presupuestos (F-04) e historial (F-08).

## Problema

Hoy no hay forma de filtrar la cartera ni de consultar la ficha completa de un cliente sin entrar al formulario de edición. La barra superior anuncia `Buscar` como «no disponible», que es una promesa incumplida desde la spec 003. Además, el estado vacío `Sin clientes` no distingue entre «no hay registros» y «la búsqueda no encontró coincidencias», lo que induce a error al usuario.

## Objetivos

- Habilitar `Buscar` únicamente para el destino `Clientes` y ofrecer un campo de consulta que filtre sin salir de la pantalla.
- Filtrar por coincidencia de subcadena, sin distinción de mayúsculas/minúsculas, sobre nombre, email, teléfono y dirección.
- Diferenciar el estado vacío del repositorio del estado «sin resultados» y permitir limpiar la consulta.
- Conservar la consulta al rotar el dispositivo.
- Permitir abrir una ficha de solo lectura con los datos del cliente y reutilizar el formulario de edición existente.
- Mantener el comportamiento y los tests de las specs 002 y 003, salvo el ajuste mínimo de la aserción de `Buscar` en `MainMenuTest`.
- Cubrir cada criterio de aceptación con un test unitario, Compose o instrumentado identificable.

## No-objetivos

- No modificar el alta, la edición, la baja ni las validaciones de la spec 002 (RF-2 a RF-7).
- No cambiar el esquema Room, el DAO ni la persistencia local de F-11; no se requiere migración.
- No habilitar `Sincronizar`, `Reportes`, `Exportar`, `Acerca de` ni los destinos `Presupuestos`, `Operaciones` y `Configuración` (F-15, F-13, F-14, F-04…F-10, F-16…F-21).
- No añadir orden configurable, filtros por fecha/estado ni agrupaciones; se conserva el orden por nombre ascendente actual.
- No sustituir la baja física por baja lógica ni introducir clientes inactivos (F-22).
- No convertir la ficha en un formulario editable en sitio; `Editar` reutiliza el diálogo existente.
- No añadir `Navigation Compose`, deep links ni un back stack propio (F-23).
- No exportar ni importar datos (F-14) ni generar reportes (F-13).

## Requisitos funcionales

- RF-1: La pantalla `Clientes` muestra el listado de la spec 002 ordenado por nombre ascendente, con `Nuevo cliente`, `Editar` y `Eliminar` intactos; con `Clientes` como destino activo, `Buscar` está habilitado y expone la descripción `Buscar clientes`.
- RF-2: Al pulsar `Buscar` se muestra/enfoca el campo `Buscar cliente` sin cambiar de destino; `Sincronizar` y el menú overflow permanecen deshabilitados conforme a la spec 003.
- RF-3: El campo filtra los clientes ya cargados por coincidencia de subcadena, recortando espacios y sin distinguir mayúsculas/minúsculas, sobre `nombre`, `email`, `telefono` y `direccion`; los campos nulos o en blanco no generan coincidencias.
- RF-4: Con la consulta vacía se muestran todos los clientes; con consulta sin coincidencias se muestra `Sin resultados` en lugar de `Sin clientes`.
- RF-5: El botón `Limpiar`, visible solo cuando la consulta no está vacía, vacía el campo y restaura el listado completo.
- RF-6: La consulta introducida se conserva al rotar el dispositivo y el listado permanece filtrado.
- RF-7: Cada fila incorpora `Ver ficha`, que abre el diálogo de solo lectura `Ficha de cliente` con nombre, teléfono, email y dirección; los campos ausentes se omiten.
- RF-8: El diálogo de ficha ofrece `Cerrar` y `Editar`; `Editar` lo cierra y abre el formulario de edición de la spec 002 con los datos precargados y el mismo ID.
- RF-9: `Nuevo cliente` sigue accesible durante la búsqueda; un cliente guardado que coincide con la consulta aparece en el listado filtrado.

## Requisitos no funcionales

- RNF-1: El filtrado opera sobre el `List<Client>` ya obtenido por `ListClientsUseCase`; no se altera `ClientDao`, el esquema Room ni el orden `ORDER BY nombre ASC` heredado de la spec 002.
- RNF-2: Se mantiene la firma pública `ClientsScreen(repository: ClientRepository, modifier: Modifier = Modifier)`. Los tests de la spec 002 no se modifican; de la spec 003 solo se ajusta la aserción de `Buscar` en `MainMenuTest.barraSuperior_estadoInicial`, conservando las de `Sincronizar`, overflow y destinos deshabilitados.
- RNF-3: `MainScreen` habilita `Buscar` solo cuando el destino activo es `Clientes`; el resto de acciones y destinos siguen deshabilitados con su `contentDescription` actual.
- RNF-4: Compatibilidad con `minSdk 24`, Compose con Material 3 (`OutlinedTextField`, `Button`, `AlertDialog`) y semántica accesible en campo, botones y ficha; `lint` sin errores nuevos.
- RNF-5: No se añaden dependencias; si aparece alguna, se declara en `gradle/libs.versions.toml`, nunca como literal en `build.gradle.kts`.
- RNF-6: Tests de UI con `createComposeRule` (API v2). La puerta es `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y `./gradlew connectedAndroidTest` en el AVD `KallpaBase_API37` antes de cerrar la spec.

## Criterios de aceptación

- [x] CA-1: Dado tres clientes guardados (`Ana Pérez`, `Luis Gómez`, `Marta Ruiz`), cuando se abre el destino `Clientes`, entonces aparecen los tres en ese orden, `Nuevo cliente`/`Editar`/`Eliminar` siguen visibles y `Buscar` está habilitado con descripción `Buscar clientes`. Verificación: `ClientSearchTest.listadoInicial_conBuscarHabilitado` con `createComposeRule` → `./gradlew connectedAndroidTest`; aserción actualizada en `MainMenuTest.barraSuperior_estadoInicial`.
- [x] CA-2: Dado el destino `Clientes` activo, cuando se pulsa `Buscar`, entonces se muestra el campo `Buscar cliente` sin cambiar de destino y `Sincronizar` continúa deshabilitado. Verificación: `ClientSearchTest.buscar_muestraCampoYConservaDestino` → `connectedAndroidTest`.
- [x] CA-3: Dado los clientes del CA-1, cuando se escribe `ana` en `Buscar cliente`, entonces solo se muestra `Ana Pérez`. Verificación: `SearchClientsUseCaseTest.filtraPorNombre` → `./gradlew test`, y `ClientSearchTest.busquedaPorNombre_filtra` → `connectedAndroidTest`.
- [x] CA-4: Dado un cliente con email `ana.perez@correo.com` y otro sin ese texto, cuando se busca `CORREO.COM`, entonces se muestra el primero y no el segundo. Verificación: `SearchClientsUseCaseTest.filtraPorEmailIgnorandoMayusculas` → `./gradlew test`.
- [x] CA-5: Dado un cliente con teléfono `+51 999 000 111`, cuando se busca `999 000`, entonces se muestra ese cliente. Verificación: `SearchClientsUseCaseTest.filtraPorTelefono` → `./gradlew test`.
- [x] CA-6: Dado un cliente con dirección `Av. Lima 123`, cuando se busca `lima`, entonces se muestra ese cliente. Verificación: `SearchClientsUseCaseTest.filtraPorDireccion` → `./gradlew test`.
- [x] CA-7: Dada la consulta `zzz` sin coincidencias, cuando se aplica, entonces se muestra `Sin resultados`, no se muestra `Sin clientes` y `Nuevo cliente` sigue visible. Verificación: `ClientSearchTest.sinCoincidencias_muestraMensajeSinResultados` → `connectedAndroidTest`.
- [x] CA-8: Dada la consulta `ana` activa, cuando se pulsa `Limpiar`, entonces el campo queda vacío y vuelven a mostrarse los tres clientes del CA-1. Verificación: `ClientSearchTest.limpiar_restauraListado` → `connectedAndroidTest`.
- [x] CA-9: Dada la consulta `ana` activa, cuando se rota el dispositivo, entonces el campo conserva `ana` y el listado sigue filtrado. Verificación: `ClientSearchRotationTest.querySobreviveRotacion` con `ActivityScenario` → `connectedAndroidTest` en `KallpaBase_API37`.
- [x] CA-10: Dado `Ana Pérez` guardada con teléfono, email y dirección, cuando se pulsa `Ver ficha`, entonces se abre `Ficha de cliente` con los cuatro valores y las acciones `Cerrar`/`Editar`; al pulsar `Editar` se abre el formulario precargado y, tras guardar, el ID no cambia. Verificación: `ClientDetailTest.fichaMuestraDatosYAbreEdicion` → `connectedAndroidTest`, y regresión `UpdateClientUseCaseTest.mantieneId` → `./gradlew test`.
- [x] CA-11: Dado el cambio completo, cuando se ejecutan la puerta local y los instrumentados, entonces `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` termina en `BUILD SUCCESSFUL` y `./gradlew connectedAndroidTest` pasa con 0 fallos en `KallpaBase_API37`, incluidos los tests sin cambios de la spec 002. Verificación: salidas de ambos comandos registradas en `review.md` durante `/spec.verify`.

## Pruebas requeridas

- Unitarias: `SearchClientsUseCaseTest` con `FakeClientRepository` — coincidencias por nombre, email, teléfono y dirección, `ignoreCase`, consulta vacía, campos nulos/en blanco y orden conservado; más la regresión de `ClientValidatorTest`, `CreateClientUseCaseTest`, `UpdateClientUseCaseTest` y `DeleteClientUseCaseTest`. Ejecutar con `./gradlew test`.
- Compose UI: `ClientSearchTest`, `ClientDetailTest` y `ClientSearchRotationTest` con `createComposeRule` (API v2): campo y estado de `Buscar`, filtrado, `Sin resultados`, `Limpiar`, ficha y edición. Actualizar únicamente la aserción de `Buscar` en `MainMenuTest`. Ejecutar con `./gradlew connectedAndroidTest`.
- Instrumentadas: regresión de `ClientListTest`, `ClientFormTest`, `ClientEditTest`, `ClientDeleteTest`, `ClientRepositoryTest` y `ClientPersistenceTest` sin modificación, en el AVD `KallpaBase_API37` (Pixel 9 x86_64 / Google APIs).

## Riesgos y decisiones abiertas

- Ubicación del campo: se propone dentro de `ClientsScreen` bajo el título, no un modo búsqueda del `TopAppBar`; los CA están redactados para valer con cualquiera de las dos opciones. Decidir en `/spec.plan`.
- Normalización de la consulta: se fija `trim` + `ignoreCase`. Decidir en el plan si se ignoran también espacios y signos en teléfonos (`999000` frente a `999 000`); si se cambia, actualizar CA-5 antes de implementar.
- La ficha es de solo lectura con acceso a `Editar`; confirmar que no se requiere edición en sitio, lo que implicaría revisar la spec 002.
- El orden heredado `ORDER BY nombre ASC` de SQLite distingue mayúsculas/minúsculas. Decidir en el plan si se normaliza; cualquier cambio obliga a actualizar CA-1.
- La rotación se verifica con `ActivityScenario`; si el test resulta inestable, se reevaluará la estrategia en `/spec.plan` sin rebajar CA-9.
- Habilitar `Buscar` contradice temporalmente una aserción de la spec 003; debe ajustarse `MainMenuTest` y documentarse la regresión en `review.md` para mantener el CI en verde.
- La columna `Spec` de F-02 en `docs/ROADMAP.md` queda pendiente de sincronización: este paso crea solo la spec y su fila en el índice, sin tocar otros archivos.
