# Revisión — 003 Menú principal

- Rama: `feature/003-menu-principal`.
- Base revisada: `42bbe4d`; commits en la rama: `0e9e9bf docs: sync spec statuses after PR 12` y `96c3815 feat: add main navigation shell (spec 003)`.
- Estado al cierre: `cerrado` en `spec.md`, `docs/specs/README.md` y `docs/ROADMAP.md` (F-23); PR #13 mergeado en `main` (`0f9e5cc`) con CI en verde.
- Fecha: 2026-11-26.
- Alcance: `spec.md`, `plan.md`, `tasks.md`, `MainScreen.kt`, `MainActivity.kt`, `MainMenuTest.kt`, índice de specs, roadmap y evidencia de Gradle aportada por la sesión solicitante.
- Limitación del revisor: no se ejecutó Gradle ni se hizo `push`; las salidas de Gradle se aceptan como evidencia real ya obtenida y se citan, no se reproducen.

## Hallazgos por severidad

### Bloqueante

Ninguno. No se encontró ningún defecto funcional que impida cumplir CA-1…CA-6 con la evidencia disponible.

### Alto

- **A-1 — Resuelto: la implementación ya está incluida en el commit `96c3815`.** `app/src/main/java/com/kallpa/base/ui/MainScreen.kt:1`, `app/src/androidTest/java/com/kallpa/base/ui/MainMenuTest.kt:1`, `app/src/main/java/com/kallpa/base/MainActivity.kt:42`, `docs/specs/003-menu-principal/plan.md:1`, `docs/specs/003-menu-principal/tasks.md:1`. `git log 42bbe4d..HEAD` contiene solo el commit de saneamiento documental posterior al PR #12; `git diff 42bbe4d` no muestra `MainScreen.kt`, `MainMenuTest.kt`, `plan.md` ni `tasks.md` porque son archivos `??`, y el cambio de `MainActivity.kt` está sin commitear (`M`). Si se abre el PR desde el HEAD actual, el shell de navegación, sus cuatro tests y los artefactos del ciclo SDD quedan fuera del diff remoto. **Recomendación:** antes del PR, `git add` de los cinco archivos nuevos/modificados y commit con prefijo convencional (`feat: add main navigation shell`; el saneamiento de specs puede ir en un `docs:` separado); repetir la puerta local y dejar que el CI ejecute sobre el commit final. No hacer `push` sin confirmación humana. **Resolución:** se hizo `git add` explícito de los archivos de código, tests y artefactos y se creó el commit `96c3815 feat: add main navigation shell (spec 003)`; `git log 42bbe4d..HEAD` ya incluye la implementación. No se ha hecho `push`.

### Medio

