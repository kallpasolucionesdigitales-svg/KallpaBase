---
name: Spec-Driven Development
description: Crea y ejecuta especificaciones de funciones en KallpaBase: spec, plan, tareas, verificación y revisión
---

# Spec-Driven Development (KallpaBase)

Usa esta skill cuando se pida una funcionalidad nueva, un cambio con criterios de aceptación o cualquier comando `/spec.*`.

## Principio

Ningún código de producto sin una spec previa. La spec es la fuente de verdad: si la implementación se desvía, se actualiza la spec antes de continuar. Los hotfixes sin spec deben documentarse después.

## Ciclo

1. **Spec** — `/spec.new <NNN> <título>` crea `docs/specs/<NNN-slug>/spec.md` con contexto, objetivos, no-objetivos, requisitos y criterios de aceptación medibles.
2. **Plan** — `/spec.plan <spec>` crea `plan.md`: decisiones técnicas, archivos a tocar, dependencias del version catalog y estrategia de tests.
3. **Tareas** — `/spec.tasks <spec>` crea `tasks.md` como checklist ordenado; cada tarea indica archivos y forma de verificación.
4. **Implementación** — `/spec.implement <spec>` ejecuta las tareas pendientes marcándolas solo cuando estén verificadas.
5. **Verificación** — `/spec.verify <spec>` corre los chequeos Gradle/ADB sin modificar archivos.
6. **Revisión** — `/spec.review <spec>` compara el diff con la spec y escribe `review.md` con hallazgos por severidad.

La verificación y la revisión pueden ejecutarse en paralelo porque usan subagentes distintos (`android-verifier` y `android-reviewer`).

## Reglas

- Los artefactos viven solo en `docs/specs/<NNN-slug>/`; se actualiza el índice `docs/specs/README.md` en cada cambio de estado.
- Los criterios de aceptación se escriben en formato Dado / Cuando / Entonces y deben ser verificables con un comando o test.
- Las dependencias se declaran en `gradle/libs.versions.toml`, nunca como literales.
- La puerta de calidad es `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y, si hay UI, `./gradlew connectedAndroidTest` con el AVD `KallpaBase_API37`.
- No se versionan secretos, `local.properties` ni keystore; el release queda fuera del flujo por defecto.
- Ningún agente hace `git push`; el PR se abre solo tras validación humana.

## Referencias

- `references/spec-template.md` — plantillas de `spec.md`, `plan.md`, `tasks.md` y `review.md`.
- `references/android-verification.md` — comandos, AVD, criterios de "verde" y fallos habituales.
