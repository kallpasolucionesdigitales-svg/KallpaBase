# Plan técnico — 004

## Decisiones

- **Solución mínima sin persistencia nueva**: filtrar en memoria el `List<Client>` que ya devuelve `ListClientsUseCase`; no se toca `ClientDao`, el esquema Room ni el orden `ORDER BY nombre ASC` heredado. No hace falta migración ni permisos nuevos.
- **Filtrado puro y testeable**: nuevo caso de uso `SearchClientsUseCase(clients, query): List<Client>`, sin repositorio ni Android. Aplica `trim()` + `lowercase(Locale.ROOT)` y busca subcadena en `nombre`, `email`, `telefono` y `direccion`; omite campos nulos/en blanco y conserva el orden de entrada. Cubre CA-3 a CA-6 en unitarios.
- **Campo siempre visible**: el campo `Buscar cliente` vive dentro de `ClientsScreen` bajo el título, con `rememberSaveable` para la consulta. No se crea un modo búsqueda del `TopAppBar`, con lo que `Nuevo cliente`, `Editar` y `Eliminar` siguen accesibles durante la búsqueda (CA-1, CA-7, CA-9).
- **Acción `Buscar` de la barra superior**: pasa de deshabilitada a habilitada solo en el destino `Clientes`, con `contentDescription = "Buscar clientes"`. Al pulsarla solicita foco al campo mediante un `FocusRequester`. Para no romper RNF-2 se conserva la firma pública `ClientsScreen(repository, modifier)` y se añade un overload `internal` con `searchRequest: Int`; los tests de la spec 002 siguen invocando la firma pública. `Sincronizar` y el overflow no cambian.
- **Estados vacíos diferenciados**: repositorio vacío + consulta vacía → `Sin clientes`; repositorio con datos + consulta sin coincidencias → `Sin resultados`. En ambos casos `Nuevo cliente` permanece visible (CA-7).
- **Ficha de solo lectura**: cada fila añade `Ver ficha`, que abre un `AlertDialog` `Ficha de cliente` con los cuatro campos y las acciones `Cerrar`/`Editar`. `Editar` cierra la ficha y reutiliza `onEditClick(id)`, por lo que el ID y las validaciones de la spec 002 no se modifican (CA-10).
- **Rotación**: la consulta se guarda con `rememberSaveable` en `ClientsScreen`. CA-9 se verifica con `ActivityScenario` sobre `MainActivity` y la base Room real sembrada previamente, sin activity de test ni manifest nuevo.
- **Regresión controlada de la spec 003**: de `MainMenuTest.barraSuperior_estadoInicial` solo se cambia la aserción de `Buscar` (descripción y `assertIsEnabled`); se mantienen las de `Sincronizar`, `Más opciones` y destinos deshabilitados. Ningún test de la spec 002 se toca.
- **Dependencias**: ninguna. `gradle/libs.versions.toml`, `build.gradle.kts` y `app/build.gradle.kts` quedan sin cambios; no se usa `Navigation Compose`, Hilt ni librerías de búsqueda.

## Archivos a crear o modificar

