---
description: Ejecuta la verificación Android de una spec sin modificar archivos
agent: android-verifier
subagent: true
---

Verifica la spec: $ARGUMENTS.

1. Lee `spec.md` y `tasks.md` para identificar los criterios de aceptación y los comandos requeridos.
2. Ejecuta en este orden: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` y, si la spec lo pide o hay cambios de UI, `./gradlew connectedAndroidTest` con el AVD `KallpaBase_API37`.
3. No edites archivos, no instales ni desinstales APKs y no ejecutes `assembleRelease`.
4. Reporta: comandos ejecutados, resultado PASS/FAIL, fallos concretos con archivo y línea, y qué criterios de aceptación quedan sin verificar.
