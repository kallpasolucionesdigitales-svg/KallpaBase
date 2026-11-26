# Instrucciones del proyecto KallpaBase

## Qué es este repo

Aplicación Android Kotlin + Jetpack Compose (Material 3), paquete `com.kallpa.base`, módulo único `:app`. Gradle Wrapper `9.9.2`, AGP `9.5.2`, JDK provisto por Android Studio (JBR 25). `minSdk 24`, `targetSdk 36`, `compileSdk 37.2`.

## Comandos canónicos

- Verificación completa local: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug`
- Tests instrumentados: `./gradlew connectedAndroidTest` (AVD `KallpaBase_API37`, Pixel 9 x86_64 / Google APIs)
- APK debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `./gradlew assembleRelease` solo con el keystore fuera del repo; no versionar credenciales.

## Convenciones

- Usar siempre `gradle/libs.versions.toml` para versiones; no literales en `build.gradle.kts`.
- Compose con Material 3; tests de UI con `createComposeRule` (API v2).
- Todo cambio de código debe pasar `check` (ktlint + detekt + lint) y mantener verde `.github/workflows/android.yml`.
- No editar `build/`, `.gradle/`, `local.properties`, `~/.gradle/kallpabase-keystore.properties` ni el keystore.
- No hacer `git push` sin confirmación humana; los PR se validan con CI verde.
- Commits con prefijo convencional en inglés (`feat:`, `fix:`, `chore:`, `docs:`).

## Flujo Spec-Driven Development

- Toda funcionalidad nueva nace de `docs/specs/<NNN-slug>/spec.md` con criterios de aceptación medibles.
- Ciclo: `/spec.new` → `/spec.plan` → `/spec.tasks` → `/spec.implement` → `/spec.verify` + `/spec.review` → PR.
- La spec es la fuente de verdad; si el código se desvía, se actualiza la spec antes de seguir.
- Índice de specs: `docs/specs/README.md`. Configuración de OpenCode: `docs/OPENCODE_SDD.md`.

## Documentación de referencia

- `docs/SETUP.md`: toolchain, decisiones de instalación y registro de CI.
- `docs/OPENCODE_SDD.md`: configuración de agentes y progreso del flujo SDD.
