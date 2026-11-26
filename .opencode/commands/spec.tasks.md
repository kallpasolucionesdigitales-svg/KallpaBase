---
description: Convierte el plan de una spec en tareas verificables
agent: spec-writer
---

Genera `tasks.md` para la spec: $ARGUMENTS.

1. Lee `spec.md` y `plan.md` de esa spec y la plantilla de la skill `spec-driven`.
2. Produce una lista ordenada de tareas en checkbox Markdown. Cada tarea debe indicar el archivo afectado, la acción concreta y cómo se verifica (comando o test).
3. Incluye tareas explícitas de tests unitarios/UI y una tarea final de verificación: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y `./gradlew connectedAndroidTest` si la spec lo requiere.
4. Escribe `docs/specs/<spec>/tasks.md` y actualiza el estado a `tasks` en `docs/specs/README.md`.
5. No marques tareas como completadas.
