# Plantillas de artefactos SDD

Copia la sección correspondiente y sustituye los marcadores entre corchetes.

## `spec.md`

```markdown
# [NNN] — [Título]

- Estado: spec
- Rama: feature/[NNN-slug]
- Fecha: [AAAA-MM-DD]

## Contexto
[Qué problema del usuario o del sistema se aborda y por qué ahora.]

## Objetivos
- [Objetivo verificable 1]
- [Objetivo verificable 2]

## No-objetivos
- [Lo que deliberadamente queda fuera de alcance]

## Requisitos funcionales
- RF-1: [Comportamiento observable]
- RF-2: [Comportamiento observable]

## Requisitos no funcionales
- RNF-1: [Rendimiento, compatibilidad minSdk 24, accesibilidad, seguridad, etc.]

## Criterios de aceptación
- [ ] CA-1: Dado [contexto], cuando [acción], entonces [resultado]. Verificación: [test/comando].
- [ ] CA-2: Dado [contexto], cuando [acción], entonces [resultado]. Verificación: [test/comando].

## Pruebas requeridas
- Unitarias: [casos]
- Compose UI: [casos con createComposeRule]
- Instrumentadas: [casos o "no aplica"]

## Riesgos y decisiones abiertas
- [Riesgo o decisión que requiere confirmación humana]
```

## `plan.md`

```markdown
# Plan técnico — [NNN]

## Decisiones
- [Decisión 1 y justificación]
- [Decisión 2 y justificación]

## Archivos a crear o modificar
- `app/src/main/java/...`: [qué cambia]
- `app/src/test/java/...`: [qué cubre]
- `gradle/libs.versions.toml`: [dependencia nueva o "ninguna"]

## Estado y UI (Compose)
[Modelo de estado, composables y Material 3 que se usarán.]

## Estrategia de tests
[Qué se cubre con JUnit, qué con Compose UI y qué con instrumentados.]

## Compatibilidad y riesgos
[minSdk 24, permisos, navegación, migración de datos, impacto en CI.]
```

## `tasks.md`

```markdown
# Tareas — [NNN]

- [ ] T1 — [Acción concreta] (`archivo:...`). Verificar: [comando o test].
- [ ] T2 — [Acción concreta] (`archivo:...`). Verificar: [comando o test].
- [ ] T3 — Añadir tests unitarios y de UI. Verificar: `./gradlew test`.
- [ ] T4 — Verificación completa. Verificar: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` [y `connectedAndroidTest` si aplica].
```

## `review.md`

```markdown
# Revisión — [NNN]

## Hallazgos

| Severidad | Archivo:Línea | Hallazgo | Recomendación |
| --- | --- | --- | --- |
| [Bloqueante/Alto/Medio/Bajo] | [ruta:línea] | [descripción] | [acción concreta] |

Si no hay hallazgos, escribir: **Sin hallazgos.**

## Verificación

- Comandos ejecutados: [listar]
- Resultado: PASS / FAIL
- Evidencia: [salidas relevantes]

## Conclusión

[Cumple / no cumple los criterios de aceptación y por qué.]
```
