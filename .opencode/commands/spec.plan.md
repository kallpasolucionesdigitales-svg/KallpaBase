---
description: Genera el plan técnico de una spec
agent: spec-writer
---

Genera `plan.md` para la spec: $ARGUMENTS.

1. Lee `docs/specs/<spec>/spec.md`, la skill `spec-driven` y su plantilla de plan.
2. Decide la solución mínima: archivos a crear o modificar, dependencias a añadir en `gradle/libs.versions.toml`, modelo de estado en Compose y estrategia de tests (unitarios, Compose UI e instrumentados).
3. Escribe `docs/specs/<spec>/plan.md` siguiendo la plantilla. No modifiques código de producto.
4. Actualiza el estado de la spec a `plan` en `docs/specs/README.md`.
5. Reporta las decisiones principales y cualquier decisión que requiera confirmación humana.