- `app/src/main/java/com/kallpa/base/domain/SearchClientsUseCase.kt`: **crear**. Función pura de filtrado por las cuatro propiedades de `Client`.
- `app/src/main/java/com/kallpa/base/ui/clients/ClientsUiState.kt`: **modificar**. Añadir `detailClientId: String? = null`; el resto de campos y `ClientFormState` quedan igual.
- `app/src/main/java/com/kallpa/base/ui/clients/ClientsState.kt`: **modificar**. Añadir `onViewClick(id)`, `onCloseDetail()` y `onEditFromDetail()` (cierra ficha y delega en `onEditClick(id)`). Sin cambios en alta/edición/baja.
- `app/src/main/java/com/kallpa/base/ui/clients/ClientsScreen.kt`: **modificar**. Consulta `rememberSaveable`, campo `OutlinedTextField(label = "Buscar cliente")`, botón `Limpiar` condicional, `Sin resultados`, botón `Ver ficha` por fila, diálogo `Ficha de cliente` y overload `internal` con `searchRequest` para el foco. Se mantiene la firma pública existente.
- `app/src/main/java/com/kallpa/base/ui/clients/RememberClientsState.kt`: **sin cambios**. El estado de búsqueda es responsabilidad del composable, no del state holder.
- `app/src/main/java/com/kallpa/base/ui/MainScreen.kt`: **modificar**. `Buscar` habilitado con descripción `Buscar clientes`, `searchRequest` local con `rememberSaveable` y paso al overload de `ClientsScreen`. El resto de acciones y destinos conservan su estado y semántica.
- `app/src/main/java/com/kallpa/base/data/ClientRepository.kt`, `RoomClientRepository.kt`, `data/local/*` y `MainActivity.kt`: **sin cambios**.
- `app/src/test/java/com/kallpa/base/domain/SearchClientsUseCaseTest.kt`: **crear**. CA-3 a CA-6, consulta vacía, campos nulos, `ignoreCase` y orden conservado, con `FakeClientRepository` solo como fuente de la lista.
- `app/src/androidTest/java/com/kallpa/base/ui/ClientSearchTest.kt`: **crear**. CA-1, CA-2, CA-3 (UI), CA-7 y CA-8 con `createComposeRule` (API v2) y el `FakeClientRepository` existente.
- `app/src/androidTest/java/com/kallpa/base/ui/ClientDetailTest.kt`: **crear**. CA-10 con `createComposeRule` (API v2).
- `app/src/androidTest/java/com/kallpa/base/ui/ClientSearchRotationTest.kt`: **crear**. CA-9 con `ActivityScenario<MainActivity>`, base real sembrada y limpieza en `@After`.
- `app/src/androidTest/java/com/kallpa/base/ui/MainMenuTest.kt`: **modificar** una sola aserción en `barraSuperior_estadoInicial` (`Buscar, no disponible` / `assertIsNotEnabled` → `Buscar clientes` / `assertIsEnabled`).
- `app/src/androidTest/java/com/kallpa/base/testing/FakeClientRepository.kt` y `app/src/test/java/com/kallpa/base/testing/FakeClientRepository.kt`: **sin cambios**, se reutilizan.
- `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`, `AndroidManifest.xml`: **sin cambios** (ninguna dependencia ni permiso nuevo).
- `docs/specs/README.md`: estado de la fila 004 en `plan`. `docs/ROADMAP.md` queda para el cierre de la spec, según lo registrado en `spec.md`.

## Estado y UI (Compose)

`ClientsUiState` mantiene `clients`, `form`, `editingId`, `pendingDeleteId` e `isLoading`, y añade `detailClientId`. La consulta no se duplica en ese estado: `ClientsScreen` la conserva con `var query by rememberSaveable { mutableStateOf("") }` y deriva `visibleClients = SearchClientsUseCase(state.uiState.clients, query)` con `remember(clients, query)`. Así la rotación conserva el texto sin recargar la lista y no se altera el state holder de la spec 002.

El overload interno recibe `searchRequest: Int`; un `LaunchedEffect(searchRequest)` llama a `focusRequester.requestFocus()` cuando el valor cambia. `ClientsContent` recibe `query`, `onQueryChange`, `visibleClients`, `onViewClick`, `onCloseDetail` y `onEditFromDetail` como parámetros explícitos, sin nuevos composables públicos.

Material 3: `OutlinedTextField` con `label = "Buscar cliente"` y semántica accesible, `Button("Limpiar")` solo con consulta no vacía, `LazyColumn` de tarjetas con `Ver ficha` junto a `Editar`/`Eliminar`, `Text("Sin resultados")` para búsquedas fallidas y `AlertDialog("Ficha de cliente")` con `Cerrar`/`Editar`. El diálogo existente de formulario y el de confirmación de baja no cambian.

