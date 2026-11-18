# CampusGPS 📍

**CampusGPS** es una aplicación Android nativa desarrollada en **Kotlin** con **Jetpack Compose** y **Material 3 (100% sin XML)** que permite obtener la ubicación del usuario mediante los servicios de Google Play Services (`FusedLocationProviderClient`), visualizarla en un mapa interactivo libre y funcional con **OpenStreetMap (osmdroid)** sin requerir facturación ni tarjetas de crédito, y calcular las distancias en tiempo real hacia 5 puntos de interés del campus ficticio, ordenándolos dinámicamente de menor a mayor distancia y guiando hacia el punto más cercano.

---

## 🛠️ Tecnologías y Librerías

* **Lenguaje:** Kotlin 2.4.21 (JVM 17)
* **UI Framework:** Jetpack Compose con Material 3 y Compose BOM
* **Arquitectura:** MVVM (Model-View-ViewModel) + Repository Pattern
* **Localización:** Google Play Services Location (`FusedLocationProviderClient`)
* **Mapas (Alternativa Libre):** OpenStreetMap / osmdroid (`org.osmdroid:osmdroid-android:6.1.20`)
* **Concurrencia:** Kotlin Coroutines + Coroutines Play Services (`await()`) + StateFlow

---

## 🏛️ Arquitectura MVVM + Repository

```text
┌────────────────────────────────────────────────────────┐
│                      VIEW (UI)                         │
│  PantallaMapa.kt · MapaUbicacion.kt · ListaLugares.kt  │
│  MensajeError.kt                                       │
└──────────────────────────┬─────────────────────────────┘
                           │ Eventos de usuario (onClick, buscar, seleccionar)
                           │ Observa StateFlow con collectAsState()
                           ▼
┌────────────────────────────────────────────────────────┐
│                     VIEWMODEL                          │
│  MapaViewModel.kt                                      │
│  - estado: StateFlow<UbicacionUi>                      │
│  - lugares: StateFlow<List<LugarConDistancia>>         │
│  - seleccionado: StateFlow<Int?>                       │
│  - masCercano: StateFlow<Lugar?>                       │
└──────────────────────────┬─────────────────────────────┘
                           │ Invoca contratos de datos
                           ▼
┌────────────────────────────────────────────────────────┐
│                     REPOSITORY                         │
│  UbicacionRepository (Interface)                       │
│    └── UbicacionRepositoryGoogle (FusedClient + GPS)   │
│  LugaresRepository (Interface)                         │
│    └── LugaresRepositoryLocal (5 puntos del campus)    │
└────────────────────────────────────────────────────────┘
```

### Reglas de Responsabilidad por Capa:
* **View (`ui/`):** Conoce únicamente a su `ViewModel`. No accede a Repositories ni calcula distancias ni conoce `FusedLocationProviderClient`. Integra el mapa mediante `AndroidView` envolviendo `MapView` de osmdroid.
* **ViewModel (`viewmodel/`):** Conoce únicamente las interfaces de repositorio. **No conoce Composables ni recibe un objeto `Context`**. Expone estados inmutables mediante `StateFlow`.
* **Repository (`data/`):** Encapsula el acceso a fuentes de datos y servicios del sistema (como Google Play Services). No expone clases de UI.
* **Utilidades (`util/`):** Funciones puras e independientes de estado para cálculos matemáticos (`distanciaMetros`) y formateo de texto (`formatearDistancia`).

---

## 📁 Estructura del Proyecto

```text
com.tecsup.campusgps/
├── MainActivity.kt                  # Punto de entrada de la app, configuración de agente osmdroid y tema
├── model/
│   └── Modelos.kt                   # Posicion, Lugar, LugarConDistancia, TipoError, UbicacionUi
├── data/
│   ├── UbicacionRepository.kt       # Interface para obtención de coordenadas y verificación de Play Services
│   ├── UbicacionRepositoryGoogle.kt # Implementación usando FusedLocationProviderClient y coroutines await()
│   ├── LugaresRepository.kt         # Interface para obtención del catálogo de lugares
│   └── LugaresRepositoryLocal.kt    # Proveedor en memoria de los 5 puntos base del campus
├── util/
│   ├── Distancia.kt                 # Función pura distanciaMetros usando Location.distanceBetween
│   └── Formato.kt                   # Función pura formatearDistancia (metros y km formateados)
├── viewmodel/
│   ├── MapaViewModel.kt             # Lógica de presentación, StateFlows, ordenamiento y selección
│   └── CampusViewModelFactory.kt    # Factory desacoplado para inyectar repositorios al ViewModel
└── ui/
    ├── PantallaMapa.kt              # Pantalla principal con Scaffold, TopAppBar y gestión de permisos
    ├── MapaUbicacion.kt             # Componente de mapa con osmdroid MapView, animación de cámara y marcadores
    ├── ListaLugares.kt              # LazyColumn de tarjetas con distancias, indicador de color y selección
    ├── MensajeError.kt              # Recuadro accesible para permiso denegado, GPS apagado o sin Play Services
    └── theme/
        ├── Color.kt                 # Paleta de colores oficial (Azul primario, verde, contenedores de error)
        ├── Theme.kt                 # Configuración de MaterialTheme LightColorScheme
        └── Type.kt                  # Tipografía de la aplicación
```

