# 003 — Menú principal

- Estado: cerrado
- Rama: `feature/003-menu-principal`
- PR: https://github.com/kallpasolucionesdigitales-svg/KallpaBase/pull/13
- Fecha: 2026-11-26
- Relación: F-23 del roadmap (módulo `Transversal`).

## Contexto

Después de la spec 002, `MainActivity` muestra `ClientsScreen` directamente, sin ninguna estructura de navegación. A medida que se implementen presupuestos, operaciones y configuración, hace falta un “shell” principal que permita acceder a cada módulo sin reedificar la pantalla raíz.

El menú debe mostrar la estructura completa de la aplicación desde el inicio, pero no debe llevar a pantallas inexistentes: los destinos aún no implementados se muestran visibles y deshabilitados con una indicación clara de su estado.

## Objetivos

- Definir la pantalla principal como un `Scaffold` de Material 3 con una barra inferior de cuatro destinos.
- Mantener `Clientes` como destino inicial y preservar el comportamiento y los tests de la spec 002.
- Hacer visibles los módulos futuros sin crear callejones sin salida.
- Preparar la estructura para habilitar destinos a medida que sus specs se verifiquen.

## No-objetivos

- No implementar las pantallas de `Presupuestos`, `Operaciones` ni `Configuración`.
- No implementar búsqueda (F-02), sincronización (F-15), reportes (F-13), exportación (F-14) ni “Acerca de”.
- No añadir `Navigation Compose`, deep links ni back stack propio; la navegación se resuelve con estado local.
- No mover el botón `Nuevo cliente` a un FAB global: permanece dentro de `ClientsScreen`.
- No modificar la funcionalidad ni los tests instrumentados de la spec 002. La sincronización de estados documentales de specs ya cerradas (spec 002, índice y roadmap) se realizó en un commit `docs:` previo y separado, fuera del cambio funcional de esta spec.

## Requisitos funcionales

- RF-1: La pantalla principal muestra una `NavigationBar` inferior con cuatro destinos: `Clientes`, `Presupuestos`, `Operaciones` y `Configuración`.
- RF-2: `Clientes` es el destino seleccionado al iniciar y muestra el `ClientsScreen` existente con su repositorio local.
- RF-3: `Clientes` está habilitado; `Presupuestos`, `Operaciones` y `Configuración` están deshabilitados hasta que la spec de su pantalla tenga estado `verificado`.
- RF-4: Los destinos deshabilitados permanecen visibles, muestran un indicador `Próximamente` / `Fase 2` y no cambian de pantalla al pulsarlos.
- RF-5: El `TopAppBar` muestra el título del destino activo (`Clientes` al inicio).
- RF-6: El `TopAppBar` muestra las acciones `Buscar` y `Sincronizar`, y un menú overflow con `Reportes`, `Exportar` y `Acerca de`; todas permanecen deshabilitadas mientras sus funcionalidades no existan.
- RF-7: Cada destino y acción expone semántica accesible; los elementos deshabilitados indican su estado en el `contentDescription`.
- RF-8: La selección del destino se conserva al rotar el dispositivo (`rememberSaveable`).

## Requisitos no funcionales

- RNF-1: Usar `Scaffold`, `TopAppBar` y `NavigationBar` de Material 3, compatible con `minSdk 24` y Compose.
- RNF-2: No añadir dependencias nuevas; `gradle/libs.versions.toml` y `app/build.gradle.kts` no cambian en esta spec.
- RNF-3: El composable raíz (`MainScreen`) recibe el repositorio de clientes y lo pasa a `ClientsScreen` sin alterar su API.
- RNF-4: Los tests existentes (`ClientListTest`, `ClientFormTest`, `ClientEditTest`, `ClientDeleteTest`) invocan `ClientsScreen` directamente y siguen pasando sin modificación.
- RNF-5: `ktlintCheck`, `detekt`, `lint`, `test` y `assembleDebug` pasan, y `connectedAndroidTest` queda verde en el AVD `KallpaBase_API37`.

## Criterios de aceptación

- [x] CA-1: Dado el arranque de la aplicación, cuando se muestra `MainScreen` (composable raíz invocado por `MainActivity`), entonces aparecen los cuatro destinos en la `NavigationBar`, `Clientes` está seleccionado y se ve `ClientsScreen`. Verificación: `MainMenuTest.menuInicial_muestraCuatroDestinos` con `createComposeRule` (API v2); el cableado de `MainActivity` se comprueba por revisión de código y `assembleDebug`.
- [x] CA-2: Dado el menú inicial, cuando se pulsa `Presupuestos`, `Operaciones` o `Configuración`, entonces el destino activo sigue siendo `Clientes` y no se muestra ninguna pantalla nueva. Verificación: `MainMenuTest.destinoDeshabilitado_noNavega`.
- [x] CA-3: Dado el menú inicial, cuando se inspeccionan los destinos deshabilitados, entonces siguen visibles con el indicador `Próximamente` / `Fase 2` y su semántica indica que no están disponibles. Verificación: `MainMenuTest.destinosFuturos_visiblesYDeshabilitados`.
- [x] CA-4: Dado `Clientes` como destino activo, cuando se muestra la pantalla, entonces el `TopAppBar` muestra `Clientes` y las acciones `Buscar` y `Sincronizar` están deshabilitadas con su `contentDescription`. Verificación: `MainMenuTest.barraSuperior_estadoInicial`.
- [x] CA-5: Dado el shell de navegación nuevo, cuando se ejecutan los tests de la spec 002, entonces `ClientListTest`, `ClientFormTest`, `ClientEditTest` y `ClientDeleteTest` pasan sin cambios. Verificación: `./gradlew connectedAndroidTest` en `KallpaBase_API37`.
- [x] CA-6: Dado el cambio completo, cuando se ejecuta la puerta local, entonces termina en `BUILD SUCCESSFUL`. Verificación: `./gradlew --no-daemon ktlintCheck detekt lint test assembleDebug`.

## Pruebas requeridas

- Unitarias: no aplica; la navegación no introduce lógica de negocio nueva.
- Compose UI: `app/src/androidTest/java/com/kallpa/base/ui/MainMenuTest.kt` con `createComposeRule` (API v2): cuatro destinos, selección inicial, destinos deshabilitados que no navegan, título de la barra superior y semántica accesible.
- Instrumentadas: los tests Compose anteriores se ejecutan con `./gradlew connectedAndroidTest` en el AVD `KallpaBase_API37`; se incluye la regresión de los tests de clientes de la spec 002.

## Riesgos y decisiones abiertas

- Los ítems deshabilitados no responden a pulsaciones; si se quiere feedback al tocarlos, habrá que cambiar la decisión a “habilitados con pantalla de próximamente” y actualizar esta spec.
- Al verificarse la spec de `Presupuestos` (F-04), su destino debe pasar a habilitado y esta spec debe actualizarse junto con el roadmap.
- El contenido de `Acerca de` y el diseño del overflow quedan por definir; por ahora solo se comprueba que las acciones están presentes y deshabilitadas.
- `Navigation Compose` se incorporará más adelante si aparecen deep links o un back stack con múltiples niveles.