- **M-1 — Resuelto: alcance documental aclarado en la spec 003.** `docs/specs/003-menu-principal/spec.md:27` prohíbe «modificar la spec 002 ni sus tests instrumentados», pero el commit `0e9e9bf` de esta rama cambia `docs/specs/002-gestion-clientes/spec.md:3-4` (estado `verificado` → `cerrado`, enlace al PR #12) y `docs/specs/002-gestion-clientes/review.md:39-48`. El cambio es coherente con el cierre del PR #12 y no toca los tests, pero contradice literalmente el alcance declarado de la spec 003. **Recomendación:** separar ese saneamiento en un commit `docs:` propio y mencionarlo en la descripción del PR, o ajustar el no-objetivo de la spec 003 para permitir expresamente la sincronización de estado de specs ya cerradas. Mantener sin cambios los tests instrumentados de 002. **Resolución:** el no-objetivo de la spec 003 se ajustó para permitir expresamente la sincronización de estados de specs cerradas, realizada en el commit `docs:` separado `0e9e9bf`; los tests instrumentados de 002 siguen sin modificaciones.

### Bajo

- **B-1 — Resuelto: CA-1 se ajustó a la verificación real del composable raíz.** `app/src/androidTest/java/com/kallpa/base/ui/MainMenuTest.kt:27-31` monta `MainScreen` con `FakeClientRepository`, mientras que `docs/specs/003-menu-principal/spec.md:50` describe el escenario «cuando se muestra `MainActivity`». El cableado real en `app/src/main/java/com/kallpa/base/MainActivity.kt:42` se cubre solo por revisión de código y por `assembleDebug`, no por un test que lance la Activity (es coherente con `plan.md:31-35`). **Recomendación:** añadir, si se quiere cobertura de integración, un test de humo con `ActivityScenario<MainActivity>` que aserte los cuatro destinos; mientras tanto, dejar explícito en el PR que CA-1 se verifica a nivel de composable raíz. **Resolución:** CA-1 ahora describe «cuando se muestra `MainScreen` (composable raíz invocado por `MainActivity`)» y aclara que el cableado de la Activity se cubre por revisión y `assembleDebug`; no se añade el test de humo para no ampliar el alcance de esta spec.
- **B-2 — Iconos iniciales ambiguos en la barra inferior.** `app/src/main/java/com/kallpa/base/ui/MainScreen.kt:154-157` deriva el icono de `destination.title.first()`, por lo que `Clientes` y `Configuración` muestran ambos la letra «C». No incumple RF-4 (el indicador `Próximamente` distingue al segundo), pero reduce la claridad visual entre un destino habilitado y otro deshabilitado. **Recomendación:** usar glifos/Material Icons distintos o iniciales compuestas («Cl»/«Co») en una iteración de UI; no requiere cambiar la semántica ya testeada.

## Limitaciones documentadas (no son hallazgos bloqueantes)

- **RF-8, conservación de selección al rotar:** `docs/specs/003-menu-principal/spec.md:38` y `app/src/main/java/com/kallpa/base/ui/MainScreen.kt:67` usan correctamente `rememberSaveable`, pero con un único destino habilitado no existe forma de cambiar la selección y compararla antes/después de una rotación. `docs/specs/003-menu-principal/plan.md:43` ya lo registra. **Recomendación:** cuando se habilite un segundo destino, añadir un test de restauración de estado (por ejemplo con `ActivityScenario` y cambio de configuración) antes de dar por ejercitado RF-8.
- **CA-2, ausencia de navegación positiva:** `app/src/androidTest/java/com/kallpa/base/ui/MainMenuTest.kt:46-65` usa `performTouchInput { click() }` sobre nodos deshabilitados (justificado en `plan.md:44`) y comprueba que `Clientes` sigue seleccionado. Como no hay otro destino habilitado, el test demuestra la ausencia de cambio, pero no que la selección funcione cuando un destino pase a estar habilitado. **Recomendación:** añadir un test de selección positiva en la spec que habilite `Presupuestos`.

## Cobertura de criterios de aceptación

| Criterio | Estado | Evidencia |
| --- | --- | --- |
| CA-1 | Verificado (con B-1) | `MainMenuTest.menuInicial_muestraCuatroDestinos` (`MainMenuTest.kt:24-43`): cuatro `contentDescription` visibles, `Clientes` seleccionado y texto `Sin clientes` de `ClientsScreen`. Cableado en `MainActivity.kt:42`; `connectedAndroidTest` en verde (18 instrumentados). |
| CA-2 | Verificado (con limitación) | `MainMenuTest.destinoDeshabilitado_noNavega` (`MainMenuTest.kt:46-65`): toque raw sobre los tres destinos futuros, `Clientes` sigue seleccionado y se mantiene `Sin clientes`. Guard estructural en `MainScreen.kt:151-152` (`enabled = false` + `onClick` condicionado). |
| CA-3 | Verificado | `MainMenuTest.destinosFuturos_visiblesYDeshabilitados` (`MainMenuTest.kt:68-86`): `assertIsDisplayed()` + `assertIsNotEnabled()` para los tres destinos, dos apariciones de `Próximamente` y una de `Fase 2`; semántica definida en `MainScreen.kt:45-62,168-170`. |
| CA-4 | Verificado | `MainMenuTest.barraSuperior_estadoInicial` (`MainMenuTest.kt:89-116`): título `Título, Clientes`, `Buscar`/`Sincronizar` deshabilitados con su `contentDescription`, overflow abierto con `Reportes`, `Exportar` y `Acerca de` deshabilitados; implementación en `MainScreen.kt:93-140`. |
| CA-5 | Verificado | `./gradlew --no-daemon connectedAndroidTest` en `KallpaBase_API37` → `BUILD SUCCESSFUL`, 18 tests instrumentados (14 previos + 4 nuevos), 0 fallos. `git status` confirma que `ClientListTest`, `ClientFormTest`, `ClientEditTest`, `ClientDeleteTest`, `CounterTest` y `GreetingTest` no se modificaron; `ClientsScreen` conserva su API (`ClientsScreen.kt:27`) y solo recibe `Modifier.padding` (`MainScreen.kt:80-83`). |
| CA-6 | Verificado | `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` → `BUILD SUCCESSFUL`, 6 tests unitarios, 0 fallos; `git diff --check` sin salida. Los comandos coinciden con el job `build` de `.github/workflows/android.yml:45`; el job instrumentado usa la misma puerta en `android.yml:99-109`. CI remoto del PR #13 en verde (ambos jobs). |

## Comandos ejecutados

Por el revisor (sin Gradle, sin push, sin modificar código de producto):

- `git status --short` y `git status --porcelain=v1 -uall`
- `git log --oneline 42bbe4d..HEAD`
- `git diff 42bbe4d`, `git diff --stat 42bbe4d`, `git diff --name-only 42bbe4d`
- `git diff --cached --name-only` (sin cambios staged)
- `git diff --check` (limpio)
- Lectura de `spec.md`, `plan.md`, `tasks.md`, `MainScreen.kt`, `MainActivity.kt`, `MainMenuTest.kt`, `ClientsScreen.kt`, `FakeClientRepository.kt`, `app/build.gradle.kts`, `gradle/libs.versions.toml`, `.github/workflows/android.yml`, `docs/ROADMAP.md`, `docs/specs/README.md` y `docs/OPENCODE_SDD.md`
- Búsqueda de patrones de secretos (`password|secret|apiKey|api_key|keystore|token`) en `app/src/**/*.kt`: sin coincidencias

Evidencia real aportada, no reejecutada por este revisor:

| Comando | Resultado |
| --- | --- |
| `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` | `BUILD SUCCESSFUL` — 6 tests unitarios, 0 fallos; ktlint, detekt y lint sin hallazgos; APK debug generado. |
| `./gradlew --no-daemon connectedAndroidTest` en `KallpaBase_API37` | `BUILD SUCCESSFUL` — 18 tests instrumentados, 0 fallos. |

CI ejecutado en el PR #13 (head `4e29eb2`) y en el merge `0f9e5cc`: ambos jobs en `success`.

## Convenciones, dependencias y artefactos

- **Version catalog:** sin cambios en `gradle/libs.versions.toml` ni en `app/build.gradle.kts` (`git diff 42bbe4d --name-only` y `git status` no los listan); RNF-2 cumplido. `createComposeRule` v2, `material3` y `ui-test-junit4` ya están disponibles (`libs.versions.toml:18-23`, `app/build.gradle.kts:66-80`).
- **AGENTS.md:** Compose con Material 3, tests con `createComposeRule` (API v2), sin dependencias nuevas, sin `build/`, `.gradle/`, `local.properties` ni keystore versionados. No se agregó `Navigation Compose`, deep links ni back stack propio (no-objetivo de `spec.md:25`).
- **Compatibilidad CI:** los comandos locales son idénticos a los del workflow (`.github/workflows/android.yml:45,99-109`); `minSdk 24` no se ve afectado; no hay permisos ni recursos nuevos.
- **Secretos/artefactos:** `git status --porcelain=v1 -uall` lista únicamente los nueve archivos esperados (código, tests y documentación); no aparecen outputs de build, APK, `local.properties` ni credenciales. La búsqueda de secretos en Kotlin no dio resultados.

## Conclusión

Los seis criterios CA-1…CA-6 tienen evidencia local en verde y la implementación cumple RF-1…RF-7 y RNF-1…RNF-5; no hay hallazgos bloqueantes. **A-1**, **M-1** y **B-1** quedan resueltos con el commit `96c3815`, el commit `docs:` separado `0e9e9bf` y el ajuste de CA-1 a la verificación del composable raíz. Queda como seguimiento no bloqueante **B-2** (iniciales ambiguas `C` para `Clientes` y `Configuración`), además de las limitaciones ya documentadas de RF-8 y CA-2, que se cubrirán al habilitar un segundo destino. Con el PR #13 mergeado y el CI en verde, el estado pasó a `cerrado`. No se ha hecho `push` desde esta sesión de cierre documental.
