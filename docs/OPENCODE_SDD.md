# Configuración de OpenCode y Spec-Driven Development en KallpaBase

> Estado: **EN CURSO** · Rama: `chore/opencode-sdd-setup` · Base: `main` @ `ef51043` · Última actualización: 2026-11-24 22:56 -03

## Objetivo

Dotar al repositorio de una configuración de OpenCode reproducible y de un flujo de **Spec-Driven Development (SDD)** para que cada cambio de la app nazca de una especificación con criterios de aceptación verificables.

La base Android ya estaba lista y verde; ver `docs/SETUP.md` para el detalle del toolchain, CI, linting, firma y AVD.

## Resumen del estado actual

- El proyecto tiene toolchain Android completo: Kotlin + Jetpack Compose (Material 3), AGP `9.5.2`, Gradle Wrapper `9.9.2`, ktlint `14.2.0`, detekt `2.0.0-alpha.6`, CI verde (run `#18`), AVD `KallpaBase_API37`, Dependabot y labels configurados.
- Antes de esta sesión **no existían** `AGENTS.md`, `opencode.jsonc`, directorio `.opencode/` ni artefactos de especificación.
- Se diseñó la arquitectura de agentes/subagentes y el ciclo SDD, y se creó la rama de trabajo.
- La configuración ya está versionada: `AGENTS.md`, `opencode.jsonc`, seis comandos, la skill `spec-driven` y el índice de specs (pasos 4–8 completados).
- Descubrimiento de agentes y comandos en la TUI **confirmado** (paso 9). Falta ejecutar la feature piloto.

## Decisiones tomadas

| Decisión | Detalle |
| --- | --- |
| Alcance | Solo configuración de herramientas y documentación; no modificar código de producto. |
| Rama | `chore/opencode-sdd-setup`, creada desde `main` (`ef51043`) con el árbol limpio. Sin `push`. Se prefirió `chore/` sobre `feature/` porque esto no es funcionalidad de la app; las ramas `feature/<nombre>` quedan para la feature piloto. |
| Agentes propios | `spec-writer` (`all`), `android-reviewer` (`subagent`, solo lectura), `android-verifier` (`subagent`, sin edición). Se conservan los built-ins `build`, `plan`, `explore` y `general`. |
| Permisos | `shell` en `ask`, allowlist para inspección Git y `./gradlew`, `git push` denegado, `local.properties` y keystore denegados. Regla ganadora: **la última coincidente**. Reglas de agente se añaden después de las globales. |
| Modelo | Sin `model` fijo en los agentes nuevos: heredan el de la sesión padre para controlar coste. |
| Subagentes | `experimental.subagent_depth` se mantiene en `1` (un subagente no crea otro). Sin `todowrite`. |
| Ubicación | Config versionada en la raíz y `.opencode/` del proyecto; no se modifica la config global `~/.config/opencode`. |
| SDD | Artefactos por función en `docs/specs/<NNN-slug>/`: `spec.md`, `plan.md`, `tasks.md`, `review.md`. |
| Interfaz de equipo | Seis comandos: `/spec.new`, `/spec.plan`, `/spec.tasks`, `/spec.implement`, `/spec.verify`, `/spec.review`. |
| Verificación | Puerta local: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y `./gradlew connectedAndroidTest` con el AVD; TODO PR debe tener CI verde. |

## Arquitectura aplicada

| Archivo | Contenido |
| --- | --- |
| `AGENTS.md` | Convenciones del proyecto: comandos canónicos, version catalog, Compose/Material 3, secretos fuera del repo, enlaces a `docs/SETUP.md` y `docs/specs/README.md`. |
| `opencode.jsonc` | `$schema`, `default_agent: "build"`, permisos globales y los 3 agentes propios. |
| `.opencode/commands/spec.new.md` | Crea `spec.md` desde `$ARGUMENTS`; agente `spec-writer`. |
| `.opencode/commands/spec.plan.md` | Genera `plan.md`; agente `spec-writer`. |
| `.opencode/commands/spec.tasks.md` | Genera `tasks.md` como checklist ordenado; agente `spec-writer`. |
| `.opencode/commands/spec.implement.md` | Implementa tareas pendientes; agente `build`. |
| `.opencode/commands/spec.verify.md` | Ejecuta chequeos; agente `android-verifier`, `subagent: true`. |
| `.opencode/commands/spec.review.md` | Compara `git diff` con la spec; agente `android-reviewer`, `subagent: true`. |
| `.opencode/skills/spec-driven/SKILL.md` | Workflow del ciclo, plantillas y guía de verificación Android. |
| `docs/specs/README.md` | Índice de specs y regla de numeración `NNN-slug`. |

