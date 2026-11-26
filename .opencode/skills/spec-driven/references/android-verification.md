# Verificación Android en KallpaBase

## Puerta local obligatoria

```bash
./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug
```

Debe terminar en `BUILD SUCCESSFUL` sin warnings nuevos de ktlint, detekt ni Android Lint.

## Tests instrumentados

```bash
adb devices
./gradlew connectedAndroidTest
```

- AVD: `KallpaBase_API37` (Pixel 9, x86_64, Google APIs Play Store).
- El emulador debe responder `emulator-5554 device` antes de lanzar los tests.
- Si el AVD no está arrancado, reportar FAIL con ese motivo; no instalar ni desinstalar APKs desde el agente verificador.

## Qué significa "verde"

- `ktlintCheck`, `detekt`, `lint`, `test` y `assembleDebug` pasan localmente.
- `connectedAndroidTest` pasa cuando la spec incluye cambios de UI o instrumentación.
- El workflow `.github/workflows/android.yml` pasa en el PR con ambos jobs (`build` e `instrumented-test`).

## Fuera de la puerta por defecto

- `assembleRelease`: requiere el keystore en `~/.android/kallpabase-release.jks` y credenciales en `~/.gradle/kallpabase-keystore.properties` (modo 600), ambos fuera del repo. No ejecutar desde el verificador.
- `git push` y publicación de artefactos: solo con confirmación humana.

## Fallos habituales

| Síntoma | Causa probable | Acción |
| --- | --- | --- |
| `compileSdk` insuficiente | Compose 1.13 exige `compileSdk >= 37.1` | Verificar `release(37) { minorApiLevel = 2 }` en `app/build.gradle.kts`. |
| `java.lang.UnsupportedClassVersionError` / `--jvm-target 25` | detekt 1.x con JBR 25 | Mantener detekt `2.0.0-alpha.6` del version catalog. |
| `Timeout waiting for emulator to boot` en CI | Falta KVM o timeout corto | Revisar el paso KVM y `emulator-boot-timeout: 1200` en el workflow. |
| Dependencia no resuelta | Falta declararla en el catálogo | Añadir alias en `gradle/libs.versions.toml`, no versión literal. |
