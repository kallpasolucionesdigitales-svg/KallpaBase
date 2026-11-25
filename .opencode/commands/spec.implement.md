---
description: Implementa las tareas pendientes de una spec
agent: build
---

Implementa las tareas pendientes de la spec: $ARGUMENTS.

1. Lee `spec.md`, `plan.md` y `tasks.md` de esa spec y las convenciones de `AGENTS.md`.
2. Trabaja en orden, una tarea a la vez, sin omitir criterios de aceptación.
3. Declara cualquier dependencia nueva en `gradle/libs.versions.toml`; no uses versiones literales.
4. Marca una tarea como completada en `tasks.md` solo después de verificarla localmente.
5. Al terminar ejecuta `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y reporta resultado. No hagas `git push`.