## Estrategia de tests

- **Unitarios** (`./gradlew test`): `SearchClientsUseCaseTest` cubre CA-3 (nombre), CA-4 (email con mayúsculas), CA-5 (teléfono), CA-6 (dirección), consulta vacía, campos nulos/en blanco y conservación del orden; se ejecuta con JUnit4 sin Android. Regresión de `ClientValidatorTest`, `CreateClientUseCaseTest`, `UpdateClientUseCaseTest` (`mantieneId`, CA-10) y `DeleteClientUseCaseTest` sin modificación.
- **Compose UI** (`createComposeRule` v2, `./gradlew connectedAndroidTest`): `ClientSearchTest` verifica CA-1 (listado + `Buscar` habilitado), CA-2 (foco/campo sin cambio de destino), CA-3 (filtrado visible), CA-7 (`Sin resultados` vs `Sin clientes`) y CA-8 (`Limpiar`); `ClientDetailTest` verifica CA-10 (ficha y apertura del formulario precargado). Se inyecta `FakeClientRepository` para no abrir la base real.
- **Instrumentados** (`./gradlew connectedAndroidTest` en `KallpaBase_API37`, Pixel 9 x86_64 / Google APIs): `ClientSearchRotationTest` siembra tres clientes en `KallpaDatabase` mediante `RoomClientRepository`, lanza `MainActivity` con `ActivityScenario`, escribe `ana`, cambia `requestedOrientation` y comprueba que el campo y el filtro sobreviven; limpia la base en `@After`. Regresión sin cambios de `ClientListTest`, `ClientFormTest`, `ClientEditTest`, `ClientDeleteTest`, `ClientRepositoryTest` y `ClientPersistenceTest`.
- **Puerta**: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y `connectedAndroidTest`, con salidas registradas en `review.md` durante `/spec.verify` (CA-11).

## Compatibilidad y riesgos

- `minSdk 24`: `rememberSaveable`, `FocusRequester` y `AlertDialog` ya están disponibles con las versiones de Compose del proyecto; no hay permisos ni servicios nuevos.
- Navegación: no se añade `Navigation Compose` ni deep links; `MainScreen` solo cambia el estado de una acción existente. La regla de habilitación de la spec 003 se cumple porque F-02 entra con spec en estado `plan` y se habilitará en el PR tras verificación.
- Datos: sin migración; el orden `ORDER BY nombre ASC` se conserva tal cual (SQLite distingue mayúsculas, decisión heredada de la spec 002). Cualquier normalización del orden exigiría actualizar CA-1 antes de implementar.
- CI: se añaden 1 test unitario y 3 clases Compose/instrumentadas, y se ajusta 1 aserción de `MainMenuTest`. El coste de `connectedAndroidTest` aumenta; la puerta debe seguir en verde en ambos jobs.
- Riesgo técnico: `ActivityScenario` + rotación puede ser sensible al timing; se usa `waitForIdle()` y limpieza explícita. Si el test resulta inestable, se reevaluará la estrategia en `/spec.tasks` o durante la implementación sin rebajar CA-9, actualizando la spec si hace falta.
- Riesgo de regresión: tocar `MainScreen` y `ClientsScreen` puede afectar a los tests de la spec 002; por eso la firma pública de `ClientsScreen` no cambia y esos tests no se editan.

### Decisiones que requieren confirmación humana

- **Normalización de teléfonos**: se decide no ignorar espacios ni signos; CA-5 busca literalmente `999 000`. Si se quiere que `999000` también coincida, confirmar y actualizar CA-5 antes de `/spec.tasks`.
- **Ficha**: se decide de solo lectura con botón `Editar`. Si se requiere edición en sitio, hay que revisar la spec 002 antes de implementar.
- **Orden**: se decide conservar el orden actual sensible a mayúsculas. Confirmar si se prefiere orden alfabético normalizado; en ese caso CA-1 debe cambiar.
