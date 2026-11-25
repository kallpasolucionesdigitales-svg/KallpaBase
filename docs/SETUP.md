# Setup de KallpaBase para desarrollo Android

> Estado: **COMPLETADO** · Última actualización: 2026-11-21

## Entorno detectado

| Item | Valor |
| --- | --- |
| OS | Ubuntu 26.04.1 LTS (x86_64) |
| RAM | 14 GB (recomendado 16 GB con emulador) |
| Disco | 197 GB libres |
| Display | `:0` (X server disponible) |
| Virtualización | KVM disponible (`/dev/kvm`, flags `vmx/svm` presentes) |
| `sudo` | **Bloqueado** en este entorno (`no new privileges`): instalar en directorio del usuario |
| Java / Gradle / SDK | No instalados al inicio |

## Decisiones tomadas

- **Toolchain:** Android Studio (IDE oficial, GUI) instalado desde el **`.tar.gz` oficial de Google** → recomendado para principiante: el *Setup Wizard* descarga el SDK, build-tools, platform-tools y emulador automáticamente.
  - Alternativa descartada por ahora: `sdkmanager` puro por CLI (más reproducible, pero más fricción con licencias y variables de entorno).
  - Alternativa secundaria: `sudo snap install android-studio --classic` (un comando, pero publicado por la comunidad, no por Google).
- **Ruta de instalación:** `$HOME/.local/opt/android-studio` (no `/opt`, porque `sudo` no está disponible en este entorno).
- **Gradle:** se usará el **Gradle Wrapper** generado con el proyecto (no Gradle global).
- **JDK:** Android Studio trae su propio JBR; el proyecto fijará el JDK requerido según AGP (JDK 17 o superior). No instalar Java global "a ciegas".
- **Registro de progreso:** este archivo, versionado en Git.
- **Stack elegido para la base:** Kotlin + Jetpack Compose (Material 3), AGP `9.5.0`, Gradle `9.6.0` (wrapper), Kotlin/Compose compiler `2.4.21`, Compose BOM `2026.11.01`.
- **Coords de la app:** `namespace`/`applicationId` = `com.kallpa.base`, `minSdk 24`, `targetSdk 36`, `compileSdk 37.2` (DSL `release(37) { minorApiLevel = 2 }`).

## Checklist

- [x] **Paso 0** — Verificar entorno (distro, RAM, disco, GUI, KVM) y decidir herramienta.
- [x] **Paso 1** — Instalar Android Studio desde `.tar.gz` oficial y completar el *Setup Wizard* (SDK Platform, Build-Tools, Platform-Tools, Emulator).
- [x] **Paso 2** — Configurar `ANDROID_HOME` / `ANDROID_SDK_ROOT`, `JAVA_HOME` y `PATH`; instalar `cmdline-tools`.
- [x] **Paso 3** — Crear la base del proyecto en el repo: Gradle Wrapper, `settings.gradle.kts`, `build.gradle.kts`, `libs.versions.toml`, módulo `:app`.
- [x] **Paso 4** — `.gitignore` de Android, `local.properties` ignorado, firma release fuera del repo.
- [x] **Paso 5** — Crear AVD (API 37, Google APIs, x86_64) y verificar `adb devices`.
- [x] **Paso 6** — Primer `./gradlew assembleDebug` + `./gradlew test` y documentar resultados.
- [x] **Paso 7** (opcional) — CI (GitHub Actions), ktlint/detekt, version catalog y Dependabot.

## Registro de progreso

### 2026-11-21
- Repositorio `KallpaBase`: rama `main`, commit inicial `dfc4aa8` (solo `README.md`).
- Todo el scaffold y la documentación se commitearon como `58121ea` ("Setup Android base project (Kotlin, Compose, Gradle 9.6)").
- Sin JDK, Gradle ni Android SDK instalados.
- Decidido: **Android Studio GUI desde el archive oficial de Google**; progreso registrado en `docs/SETUP.md`.
- Descargado y verificado archive oficial: `android-studio-rabbit2-patch2-linux.tar.gz` (2026.2.2.10, 1.589.479.785 bytes).
- Extraído en `$HOME/.local/opt/android-studio`.
- Primer lanzamiento falló con `GLib-GIO-ERROR: ... xsettings ... 'antialiasing'` (incompatibilidad GTK/Wayland de este entorno).
- **Fix aplicado:** lanzar con `GDK_BACKEND=x11`. Android Studio quedó corriendo (PID 22374) y esperando el *Setup Wizard* en la GUI.
- Nota: `sudo` está bloqueado, se omite `apt install` de librerías i386.
- Usuario completó el *Setup Wizard*. Verificado:
  - `platforms;android-37.0`, `build-tools;36.0.0`, `platform-tools;37.0.3`, `emulator;37.3.10`
  - System image: `android-37.0;google_apis_playstore;x86_64`
  - `adb devices` responde correctamente.
