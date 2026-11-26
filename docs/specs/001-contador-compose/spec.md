# 001 — Contador Compose en MainActivity

- Estado: cerrado
- Rama: `feature/001-contador-compose`
- PR: https://github.com/kallpasolucionesdigitales-svg/KallpaBase/pull/11
- Fecha: 2026-11-24 (cerrada el 2026-11-26)

## Contexto

La app base solo muestra un saludo estático (`Greeting`). Se necesita una feature mínima que valide de extremo a extremo el flujo Spec-Driven Development recién configurado: agentes, comandos, subagentes de verificación y revisión, y la puerta de calidad existente.

## Objetivos

- Añadir un contador interactivo junto al saludo sin introducir dependencias nuevas.
- Cubrir el comportamiento con tests Compose instrumentados.
- Verificar que el ciclo SDD completo funciona en este repositorio.

## No-objetivos

- No persistir el contador entre rotaciones o reinicios de proceso.
- No añadir ViewModel, navegación, temas ni almacenamiento.
- No modificar la firma release, CI ni el version catalog.

## Requisitos funcionales

- RF-1: La pantalla muestra el saludo existente `Hola, KallpaBase!`.
- RF-2: La pantalla muestra un botón con la etiqueta `Incrementar`.
- RF-3: La pantalla muestra `Clics: N`, donde `N` empieza en 0 y aumenta en 1 con cada pulsación.

## Requisitos no funcionales

- RNF-1: Usar Material 3 y Compose con las dependencias ya declaradas en `gradle/libs.versions.toml`.
- RNF-2: El estado debe sobrevivir a recomposiciones (`remember`).
- RNF-3: `ktlintCheck`, `detekt`, `lint`, `test` y `assembleDebug` deben pasar; el CI del PR debe quedar verde.

## Criterios de aceptación

- [x] CA-1: Dado el estado inicial, cuando se muestra `MainActivity`, entonces se ven `Hola, KallpaBase!` y `Clics: 0`. Verificación: `CounterTest.initial_state_isDisplayed` → PASS (3.817s).
- [x] CA-2: Dado `Clics: 0`, cuando se pulsa `Incrementar`, entonces se ve `Clics: 1`. Verificación: `CounterTest.click_increments_count` → PASS (3.835s).
- [x] CA-3: Dado el estado inicial, cuando se pulsa `Incrementar` tres veces, entonces se ve `Clics: 3`. Verificación: `CounterTest.multiple_clicks_update_count` → PASS (5.057s).
- [x] CA-4: Dado el cambio completo, cuando se ejecuta la puerta local, entonces termina en `BUILD SUCCESSFUL`. Verificación: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` → ejecutado el 2026-11-24 23:03 -03, `BUILD SUCCESSFUL` en 1m 43s (62 tareas).

## Pruebas requeridas

- Unitarias: no aplica (no hay lógica pura nueva).
- Compose UI: `app/src/androidTest/java/com/kallpa/base/CounterTest.kt` con `createComposeRule` (API v2).
- Instrumentadas: los tests Compose anteriores se ejecutan con `connectedAndroidTest` en el AVD `KallpaBase_API37`.

## Riesgos y decisiones abiertas

- Convención de estilo: los nombres de tests usan guiones bajos (ya permitido por detekt y ktlint en este repo).
- No se requiere aprobación humana adicional; el alcance está cubierto por los criterios anteriores.
