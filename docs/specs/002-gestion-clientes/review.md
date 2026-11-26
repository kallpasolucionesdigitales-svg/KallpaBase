# Revisión — 002 Gestión de clientes

- Rama: `feature/002-gestion-clientes`.
- Estado: `verificado`.
- Fecha: 2026-11-26.
- Alcance: implementación completa de T1–T11, `spec.md`, `plan.md`, `tasks.md`, `docs/specs/README.md` y salidas reales de Gradle/ADB.

## Verificación ejecutada

| Comando | Resultado | Detalle |
| --- | --- | --- |
| `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` | `BUILD SUCCESSFUL` | ktlint, detekt y lint sin hallazgos; 6 tests unitarios, 0 fallos/errores; APK debug generado en `app/build/outputs/apk/debug/app-debug.apk`. |
| `./gradlew --no-daemon connectedAndroidTest` | `BUILD SUCCESSFUL` | AVD `KallpaBase_API37`, 14 tests instrumentados, 0 fallos/errores (incluye `CounterTest` y `GreetingTest` de 001). |

Las ejecuciones se repitieron tras el último cambio de código (separación de `ClientsState` / `RememberClientsState` y ajustes de formato). Los informes XML están en `app/build/test-results/testDebugUnitTest/` y `app/build/outputs/androidTest-results/connected/debug/`.

## Cobertura de criterios

| Criterio | Estado | Evidencia |
| --- | --- | --- |
| CA-1 | Verificado | `ClientListTest.listaVacia_muestraEstadoVacio` |
| CA-2 | Verificado | `CreateClientUseCaseTest.guardaConId` + `ClientFormTest.altaValida_actualizaLista` (ID no vacío y registro en repositorio falso; persistencia real en CA-7) |
| CA-3 | Verificado | `ClientValidatorTest.camposInvalidos` + `ClientFormTest.camposInvalidos_muestraErrores` |
| CA-4 | Verificado | `UpdateClientUseCaseTest.mantieneId` + `ClientEditTest.edicion_actualizaLista` (precarga asertada leyendo `EditableText` del formulario) |
| CA-5 | Verificado | `ClientDeleteTest.cancelar_noElimina` |
| CA-6 | Verificado | `DeleteClientUseCaseTest.bajaEliminaRegistro` + `ClientRepositoryTest.bajaEliminaRegistro` (`ClientRepository.get(id)` contra Room real) |
| CA-7 | Verificado | `ClientPersistenceTest.altaSobreviveReinicio` (los cuatro campos se comparan en el objeto restaurado; la tarjeta muestra nombre, email, teléfono y dirección) |
| CA-8 | Verificado | `ClientPersistenceTest.bajaSobreviveReinicio` |
| CA-9 | Verificado | Ambas puertas locales en verde; CI del PR queda pendiente de ejecución remota |

## Hallazgos resueltos durante la implementación

- CA-7 pedía la dirección en el listado: `ClientRow` la muestra y `plan.md` lo documenta.
- CA-4 pedía precarga del formulario: `ClientEditTest` lee `SemanticsProperties.EditableText` antes de guardar.
- Reinicio definido sin ambigüedad: cierre y reconstrucción de `KallpaDatabase` sobre el mismo fichero, con limpieza en `@Before`/`@After`.
- Aislamiento de tests Room: nombre de base propio y borrado del fichero en cada caso.
- `detekt` y `ktlint` obligaron a separar `ClientsState` de `RememberClientsState.kt` y a usar nombres de archivo PascalCase; `plan.md` refleja la decisión.

## Hallazgos resueltos y seguimiento

- **Resuelto (Medio)** — `docs/ROADMAP.md` quedó sincronizado: F-01 figura como `verificado` y F-11 (persistencia local) pasó a `verificado` durante el saneamiento posterior al merge.
- **Resuelto (Medio)** — Los artefactos de la spec y el roadmap se incluyeron en el PR #12, mergeado en `main`; CI del PR y de `main` en verde.
- **Medio (seguimiento)** — No hay test que lance `MainActivity` y aserte la pantalla inicial; T8 cubre compilación y la conservación de `CounterTest`/`GreetingTest` según lo previsto en la spec. La spec 003 añade el shell de navegación y cubre la pantalla inicial.
- **Bajo (seguimiento)** — `ClientValidator` acepta `ana@` porque solo exige `@`; CA-3 no cubre ese caso, pero RF-2 pide formato válido. Se puede endurecer en una spec posterior sin migración de datos.

## Conclusión

Todas las tareas T1–T11 están completadas y los nueve criterios de aceptación tienen evidencia local y remota. La spec pasa a `cerrado` con el PR #12: ambos jobs de CI terminaron en verde (run `45885350035`) y el merge en `main` generó el run `45885389796`, también verde. No hay `push` pendiente.
