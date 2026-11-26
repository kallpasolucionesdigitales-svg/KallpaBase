# Revisión — 001 Contador Compose en MainActivity

- Rama: `feature/001-contador-compose`
- Commit base: `794bb5f`
- Fecha: 2026-11-24
- Alcance: `spec.md`, `plan.md`, `tasks.md`, `git status` y `git diff 794bb5f` (incluidos archivos sin seguimiento)

## Cobertura de criterios

| Criterio | Estado | Evidencia |
| --- | --- | --- |
| CA-1 (estado inicial) | Cubierto | `app/src/androidTest/java/com/kallpa/base/CounterTest.kt:16-26` comprueba saludo, `Clics: 0` y botón. |
| CA-2 (una pulsación) | Cubierto | `app/src/androidTest/java/com/kallpa/base/CounterTest.kt:29-38`. |
| CA-3 (tres pulsaciones) | Cubierto | `app/src/androidTest/java/com/kallpa/base/CounterTest.kt:41-52`. |
| CA-4 (puerta local) | No verificado | `docs/specs/001-contador-compose/spec.md:40` y `docs/specs/001-contador-compose/tasks.md:7` siguen desmarcados; ver hallazgo A-1. |

Comprobaciones sin hallazgos: `GreetingTest` permanece intacto (T3), `gradle/libs.versions.toml` no se modifica (conforme al no-objetivo), se usa Material 3 y `createComposeRule` de API v2 (`CounterTest.kt:5`), el estado usa `remember` (`MainActivity.kt:39`), los imports son explícitos, `git diff --check` no reporta whitespace y no hay secretos ni archivos generados (`build/`, `.gradle/`, `local.properties` o keystore) en el cambio.

## Hallazgos

### Bloqueante

- Sin hallazgos.

### Alto

- **A-1 — CA-4 y T5 sin ejecutar ni marcar (RNF-3 no demostrada).**
  - `docs/specs/001-contador-compose/spec.md:40`, `docs/specs/001-contador-compose/tasks.md:7`.
  - La spec exige `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` en verde y el índice (`docs/specs/README.md:12`) impide cerrar la spec sin esa puerta y sin CI verde. Ambos checkboxes están sin marcar, y esta revisión no pudo ejecutar Gradle por restricción de permisos del entorno, así que no hay evidencia de `BUILD SUCCESSFUL`.
  - Recomendación concreta: ejecutar la puerta local y `./gradlew --no-daemon connectedAndroidTest` con el AVD `KallpaBase_API37`, pegar el resultado en `spec.md` y marcar `- [x] CA-4` (spec.md:40) y `- [x] T5` (tasks.md:7) antes de pasar la spec a `verificado`.

- **A-2 — Los cambios no están commiteados y los archivos nuevos no tienen seguimiento.**
  - `git log 794bb5f..HEAD` está vacío; `git status --porcelain` muestra `?? app/src/androidTest/java/com/kallpa/base/CounterTest.kt:1` y `?? docs/specs/001-contador-compose/spec.md:1` (junto con `plan.md`, `tasks.md` y este `review.md`).
  - Un PR abierto desde el HEAD actual quedaría vacío: `CounterTest.kt` no llegaría a CI, por lo que CA-1 a CA-3 no se ejecutarían en el workflow (`.github/workflows/android.yml:99-109`) aunque localmente pasen.
  - Recomendación concreta: `git add` de los cinco archivos implicados (`MainActivity.kt`, `docs/specs/README.md`, `CounterTest.kt` y los artefactos de la spec, incluido `review.md`) y crear un commit con prefijo convencional, p. ej. `feat: add compose counter with instrumented tests`; después verificar que el PR lista `CounterTest.kt` y que el job `instrumented-test` lo ejecuta.

### Medio

- Sin hallazgos.

### Bajo

- **B-1 — El rename del preview no está reflejado en `plan.md`.**
  - `app/src/main/java/com/kallpa/base/MainActivity.kt:63` renombra `GreetingPreview` a `CounterScreenPreview`, pero `docs/specs/001-contador-compose/plan.md:9-14` solo describe la creación de `CounterScreen` y no menciona este cambio. Es funcionalmente correcto y no rompe ningún test, pero el plan queda desactualizado como fuente de seguimiento.
  - Recomendación concreta: añadir una línea en `plan.md:11` indicando que `GreetingPreview` se sustituye por `CounterScreenPreview` que renderiza `CounterScreen()`.

## Conclusión

La implementación cubre correctamente CA-1 a CA-3 y respeta las convenciones revisadas; no se encontraron defectos de código ni secretos/archivos generados. Los tres hallazgos quedaron resueltos: A-1 con verificación local, B-1 documentado en `plan.md` y A-2 con el commit `5c4d4b6` incluido en el PR #11. CI terminó en verde. La spec se cierra sin hallazgos bloqueantes.

## Seguimiento del implementador (2026-11-24 23:04 -03; cierre 2026-11-26)

- **A-1 resuelto:** el agente principal ejecutó `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` → `BUILD SUCCESSFUL` en 1m 43s (62 tareas). El subagente `android-verifier` ejecutó además `./gradlew connectedAndroidTest` → `BUILD SUCCESSFUL` en 49s con 4 tests, 0 fallos y 0 omitidos en `KallpaBase_API37`. Los cuatro criterios CA-1 a CA-4 están marcados en `spec.md` y T5 en `tasks.md`.
- **A-2 resuelto:** commit `5c4d4b6` (`feat: add compose counter with instrumented tests`), pusheado y abierto como [PR #11](https://github.com/kallpasolucionesdigitales-svg/KallpaBase/pull/11). Incluye `CounterTest.kt` y los artefactos de la spec.
- **B-1 resuelto:** `plan.md` ya documenta el rename `GreetingPreview` → `CounterScreenPreview`.
- **Nota del verificador:** `ktlintCheck`, `detekt`, `test` y `assembleDebug` aparecieron UP-TO-DATE en su corrida; el resultado es válido sobre los outputs de la ejecución previa del implementador.
- **CI (RNF-3) verificado:** run [`45818074320`](https://github.com/kallpasolucionesdigitales-svg/KallpaBase/actions/runs/45818074320) sobre `5c4d4b6` → ambos jobs en verde: "Build y checks estáticos" (1m28s) y "Tests instrumentados (API 37)" (3m56s). Se lanzó con `workflow_dispatch` porque el cambio de base del PR a `main` no dispara el evento `pull_request` por defecto.
