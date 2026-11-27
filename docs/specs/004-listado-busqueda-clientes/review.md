# Revisión — 004 Listado y búsqueda de clientes

- Rama: `feature/004-listado-busqueda-clientes`.
- Base: `ab9c4e4` (HEAD local al cierre). Sin commit ni `push` desde esta sesión.
- Fecha: 2026-11-27.
- Alcance: `SearchClientsUseCase.kt`, `ClientsUiState.kt`, `ClientsState.kt`, `ClientsScreen.kt`, `MainScreen.kt`, `MainMenuTest.kt`, tres clases de test nuevas, `SearchClientsUseCaseTest`, artefactos de la spec e índice.
- Limitación del revisor: la verificación la ejecutó `android-verifier` y la sesión implementadora; no se repitió el CI remoto ni se hizo `push`.

## Hallazgos por severidad

### Bloqueante

Ninguno. Los once criterios de aceptación tienen evidencia local en verde.

### Alto

- **A-1 — Resuelto: el CA-1 no aseveraba el orden en la UI.** `app/src/androidTest/java/com/kallpa/base/ui/ClientSearchTest.kt:49-51` comprobaba solo la presencia de las tres filas, mientras que `spec.md:60` exige que aparezcan en orden. Se añadió en `ClientSearchTest.kt:53-59` una comparación de `boundsInRoot.top` para Ana/Luis/Marta con `assertTrue`; el orden sigue respaldado además por `ClientDao.kt:10` (`ORDER BY nombre ASC`) y por `SearchClientsUseCaseTest.consultaVacia_devuelveTodosEnOrden`. Verificado con `connectedAndroidTest` en verde (25/25).
- **A-2 — Resuelto: cierre documental del CA-11 pendiente.** El verificador señaló que faltaba `review.md`, que los CA seguían sin marcar y que el índice estaba en `tasks`. Se marcan CA-1…CA-11 en `spec.md`, se crea este archivo con las salidas de ambos comandos y se actualiza `docs/specs/README.md` e `spec.md` a `verificado`.

### Medio

- **M-1 — Resuelto: detekt `TooManyFunctions` en el state holder.** `app/src/main/java/com/kallpa/base/ui/clients/ClientsState.kt` pasó de 9 a 12 funciones al añadir los eventos de ficha (límite configurado: 11). Se añadió `@Suppress("TooManyFunctions")` en `ClientsState.kt:18` con comentario justificativo: un evento público por acción de la UI. `detekt` en verde en la puerta final; no se relajó la configuración global.

### Bajo

- **B-1 — CA-9 usa `createAndroidComposeRule`, no una llamada explícita a `ActivityScenario`.** `ClientSearchRotationTest.kt:26` lanza `MainActivity` con `createAndroidComposeRule<MainActivity>()` (API v2), que internamente se apoya en `ActivityScenario`; `spec.md:68` y `plan.md:27` describen `ActivityScenario` de forma literal. La cobertura funcional es equivalente y el test está en verde; se documenta la diferencia sin rebajar el criterio ni reescribir un test estable.
- **B-2 — RF-9 sin CA ni test dedicado.** Guardar un cliente nuevo mientras hay una consulta activa no tiene criterio de aceptación propio. La pantalla mantiene el campo y las acciones disponibles durante la búsqueda (`ClientsScreen.kt:128-157`), pero no hay un test específico. Seguimiento no bloqueante para una spec posterior.
- **B-3 — Inestabilidad puntual del AVD en una corrida.** Una ejecución de `connectedAndroidTest` quedó detenida ~17 min en `ClientSearchRotationTest.querySobreviveRotacion` (fuera del timeout); se canceló (`exit 143`), se reinició `KallpaBase_API37` y la repetición terminó en verde en 2m 20s. `plan.md:54` ya contempla reevaluar la estrategia de rotación si el test resulta inestable; no hubo que cambiarla.

## Cobertura de criterios de aceptación

