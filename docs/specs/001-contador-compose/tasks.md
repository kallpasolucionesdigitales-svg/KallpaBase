# Tareas — 001

- [x] T1 — Añadir `CounterScreen` en `app/src/main/java/com/kallpa/base/MainActivity.kt` e invocarlo desde `setContent`. Verificar: `ktlintCheck` y compilación correcta.
- [x] T2 — Crear `app/src/androidTest/java/com/kallpa/base/CounterTest.kt` con los tres tests de los criterios CA-1 a CA-3. Verificar: compilación del source set AndroidTest.
- [x] T3 — Mantener `GreetingTest` sin cambios como test de regresión. Verificar: `git diff --stat` no lo incluye.
- [x] T4 — Actualizar el estado de la spec a `en progreso` en `docs/specs/README.md` y en `spec.md`. Verificar: índice coherente.
- [x] T5 — Verificación completa. Verificar: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` → BUILD SUCCESSFUL (1m 43s) y `./gradlew connectedAndroidTest` → BUILD SUCCESSFUL (49s, 4 tests / 0 fallos) en `KallpaBase_API37`. Validado por `android-verifier`.