Ciclo de trabajo: **`/spec.new` → `/spec.plan` → `/spec.tasks` → `/spec.implement` → `/spec.verify` + `/spec.review` (en paralelo) → PR**.

## Checklist

- [x] **Paso 0** — Diagnosticar repo y toolchain; confirmar CI verde y AVD disponible.
- [x] **Paso 1** — Definir arquitectura de agentes/subagentes, permisos y flujo SDD.
- [x] **Paso 2** — Crear rama `chore/opencode-sdd-setup` desde `main` (`ef51043`), árbol limpio.
- [x] **Paso 3** — Documentar y listar el progreso en `docs/OPENCODE_SDD.md`.
- [x] **Paso 4** — Crear `AGENTS.md` con convenciones y comandos canónicos.
- [x] **Paso 5** — Crear `opencode.jsonc` (`$schema`, `default_agent`, permisos, 3 agentes); validado como JSON.
- [x] **Paso 6** — Crear los 6 comandos en `.opencode/commands/`.
- [x] **Paso 7** — Crear skill `spec-driven` con plantillas y guía de verificación Android.
- [x] **Paso 8** — Crear `docs/specs/README.md` (índice y regla `NNN-slug`).
- [x] **Paso 9** — Verificar descubrimiento de agentes/comandos en TUI; confirmado por el usuario.
- [ ] **Paso 10** — Feature piloto `feature/001-<nombre>`: spec → plan → tareas → implementación → verificación → revisión.
- [ ] **Paso 11** — Validar localmente y exigir CI verde en el PR.
- [ ] **Paso 12** — Push de la rama y PR (aún no ejecutado).

## Registro de progreso

### 2026-11-24 — Sesión 1

1. Lectura de `README.md`, `docs/SETUP.md`, Gradle y workflows: base Android confirmada (AGP `9.5.2`, Gradle `9.9.2`, Compose Material 3, JBR 25, AVD `KallpaBase_API37`, CI run `#18` verde).
2. Consulta de la documentación oficial de OpenCode V2: config, agents, commands, skills, instructions y permissions. Confirmado: subagentes en background con contexto fresco; `subagent_depth` por defecto `1`; permisos por agente con última regla coincidente; la config se recarga al guardar.
3. Verificado: no existían `AGENTS.md`, `.opencode/` ni configuración de proyecto; `~/.config/opencode` solo contiene `service.json`.
4. Diseño del plan presentado al usuario: 3 agentes, 6 comandos, 1 skill, artefactos SDD y pasos de aplicación.
5. Decisión de rama: `chore/opencode-sdd-setup`, creada localmente desde `main`, sin modificaciones ni push.
6. Registro de progreso escrito en este archivo (`docs/OPENCODE_SDD.md`).
7. Pasos 4–8 aplicados: creados `AGENTS.md`, `opencode.jsonc`, los seis comandos, la skill `spec-driven` (con `spec-template.md` y `android-verification.md`) y `docs/specs/README.md`. `opencode.jsonc` validado con `python3 -m json.tool`; corregidas tres erratas en los comandos. OpenCode cargó `AGENTS.md` y descubrió la skill automáticamente.
8. Paso 9 confirmado por el usuario: los tres agentes propios y los seis comandos `/spec.*` aparecen en la TUI. No hizo falta `/reload`.

## Riesgos y pendientes

- **Riesgo menor:** la allowlist `./gradlew *` es amplia; conviene estrecharla tras la primera semana de uso.
- **Sin bloqueadores técnicos:** la toolchain, el linting y el emulador ya están validados en `docs/SETUP.md`.
- **Pendiente:** arrancar la feature piloto `001` para validar el ciclo completo de extremo a extremo.

## Siguiente paso

Ejecutar el paso **10**: crear la rama `feature/001-<nombre>` y recorrer el ciclo completo con una feature piloto pequeña: `/spec.new` → `/spec.plan` → `/spec.tasks` → `/spec.implement` → `/spec.verify` + `/spec.review`. Después validar con los comandos del paso 11 antes del PR.
