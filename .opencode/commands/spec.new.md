---
description: Crea una nueva especificación SDD (spec.md)
agent: spec-writer
---

Crea una nueva especificación a partir de: $ARGUMENTS.

1. Carga la skill `spec-driven` y lee `references/spec-template.md`.
2. Consulta `docs/specs/README.md` para tomar el siguiente ID libre (`NNN`) y deriva el slug en `kebab-case`.
3. Crea la rama antes de escribir archivos: verifica con `git status --porcelain` que el árbol esté limpio; si hay cambios pendientes, detente y repórtalos sin crear la rama ni los archivos. Desde `main`, ejecuta `git switch -c feature/<NNN-slug>`; si la rama ya existe, reutilízala con `git switch feature/<NNN-slug>` sin recrearla. No hagas `push` ni `commit`.
4. Crea `docs/specs/<NNN-slug>/spec.md` con: contexto, problema, objetivos, no-objetivos, requisitos funcionales y no funcionales, criterios de aceptación Dado/Cuando/Entonces con su forma de verificación, pruebas requeridas, riesgos y decisiones abiertas.
5. Añade la fila correspondiente al índice `docs/specs/README.md` con estado `spec` y rama `feature/<NNN-slug>`.
6. No escribas código de producto ni modifiques otros archivos.
7. Reporta la ruta creada, la rama activa y lista los criterios de aceptación en una respuesta corta.
