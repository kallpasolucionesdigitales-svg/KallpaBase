---
description: Revisa el diff contra la spec y las convenciones Android
agent: android-reviewer
subagent: true
---

Revisa los cambios de la spec: $ARGUMENTS.

1. Lee `spec.md`, `plan.md`, `tasks.md` y el diff de Git (`git status`, `git diff`, `git log`).
2. Comprueba: criterios de aceptación cubiertos, convenciones de `AGENTS.md`, uso de `gradle/libs.versions.toml`, tests añadidos, ausencia de secretos o archivos generados y que CI seguirá verde.
3. Escribe `docs/specs/<spec>/review.md` con los hallazgos agrupados por severidad (Bloqueante / Alto / Medio / Bajo), indicando archivo:línea y una recomendación concreta. Si no hay hallazgos, indícalo explícitamente.
4. No modifiques código ni ejecutes comandos que cambien el repo o el dispositivo.