---

## 📍 Puntos de Interés Ficticios del Campus

Coordenadas base de referencia: **-12.0464, -77.0428 (Lima)**

| Id | Nombre | Categoría | Latitud | Longitud |
|:--:|:-------|:----------|:-------:|:--------:|
| 1 | Biblioteca Central | Estudio | -12.0460 | -77.0435 |
| 2 | Cafetería Campus | Comida | -12.0470 | -77.0420 |
| 3 | Laboratorio de Cómputo | Estudio | -12.0455 | -77.0415 |
| 4 | Auditorio Principal | Eventos | -12.0480 | -77.0440 |
| 5 | Estacionamiento Norte | Servicios | -12.0440 | -77.0430 |

---

## 📱 Capturas de Pantalla

| 1. Solicitud de Permiso | 2. Ubicación en Mapa | 3. Marcadores del Campus |
|:---:|:---:|:---:|
| ![Permiso](docs/screenshots/01-permiso.png) | ![Mapa](docs/screenshots/02-mapa-ubicacion.png) | ![Marcadores](docs/screenshots/03-marcadores-campus.png) |

| 4. Lista Ordenada y Selección | 5. Manejo de Errores |
|:---:|:---:|
| ![Lista](docs/screenshots/04-lista-ordenada.png) | ![Error](docs/screenshots/05-error-permiso.png) |

---

## ✅ Lista de Verificación y Pruebas de Aceptación (Fase 1)

- [x] **Compilación limpia:** Compila de manera autónoma con Gradle (`compileDebugKotlin`, `assembleDebug`).
- [x] **Prueba 1:** Al pulsar el botón por primera vez, el sistema solicita permisos en tiempo de ejecución.
- [x] **Prueba 2:** Al conceder permiso, se obtienen las coordenadas y se visualiza el marcador "Estás aquí" en rojo.
- [x] **Prueba 3:** Si el usuario deniega el permiso, se muestra un mensaje de advertencia y el botón "Abrir ajustes" sin que la app crashee.
- [x] **Prueba 4:** Con el emulador en el punto base (-12.0464, -77.0428), se observan los 5 marcadores del campus sobre los mapas de OpenStreetMap.
- [x] **Prueba 5:** Al pulsar cualquier marcador, se despliega el InfoWindow con nombre y categoría.
- [x] **Prueba 6:** La lista inferior muestra los 5 lugares ordenados por distancia ascendente (en metros o kilómetros).
- [x] **Prueba 7:** Al tocar una tarjeta de la lista, la cámara del mapa se traslada suavemente al lugar con zoom 17.5 y la tarjeta se resalta con borde azul.
- [x] **Prueba 8:** El botón "Ir al más cercano" se habilita tras obtener la ubicación, conduce al punto más próximo y dicho marcador se destaca en color verde.
- [x] **Prueba 9:** Al cambiar las coordenadas en el emulador, la distancia y el lugar verde se recalculan y reordenan dinámicamente.
- [x] **Prueba 10:** Si la ubicación o el GPS están deshabilitados en el dispositivo, se muestra la tarjeta de error con el botón "Reintentar".
- [x] **Prueba 11:** La app sobrevive a la rotación de pantalla manteniendo el estado y la lista gracias a `ViewModel`.
- [x] **Prueba 12:** Uso de API libre de mapas: No requiere clave de pago ni facturación de Google Cloud, garantizando que el mapa siempre renderice sus mosaicos en cualquier dispositivo o emulador.

---

## 🧠 Preguntas de Reflexión ("Para Pensar")

