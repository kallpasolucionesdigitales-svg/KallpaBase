# Índice de especificaciones

Toda funcionalidad nueva de KallpaBase se define aquí antes de implementarse. El flujo y la configuración de OpenCode están documentados en `docs/OPENCODE_SDD.md`.

El inventario de funcionalidades, prioridades y decisiones de producto está en [`docs/ROADMAP.md`](../ROADMAP.md).

## Convención

- ID secuencial de 3 dígitos con el nombre en `kebab-case`: `docs/specs/<NNN-slug>/`.
- Rama de trabajo: `feature/<NNN-slug>`.
- Artefactos: `spec.md` (qué y por qué), `plan.md` (cómo), `tasks.md` (checklist), `review.md` (hallazgos y verificación).
- Estados: `spec` → `plan` → `tasks` → `en progreso` → `verificado` → `cerrado`.
- Comandos: `/spec.new`, `/spec.plan`, `/spec.tasks`, `/spec.implement`, `/spec.verify`, `/spec.review`.
- Una spec no se cierra sin `ktlintCheck detekt lint test assembleDebug` en verde y CI del PR en verde.

## Especificaciones

| ID | Título | Estado | Rama | PR |
| --- | --- | --- | --- | --- |
| 001 | Contador Compose en MainActivity | cerrado | `feature/001-contador-compose` | [#11](https://github.com/kallpasolucionesdigitales-svg/KallpaBase/pull/11) |
| 002 | Gestión de clientes | verificado | `feature/002-gestion-clientes` | — |
| 003 | Menú principal | spec | `feature/003-menu-principal` | — |
