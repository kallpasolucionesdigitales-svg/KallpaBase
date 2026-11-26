# Plan técnico — 002

## Decisiones

- **Persistencia**: Room 2.8.5 con un DAO y una única base `KallpaDatabase`; `room-ktx` para operaciones `suspend` y `withContext(Dispatchers.IO)` en el repositorio. No se usa DataStore ni red. Room 2.8.5 exige `minSdk 23`; el proyecto declara 24, por lo que no hay conflicto.
- **Baja y unicidad**: baja física (`DELETE`), duplicados de nombre/email permitidos y teléfono como texto libre. CA-6 y CA-8 se cumplen sin filtros de registros "activos", por lo que la spec no necesita cambios.
- **Capas mínimas**: modelo de dominio `Client` separado de `ClientEntity` (con mappers), interfaz `ClientRepository` + `RoomClientRepository`, casos de uso `ListClientsUseCase` / `CreateClientUseCase` / `UpdateClientUseCase` / `DeleteClientUseCase` y un `ClientValidator` puro. Sin inyección de dependencias, sin Navigation y sin ViewModel/lifecycle: un state holder en Compose con `rememberCoroutineScope` es suficiente para el MVP.
- **Identificadores**: `CreateClientUseCase` genera `UUID.randomUUID().toString()`; `UpdateClientUseCase` conserva el ID recibido. Así CA-2 y CA-4 se prueban sin depender de la base de datos.
- **Punto de entrada (confirmado)**: `ClientsScreen` es la pantalla inicial de `MainActivity`. `CounterScreen` y `Greeting` se conservan como composables y sus tests directos (`CounterTest`, `GreetingTest`) siguen verdes; el contador deja de ser la pantalla de arranque y no se añade un botón de acceso a él en esta spec.
- **Sincronización futura**: no se añaden `updatedAt` ni ID remoto en esta spec; F-15 decidirá el esquema y la migración. El repositorio desacoplado es la única preparación necesaria ahora.
- **Versiones (resuelto)**: se declaran en `gradle/libs.versions.toml` Room 2.8.5, KSP 2.3.14 y Coroutines 1.11.0. La guía oficial de KSP empareja la serie 2.3.x con Kotlin 2.4.21; 2.3.14 es el último parche estable y corrige la regresión concurrente de 2.3.13.

## Archivos a crear o modificar

- `gradle/libs.versions.toml`: añadir `[versions]` `room = "2.8.5"`, `ksp = "2.3.14"`, `coroutines = "1.11.0"`; en `[libraries]` `androidx-room-runtime`, `androidx-room-ktx`, `androidx-room-compiler`, `kotlinx-coroutines-android`; en `[plugins]` `ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }`.
- `build.gradle.kts`: declarar `alias(libs.plugins.ksp) apply false` junto al resto de plugins.
- `app/build.gradle.kts`: aplicar `alias(libs.plugins.ksp)` y añadir `implementation(libs.androidx.room.runtime)`, `implementation(libs.androidx.room.ktx)`, `ksp(libs.androidx.room.compiler)` y `implementation(libs.kotlinx.coroutines.android)`. No se añaden plugins de Navigation ni Hilt.
- `app/src/main/java/com/kallpa/base/domain/Client.kt`: modelo inmutable con `id`, `nombre`, `telefono`, `email`, `direccion`.
- `app/src/main/java/com/kallpa/base/domain/ClientValidator.kt`: reglas `nombre.isBlank()` → `El nombre es obligatorio` y email no vacío sin `@` → `El email no es válido`; recorta espacios al guardar.
- `app/src/main/java/com/kallpa/base/domain/CreateClientUseCase.kt`, `UpdateClientUseCase.kt`, `DeleteClientUseCase.kt`, `ListClientsUseCase.kt`: operaciones sobre `ClientRepository`; el alta asigna el UUID y la edición conserva el ID.
- `app/src/main/java/com/kallpa/base/data/ClientRepository.kt`: interfaz con `list()`, `get(id)`, `save(client)`, `delete(id)`.
- `app/src/main/java/com/kallpa/base/data/RoomClientRepository.kt`: implementación con `withContext(Dispatchers.IO)` y mappers `Client` ↔ `ClientEntity`.
- `app/src/main/java/com/kallpa/base/data/local/ClientEntity.kt`, `ClientDao.kt`, `KallpaDatabase.kt`: entidad, consultas y base de datos (`version = 1`, sin migraciones todavía).
- `app/src/main/java/com/kallpa/base/ui/clients/ClientsUiState.kt`: `ClientsUiState` y `ClientFormState` con errores de validación.
- `app/src/main/java/com/kallpa/base/ui/clients/ClientsState.kt` y `RememberClientsState.kt`: state holder que carga la lista, aplica eventos y recarga tras mutar. El nombre `RememberClientsState.kt` cumple la regla `filename` de ktlint (PascalCase).
- `app/src/main/java/com/kallpa/base/ui/clients/ClientsScreen.kt`: lista, estado vacío `Sin clientes`, botón `Nuevo cliente`, diálogo de formulario y diálogo de confirmación de baja.
- `app/src/main/java/com/kallpa/base/MainActivity.kt`: construir la base/repositorio sin DI y mostrar `ClientsScreen()` como contenido inicial. `Greeting` y `CounterScreen` permanecen en el archivo sin cambios funcionales. `AndroidManifest.xml` no requiere cambios: no hay activities ni permisos nuevos.
- `app/src/test/java/com/kallpa/base/testing/FakeClientRepository.kt` y `app/src/androidTest/java/com/kallpa/base/testing/FakeClientRepository.kt`: repositorios en memoria para unitarios y Compose UI.
- `app/src/test/java/com/kallpa/base/domain/ClientValidatorTest.kt`, `CreateClientUseCaseTest.kt`, `UpdateClientUseCaseTest.kt`, `DeleteClientUseCaseTest.kt`: cubren CA-2, CA-3, CA-4 y CA-6 sin Android.
- `app/src/androidTest/java/com/kallpa/base/ui/ClientListTest.kt`, `ClientFormTest.kt`, `ClientEditTest.kt`, `ClientDeleteTest.kt`: cubren CA-1 a CA-6 con `createComposeRule` (API v2).
- `app/src/androidTest/java/com/kallpa/base/data/ClientRepositoryTest.kt` y `ClientPersistenceTest.kt`: cubren CA-6 a CA-8 contra Room real.
- `docs/specs/README.md`: estado de la fila 002 en `plan`.