- Instalados además **commandline-tools 15859902** (`sdkmanager 22.0`; el CLI avisa que será reemplazado por `android sdk`).
- Exportado en `~/.bashrc` y `~/.profile`: `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `JAVA_HOME` (JBR 25 de Android Studio) y `PATH` con `cmdline-tools`, `platform-tools` y `emulator`.
- Instalado además `platforms;android-37.2` (los libs de Compose 1.13 exigen compileSdk ≥ 37.1).
- Scaffold creado: `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`, `gradle.properties`, wrapper 9.6.0, módulo `:app` con `MainActivity` Compose y `.gitignore`.
- Primera compilación: falló por dependencias faltantes y por `compileSdk = 37.0` → corregido.
- **`./gradlew assembleDebug` → BUILD SUCCESSFUL** (APK en `app/build/outputs/apk/debug/app-debug.apk`).
- Firma release: keystore en `~/.android/kallpabase-release.jks` (fuera del repo) y credenciales en `~/.gradle/kallpabase-keystore.properties` (modo 600). Plantilla en `docs/keystore.properties.example`.
- **`./gradlew assembleRelease` → BUILD SUCCESSFUL**; verificado con `apksigner verify --print-certs` (certificado `CN=KallpaBase`).
- AVD `KallpaBase_API37` (Pixel 9, x86_64, Google APIs Play Store) creado y arrancado. Primera ventana mostró un diálogo de advertencia de RAM (15 GB disponibles vs 16 GB sugeridos); se relanzó en modo headless (`-no-window`) y **boot completado en ~78 s**.
- `adb devices` → `emulator-5554 device`; `adb install -r app-debug.apk` → **Success**; `MainActivity` en foreground (PID verificado).
- Tests agregados: `ExampleUnitTest` (JUnit 4) y `GreetingTest` (Compose UI, API v2 de `createComposeRule`).
- `./gradlew test` → **BUILD SUCCESSFUL**; `./gradlew connectedAndroidTest` → **BUILD SUCCESSFUL** en `KallpaBase_API37 (AVD) - 17`.
- Nota: `ui-test-junit4` y `ui-test-manifest` no son versionados por el BOM 2026.11.01; se fijaron a `1.13.0` en el catálogo.
- **Paso 7 completado:** ktlint `14.2.0` + detekt `2.0.0-alpha.6` (la rama 1.x no corre con el JBR 25 local: falla con `--jvm-target 25`; la 2.x usa Kotlin 2.x y funciona). Config en `.editorconfig` y `config/detekt/detekt.yml`; ambos enganchados a `check`.
- CI en `.github/workflows/android.yml`: job `build` (JDK 21 + setup-android + caché Gradle + `ktlintCheck detekt lint test assembleDebug` + artefactos) y job `instrumented-test` (emulador API 37 / `google_apis` / `pixel_9` + `connectedAndroidTest`).
- Dependabot en `.github/dependabot.yml`: semanal para `gradle` y `github-actions`.
- Verificación local: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug` → **BUILD SUCCESSFUL** (2m 32s). YAML validado con PyYAML.
- Commit de cierre del Paso 7: `chore: add CI, linting and dependabot`.
- Fix de CI (2026-11-22): `setup-android` `@v3` → `@v4` (v3 instala el paquete `tools`, eliminado por Google → `sdkmanager` exit 1). Además `api-level: '37.0'` porque el SDK usa `android-37.0` como identificador, no `android-37`.
- Fix de CI (2026-11-22, parte 2): los runs #9 y #10 pasaron el job estático pero fallaron en `Tests instrumentados (API 37)` con `Timeout waiting for emulator to boot`. Agregado paso de **KVM** (udev rules, requerido por `android-emulator-runner` en Linux) y `emulator-boot-timeout: 1200`.
- **CI verde:** run `#11` (commit `2c8a6ba`) → **Success** en 4m 57s; run `#13` (`ebddaee`, tras los bumps de acciones) → **Success**; run `#14` (`58088bb`, estado final con AGP 9.5.2 + Gradle 9.9.1) → **Success** en 6m 19s (estático 2m 7s · instrumentados 4m 7s), artefactos `app-debug` 11.9 MB y `reports` 137 KB, sin warnings; run `#15` (`116c650`, documentación final) → **Success** en 5m 18s.
- **Dependabot (2026-11-22):** mergeados PRs **#1–#4** (`setup-java@v6`, `upload-artifact@v7`, `checkout@v7`, `cache@v6`), con lo que desaparecen los warnings de Node 20 / acciones deprecadas. PR **#5** (`setup-android` 3→4) ya estaba cerrado por redundante (`d4a591b`). YAML validado.
- **Dependabot (2026-11-22, parte 2):** mergeados PRs **#6** (AGP `9.5.0`→`9.5.2`) y **#7** (Gradle wrapper `9.6.0`→`9.9.1`). Verificación local en worktree temporal: `ktlintCheck detekt lint test assembleDebug assembleRelease` → **BUILD SUCCESSFUL** (5m 34s) y `connectedAndroidTest` → **BUILD SUCCESSFUL** (1m 25s, API 37 / JBR 25). Gradle 9.9.1 corrige 3 advisories (1 critical, 2 high). Los fallos previos de CI en #6/#7 se debían al workflow anterior (`setup-android@v3`), ya corregido en `main`.
- **Labels (2026-11-24):** creados y verificados `dependencies` (`#1d76db`) y `ci` (`#f61969`); 12 labels activos. Coinciden con `.github/dependabot.yml`, por lo que Dependabot ya no avisa de etiquetas faltantes.
- **Dependabot (2026-11-24):** mergeado PR **#9** (Gradle wrapper `9.9.1`→`9.9.2`). El patch corrige 4 advisories de seguridad (SFTP host keys, verificación de snapshots, TestKit y daemon registry). Verificación local: checks completos + `connectedAndroidTest` → **BUILD SUCCESSFUL**; CI del PR (run **#16**) → **Success** en 6m 45s.

