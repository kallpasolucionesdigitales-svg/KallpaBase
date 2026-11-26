# Plan técnico — 003

## Decisiones

- Crear un composable raíz `MainScreen(repository: ClientRepository)` en `app/src/main/java/com/kallpa/base/ui/MainScreen.kt`. `MainActivity` pasa el repositorio de Room existente y no construye la UI de clients directamente.
- Mantener el destino activo con `var selectedDestination by rememberSaveable { mutableStateOf(MainDestination.CLIENTS) }`; así se conserva al rotar (CA-8) sin `Navigation Compose`, deep links ni back stack propio.
- Modelar los destinos en un `enum class MainDestination` con `title`, `enabled` e `indicator` (`Próximamente` / `Fase 2`). Solo `Clientes` está habilitado porque la spec 002 está cerrada; los pulsos sobre los demás destinos son no-op y el `NavigationBarItem` usa `enabled = false`.
- Usar `Scaffold` con `TopAppBar` y `NavigationBar` de Material 3. El título de la barra superior se deriva del destino activo; las acciones `Buscar` y `Sincronizar` son `TextButton` deshabilitados y el overflow es un `DropdownMenuItem` deshabilitado por entrada.
- Exponer semántica explícita con `Modifier.semantics(mergeDescendants = true) { contentDescription = ... }`: `Clientes, disponible`, `Presupuestos, no disponible, Próximamente`, `Operaciones, no disponible, Fase 2`, `Configuración, no disponible, Próximamente`; para las acciones, `Buscar, no disponible`, `Sincronizar, no disponible` y `<nombre>, no disponible`.
- Pasar `Modifier.padding(paddingValues)` del `Scaffold` raíz a `ClientsScreen` a través de su parámetro `modifier` existente: no cambia su API y mantiene el FAB `Nuevo cliente` dentro de la pantalla de clientes.
- No añadir dependencias; `material3`, `ui`, `activity-compose` y `ui-test-junit4` ya están en el catálogo. No tocar `gradle/libs.versions.toml` ni `app/build.gradle.kts`.

## Archivos a crear o modificar

- `app/src/main/java/com/kallpa/base/ui/MainScreen.kt` (nuevo): `MainDestination`, `MainScreen`, `MainTopBar` y `MainNavigationBar`.
- `app/src/main/java/com/kallpa/base/MainActivity.kt`: envolver `ClientsScreen` con `MainScreen(repository = repository)`; conservar `CounterScreen`, `Greeting` y el preview de 001.
- `app/src/androidTest/java/com/kallpa/base/ui/MainMenuTest.kt` (nuevo): cuatro tests con `createComposeRule` (API v2) que cubren CA-1 a CA-4 y la semántica accesible.
- `docs/specs/003-menu-principal/`: `spec.md`, `plan.md`, `tasks.md` y `review.md`.
- `docs/specs/README.md` y `docs/ROADMAP.md`: estados de la spec y de F-23.
- `gradle/libs.versions.toml`: sin cambios.

## Estado y UI (Compose)

Jerarquía: `MaterialTheme → MainScreen → Scaffold(topBar = MainTopBar, bottomBar = MainNavigationBar) → when (selectedDestination) { CLIENTS -> ClientsScreen(repository, Modifier.padding(paddingValues)) ... }`. Los destinos futuros no tienen composable propio: se renderizan solo como ítems deshabilitados en la barra inferior, por lo que el contenido sigue siendo `ClientsScreen` cuando se pulsan.

`MainTopBar` muestra `Text(selectedDestination.title)` y tres controles deshabilitados: `Buscar`, `Sincronizar` y `Más opciones`; este último abre un `DropdownMenu` con `Reportes`, `Exportar` y `Acerca de`, también deshabilitados. `MainNavigationBar` usa `NavigationBarItem(selected = dest == selectedDestination, enabled = dest.enabled, onClick = { if (dest.enabled) onSelect(dest) }, icon = ..., label = ...)`; el `label` incluye el título y, cuando aplica, el indicador de disponibilidad.

## Estrategia de tests

- Unitarias: no aplica; no hay lógica de negocio nueva.
- Compose UI (`MainMenuTest`, `createComposeRule` de API v2, `FakeClientRepository` existente):
  - `menuInicial_muestraCuatroDestinos`: CA-1, cuatro `contentDescription` visibles, `Clientes` seleccionado y `Sin clientes` de `ClientsScreen` presente.
  - `destinoDeshabilitado_noNavega`: CA-2, `performTouchInput { click() }` sobre un destino deshabilitado y se comprueba que `Clientes` sigue seleccionado y que no aparece ninguna otra pantalla.
  - `destinosFuturos_visiblesYDeshabilitados`: CA-3, indicadores `Próximamente` / `Fase 2` visibles y `assertIsNotEnabled()` en cada destino futuro.
  - `barraSuperior_estadoInicial`: CA-4, título `Clientes`, acciones `Buscar`/`Sincronizar` deshabilitadas con su `contentDescription` y menú overflow con tres entradas deshabilitadas.
- Instrumentados: `./gradlew connectedAndroidTest` en `KallpaBase_API37`, incluyendo sin cambios `ClientListTest`, `ClientFormTest`, `ClientEditTest`, `ClientDeleteTest`, `CounterTest` y `GreetingTest` (CA-5).
- Puerta local: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` (CA-6).

## Compatibilidad y riesgos

- `minSdk 24`: solo APIs de Compose y Material 3; sin permisos nuevos.
- `ClientsScreen` tiene su propio `Scaffold`; el `Scaffold` raíz aporta la barra superior/inferior y el interior aporta el FAB de clientes. Los paddings se encadenan con `Modifier.padding` y no se modifica la API de `ClientsScreen`.
- RF-8 se cumple estructuralmente con `rememberSaveable`, pero no es ejercitable con un solo destino habilitado: no existe forma de cambiar la selección para compararla antes y después de rotar. Se documentará como limitación hasta que se habilite un segundo destino.
- Los ítems deshabilitados no responden a pulsaciones; el test CA-2 usa `performTouchInput { click() }` porque `performClick()` exige que el nodo tenga acción de clic habilitada.
- La semántica de los destinos usa `mergeDescendants = true`, por lo que los tests localizan los ítems por `contentDescription` y no por texto parcial.
- Riesgo de estilo: imports explícitos, nombres de archivo PascalCase y salto de línea final para ktlint/detekt.