| Criterio | Estado | Evidencia |
| --- | --- | --- |
| CA-1 | Verificado (A-1 resuelto) | `ClientSearchTest.listadoInicial_conBuscarHabilitado` (`ClientSearchTest.kt:38-64`): tres filas visibles **en orden**, `Nuevo cliente`/`Editar`/`Eliminar` presentes y `Buscar` habilitado con `Buscar clientes` (`MainScreen.kt:112`); `MainMenuTest.barraSuperior_estadoInicial` en verde. |
| CA-2 | Verificado | `ClientSearchTest.buscar_muestraCampoYConservaDestino`: campo `Buscar cliente` enfocado tras pulsar `Buscar`, título `Título, Clientes` sin cambio de destino y `Sincronizar` deshabilitado. |
| CA-3 | Verificado | `SearchClientsUseCaseTest.filtraPorNombre` y `ClientSearchTest.busquedaPorNombre_filtra`. |
| CA-4 | Verificado | `SearchClientsUseCaseTest.filtraPorEmailIgnorandoMayusculas`. |
| CA-5 | Verificado | `SearchClientsUseCaseTest.filtraPorTelefono` (`999 000` literal; decisión T0 confirmada). |
| CA-6 | Verificado | `SearchClientsUseCaseTest.filtraPorDireccion`. |
| CA-7 | Verificado | `ClientSearchTest.sinCoincidencias_muestraMensajeSinResultados`: `Sin resultados`, sin `Sin clientes`, `Nuevo cliente` visible. |
| CA-8 | Verificado | `ClientSearchTest.limpiar_restauraListado`: campo vacío (leído de `EditableText`) y tres filas restauradas. |
| CA-9 | Verificado (B-1) | `ClientSearchRotationTest.querySobreviveRotacion`: consulta `ana` y filtro conservados tras cambiar `requestedOrientation` a landscape; base Room real sembrada y limpiada. |
| CA-10 | Verificado | `ClientDetailTest.fichaMuestraDatosYAbreEdicion`: ficha con cuatro valores, `Cerrar`/`Editar`, formulario precargado e ID `cliente-1` estable; regresión `UpdateClientUseCaseTest`. |
| CA-11 | Verificado (A-2 resuelto) | Puerta local y `connectedAndroidTest` en verde con 0 fallos; regresión de la spec 002 sin cambios (10/10) y evidencia registrada aquí. |

## Comandos ejecutados

| Comando | Resultado |
| --- | --- |
| `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` | `BUILD SUCCESSFUL in 1m 3s` — 12 tests unitarios, 0 fallos; lint `0 errors, 6 warnings, 1 hint`; APK debug generado. |
| `./gradlew --no-daemon connectedAndroidTest` en `KallpaBase_API37(AVD) - 17` | `BUILD SUCCESSFUL in 2m 20s` — `testsuites tests="25" failures="0" errors="0" skipped="0"`. |
| `git diff --check` | Sin salida (limpio). |

Desglose instrumentado (25/25): `CounterTest` 3, `GreetingTest` 1, `ClientPersistenceTest` 2, `ClientRepositoryTest` 2, `ClientDeleteTest` 2, `ClientDetailTest` 1, `ClientEditTest` 1, `ClientFormTest` 2, `ClientListTest` 1, `ClientSearchRotationTest` 1, `ClientSearchTest` 5, `MainMenuTest` 4. Regresión de la spec 002: 10/10.

Evidencia: `app/build/outputs/androidTest-results/connected/debug/TEST-KallpaBase_API37(AVD) - 17.xml`, `app/build/test-results/testDebugUnitTest/TEST-*.xml` y `app/build/reports/lint-results-debug.txt`.

## Convenciones, dependencias y artefactos

- **Version catalog:** sin cambios en `gradle/libs.versions.toml`, `build.gradle.kts`, `app/build.gradle.kts`, `AndroidManifest.xml`, `app/src/main/java/com/kallpa/base/data/local/*` ni `MainActivity.kt` (T12/RNF-5). No hay dependencias nuevas.
- **AGENTS.md:** Compose Material 3, tests con `createComposeRule`/`createAndroidComposeRule` (API v2), versiones solo en el catálogo, sin secretos ni artefactos de build versionados. `git status --short` lista únicamente los archivos previstos.
- **Compatibilidad CI:** los comandos locales coinciden con los jobs de `.github/workflows/android.yml`; `minSdk 24` no se ve afectado; no hay permisos ni recursos nuevos. CI remoto aún no ejecutado (sin `push`).
- **Regresión spec 003:** solo se ajustó la aserción de `Buscar` en `MainMenuTest.barraSuperior_estadoInicial`; el resto de aserciones (`Sincronizar`, overflow, destinos deshabilitados) permanecen. Quedó registrado en `spec.md:52` y en los hallazgos de la spec 003.

## Conclusión

Los CA-1…CA-11 están verificados con evidencia local en verde y no hay hallazgos bloqueantes. **A-1**, **A-2** y **M-1** quedan resueltos; **B-1**, **B-2** y **B-3** son seguimientos no bloqueantes documentados. La spec pasa a `verificado` en `spec.md` y en `docs/specs/README.md`; el paso a `cerrado` queda supeditado al PR con CI verde. No se ha hecho `push`.