## Estado final

**Setup completado:** toolchain Android, scaffold Compose, calidad (ktlint/detekt/lint), CI verde, Dependabot al día y labels configurados. Repo público en https://github.com/kallpasolucionesdigitales-svg/KallpaBase.

## Acciones siguientes (opcionales)

1. Abrir el repo en Android Studio (*File > Open* → raíz → Gradle Sync).
2. Crear rama de trabajo `feature/<nombre>` y empezar desarrollo.
3. Ante nuevos PRs de Dependabot, revisar el diff y esperar CI verde antes de mergear.

## Comandos de referencia (Paso 1)

```bash
# 1. Descargar el .tar.gz desde https://developer.android.com/studio
URL='https://edgedl.me.gvt1.com/android/studio/ide-zips/2026.2.2.10/android-studio-rabbit2-patch2-linux.tar.gz'
curl -L --fail -o /tmp/opencode/android-studio-rabbit2-patch2-linux.tar.gz "$URL"

# 2. Extraer en directorio del usuario (sin sudo)
mkdir -p "$HOME/.local/opt"
tar -xzf /tmp/opencode/android-studio-rabbit2-patch2-linux.tar.gz -C "$HOME/.local/opt"

# 3. Lanzar (GDK_BACKEND=x11 evita el crash de GTK en este entorno)
env DISPLAY=:0 GDK_BACKEND=x11 "$HOME/.local/opt/android-studio/bin/studio"

# 4. En el asistente: Standard → aceptar licencias → descargar SDK
# 5. Tools > Create Desktop Entry (opcional)
```

> Verificar después: `adb devices`, `sdkmanager --list`, y que el SDK quedó en `~/Android/Sdk` (o la ruta elegida en el wizard).