## Estado y UI (Compose)

`ClientsUiState` contiene `clients: List<Client>`, `form: ClientFormState?` (`null` = cerrado), `editingId: String?`, `pendingDeleteId: String?` e `isLoading: Boolean`. `ClientFormState` guarda `nombre`, `telefono`, `email`, `direccion`, `nombreError` y `emailError`. El state holder expone eventos: `OnNewClick`, `OnEditClick(id)`, `OnDeleteClick(id)`, `OnFieldChange`, `OnSaveClick`, `OnCancelForm`, `OnConfirmDelete` y `OnCancelDelete`.

`ClientsScreen` usa Material 3: `Scaffold` con un `Button` visible `Nuevo cliente`, `LazyColumn` de tarjetas con `Nombre`, `Email`, `Teléfono` y `Dirección` cuando existan, `Text("Sin clientes")` cuando la lista esté vacía, `AlertDialog` para el formulario y otro `AlertDialog` para `Eliminar` → `Confirmar`/`Cancelar`. Los campos usan `OutlinedTextField` con `label`, de modo que las aserciones por texto de `createComposeRule` funcionen sin etiquetas de contenido adicionales. Todo el estado se crea con `remember` y se recarga desde el repositorio después de cada mutación, así que no se necesita `ViewModel`, `Navigation` ni `collectAsStateWithLifecycle`.

## Estrategia de tests

- **Unitarios** (`./gradlew test`): `ClientValidatorTest` verifica nombre vacío, email sin `@` y caso válido; `CreateClientUseCaseTest.guardaConId` comprueba ID no vacío; `UpdateClientUseCaseTest.mantieneId` comprueba que el ID no cambia; `DeleteClientUseCaseTest.bajaEliminaRegistro` comprueba que el repositorio falso queda sin el registro. Todos usan `FakeClientRepository` y JUnit4, sin dependencias de Android.
- **Compose UI** (`createComposeRule` v2, `./gradlew connectedAndroidTest`): `ClientListTest.listaVacia_muestraEstadoVacio`, `ClientFormTest.altaValida_actualizaLista`, `ClientFormTest.camposInvalidos_muestraErrores`, `ClientEditTest.edicion_actualizaLista`, `ClientDeleteTest.cancelar_noElimina` y `ClientDeleteTest.confirmar_eliminaDeLista`. Se inyecta el repositorio falso como parámetro del composable para no abrir la base real.
- **Instrumentados** (`./gradlew connectedAndroidTest` en `KallpaBase_API37`, Pixel 9 x86_64 / Google APIs): `ClientRepositoryTest.bajaEliminaRegistro` consulta el DAO real; `ClientPersistenceTest.altaSobreviveReinicio` y `bajaSobreviveReinicio` cierran `KallpaDatabase` y la reconstruyen apuntando al mismo fichero con `@Before`/`@After` de limpieza, de modo que la segunda instancia lee exactamente lo que persistió la primera; verifica CA-7 y CA-8 sin conectividad y sin depender de relanzar la `Activity`.
- **Puerta**: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y `connectedAndroidTest`. Las salidas se registran en `review.md` durante `/spec.verify`.

## Compatibilidad y riesgos

- `minSdk 24` es compatible con Room 2.8.5 (mínimo API 23); se valida en el primer `assembleDebug`.
- Resuelto: KSP 2.3.14 con Kotlin 2.4.21, AGP 9.5.2 y Gradle 9.9.2. La documentación oficial usa KSP 2.3.12 con Kotlin 2.4.21 y 2.3.14 es el parche estable posterior; no se requiere decisión adicional. Si el primer build detecta una incompatibilidad, se ajusta únicamente el valor de `ksp` en el catálogo, nunca un literal en Gradle.
- Resuelto: `ClientsScreen` sustituye al contador como pantalla de arranque. Como `CounterTest` y `GreetingTest` invocan sus composables directamente, siguen siendo válidos aunque ya no sean el contenido de `MainActivity`.
- Resuelto: el campo `- Estado` de `spec.md` queda sincronizado en `plan`, igual que el índice `docs/specs/README.md`.
- No se modela `updatedAt`/ID remoto: F-15 puede exigir una migración de esquema. Es un riesgo aceptado del enfoque local-first por fases.
