# Plan técnico — 001

## Decisiones

- Estado local con `remember { mutableIntStateOf(0) }` dentro de un composable `CounterScreen`; no se introduce ViewModel porque no hay lógica de negocio ni estado compartido.
- Se conserva `Greeting` y su test actual; `CounterScreen` lo envuelve junto al contador para no romper `GreetingTest`.
- No se añaden dependencias: ya están disponibles `material3`, `ui`, `activity-compose` y `ui-test-junit4` en el catálogo.

## Archivos a crear o modificar

- `app/src/main/java/com/kallpa/base/MainActivity.kt`: `setContent` invoca `CounterScreen()`; se añade el composable con `Column`, `Greeting`, `Text` y `Button`. El preview `GreetingPreview` se sustituye por `CounterScreenPreview`, que renderiza `CounterScreen()`.
- `app/src/androidTest/java/com/kallpa/base/CounterTest.kt`: tres tests Compose (estado inicial, una pulsación, tres pulsaciones).
- `docs/specs/001-contador-compose/`: artefactos de la spec.
- `gradle/libs.versions.toml`: sin cambios.

## Estado y UI (Compose)

`CounterScreen` mantiene `count: Int` con `mutableIntStateOf`. La jerarquía es `Surface → Column(horizontalAlignment = CenterHorizontally, verticalArrangement = Arranged with spacedBy)` → `Greeting("KallpaBase")` → `Text("Clics: $count")` → `Button(onClick = { count++ }) { Text("Incrementar") }`. Se usa `MaterialTheme` en `MainActivity` y en el test.

## Estrategia de tests

- `GreetingTest` existente se mantiene sin cambios (regresión del saludo).
- `CounterTest` usa `createComposeRule()` de la API v2, `setContent { MaterialTheme { CounterScreen() } }`, `onNodeWithText(...).assertIsDisplayed()` y `performClick()`.
- Puerta local: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug`.
- Instrumentación: `./gradlew connectedAndroidTest` con el AVD `KallpaBase_API37`.

## Compatibilidad y riesgos

- `minSdk 24`: las APIs usadas son de Compose y no requieren permisos ni características de API superior.
- Riesgo de estilo: imports explícitos (no comodín) y final newline para pasar ktlint.
- Sin impacto en release, keystore ni workflow de CI.