### 1. ¿Por qué `FusedLocationProviderClient` está dentro del Repository y no en el ViewModel?
**Respuesta:**  
Por el principio de separación de responsabilidades y la arquitectura limpia de Android. `FusedLocationProviderClient` es una API dependiente del framework de Android y de Google Play Services que requiere un `Context` para instanciarse. 
El `ViewModel` debe ser agnóstico de las fuentes de datos concretas y nunca debe retener referencias a un `Context` (para evitar fugas de memoria). Al encapsular `FusedLocationProviderClient` dentro de `UbicacionRepositoryGoogle` tras la interface `UbicacionRepository`, el `ViewModel` puede ser probado con pruebas unitarias fácilmente inyectando un repositorio simulado (*fake* o *mock*), sin requerir emuladores ni APIs externas.

### 2. ¿Qué diferencia hay entre el permiso de ubicación y el servicio de mapas? ¿Qué pasa si falta cada uno?
**Respuesta:**  
* **Permiso de ubicación (`ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`):** Es una autorización que otorga el **usuario final** a nivel del sistema operativo Android en tiempo de ejecución para que la app pueda acceder al hardware de GPS y sensores. Si falta o se deniega, la app no puede leer las coordenadas satelitales y arroja una `SecurityException` (manejada mostrando la vista de error para abrir ajustes).
* **Servicio de mapas (OpenStreetMap / osmdroid):** Es el motor de renderizado de teselas cartográficas. Al utilizar OpenStreetMap mediante osmdroid, la aplicación descarga directamente los mapas de forma gratuita y libre sin requerir tarjetas de crédito ni claves privadas vulnerables, eliminando las restricciones de facturación de Google Maps. Si falta conexión a internet, se mostrarán las teselas cacheadas o la cuadrícula base, pero la ubicación satelital seguirá calculándose.

### 3. ¿Por qué es ventajoso usar OpenStreetMap frente a APIs con cobro obligatorio en proyectos educativos?
**Respuesta:**  
Google Maps Platform exige vincular una tarjeta de crédito a una cuenta de facturación en Google Cloud Console para generar una clave de API válida; sin ello, el mapa muestra una cuadrícula gris con marcas de agua o falla en cargar. OpenStreetMap es un proyecto colaborativo de datos abiertos que, a través de osmdroid, permite a los estudiantes y desarrolladores renderizar mapas completos, marcadores interactivos y controles multitáctiles de forma totalmente gratuita, transparente y reproducible en cualquier entorno de evaluación sin riesgos de fuga de credenciales ni bloqueos de facturación.

### 4. ¿Dónde calculas la distancia y por qué no en el Composable?
**Respuesta:**  
La distancia se calcula exclusivamente en el **`ViewModel`** (apoyado en la función pura `distanciaMetros` de `util/Distancia.kt`) al momento de recibir exitosamente la posición satelital del repositorio.
**No se calcula en el Composable** porque los Composables sufren múltiples recomposiciones durante el ciclo de vida de la pantalla (por animaciones, desplazamientos en la lista, cambios de foco o eventos de recomposición). Calcular distancias y reordenar listas dentro del Composable ralentizaría drásticamente el hilo principal (haciendo que la UI sufra caídas de cuadros/jank) y violaría la regla fundamental de Compose: **las funciones composables deben ser funciones puras de pintado que reflejen un estado inmutable provisto por el ViewModel**.

### 5. ¿Qué pasa con tu app si el usuario niega el permiso dos veces? ¿Cómo lo manejas?
**Respuesta:**  
A partir de Android 11, si el usuario deniega la solicitud de permiso dos veces (o presiona "No volver a preguntar" en versiones anteriores), el sistema operativo considera denegado el permiso de forma permanente y el cuadro de diálogo del sistema no volverá a mostrarse automáticamente aunque se invoque el lanzador.
En CampusGPS se maneja este escenario mostrando un estado claro de error (`TipoError.SIN_PERMISO`) con un contenedor visual descriptivo y el botón **"Abrir ajustes"**, el cual dispara un `Intent` directo a la pantalla de configuración de la aplicación en el sistema:
```kotlin
val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.fromParts("package", context.packageName, null)
}
context.startActivity(intent)
```
De este modo, el usuario puede habilitar el permiso manualmente sin frustración y al regresar a la app, al pulsar nuevamente "Obtener mi ubicación", la aplicación detectará el permiso concedido sin necesidad de reinstalarla.
