# CentroTareas: que la pantalla nunca se congele ⚡

**CentroTareas** es una aplicación Android nativa desarrollada en **Kotlin** con **Jetpack Compose** y **Material 3 (100% sin XML)**, estructurada bajo el patrón **MVVM + Repository** y potenciada por **Corrutinas de Kotlin** y **StateFlow**. Su propósito fundamental es demostrar y aplicar las mejores prácticas para la ejecución de tareas asíncronas y cálculos intensivos en segundo plano, garantizando que el hilo principal (*Main Thread* / *UI Thread*) permanezca siempre libre y fluido para atender las interacciones del usuario sin congelamientos ni bloqueos de interfaz.

---

## 🛠️ Tecnologías y Librerías

* **Lenguaje:** Kotlin 2.4.21 (JVM 17)
* **UI Framework:** Jetpack Compose con Material 3 y Compose BOM
* **Arquitectura:** MVVM (Model-View-ViewModel) + Repository Pattern (Clean Architecture)
* **Concurrencia:** Kotlin Coroutines (`launch`, `async`, `await`, `withContext`, `coroutineScope`)
* **Gestión de Ciclo de Vida y Estado:** `viewModelScope`, `StateFlow`, `collectAsState()`
* **Despachadores:** `Dispatchers.Main` (UI) y `Dispatchers.Default` (CPU-bound & Delays)

---

## 🏛️ Arquitectura MVVM + Repository

```text
┌────────────────────────────────────────────────────────────────────────┐
│                               VIEW (UI)                                │
│  PantallaTareas.kt (Tarjetas Compose, Scaffold, TopAppBar, Buttons)   │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Eventos de usuario (onClick, toques, filtros)
                                    │ Observa flujos StateFlow con collectAsState()
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                              VIEWMODEL                                 │
│  TareasViewModel.kt                                                    │
│  - mensaje: StateFlow<String>                                          │
│  - progreso: StateFlow<Float>                                          │
│  - corriendo: StateFlow<Boolean>                                       │
│  - primos: StateFlow<PrimosUi>                                         │
│  - carga: StateFlow<CargaUi>                                           │
│  - simularError: StateFlow<Boolean>                                    │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Invoca operaciones asíncronas suspend
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                             REPOSITORY                                 │
│  TareasRepository (Interface)                                          │
│    └── TareasRepositoryLocal (Cálculo de Primos & Cargas Simuladas)    │
└────────────────────────────────────────────────────────────────────────┘
```

### Reglas de Responsabilidad por Capa:
* **View (`ui/`):** Conoce únicamente a su `ViewModel`. No lanza corrutinas directamente ni bloquea hilos; se limita a renderizar el estado inmutable y reportar eventos.
* **ViewModel (`viewmodel/`):** Conoce únicamente la interfaz del `Repository`. No conoce clases de UI ni contiene objetos `Context`. Administra `viewModelScope` y expone estados reactivos con `StateFlow`.
* **Repository (`data/`):** Desacoplado de la interfaz de usuario. Sus funciones `suspend` delegan el trabajo intensivo a despachadores de segundo plano (`Dispatchers.Default`) y respetan la cancelación cooperativa con `ensureActive()`.

---

## 📁 Estructura del Proyecto

```text
com.tecsup.centrotareas/
├── MainActivity.kt                      # Punto de entrada de la aplicación y configuración de tema
├── model/
│   └── Estados.kt                       # Sealed interfaces: PrimosUi y CargaUi
├── data/
│   ├── TareasRepository.kt              # Interface de abstracción de datos y tareas
│   └── TareasRepositoryLocal.kt         # Implementación local con cálculo de primos y cargas simuladas
├── viewmodel/
│   ├── TareasViewModel.kt               # Lógica de negocio, gestión de Job y corrutinas
│   └── TareasViewModelFactory.kt        # Fábrica desacoplada para instanciación del ViewModel
└── ui/
    ├── PantallaTareas.kt                # UI principal con 3 tarjetas funcionales y botón de prueba de vida
    └── theme/
        ├── Color.kt                     # Paleta oficial (Azul principal, verde éxito, rojo error)
        ├── Theme.kt                     # LightColorScheme de Material 3
        └── Type.kt                      # Tipografía de la app
```

---

## 🔍 Observación del Paso 1 (Tarea larga en el hilo principal)

### 1. ¿Qué pasa con la pantalla durante la ejecución de `tareaLargaMala()`?
Durante los 4 segundos en los que se invoca `Thread.sleep(4000)`, la aplicación deja de responder por completo: las animaciones se congelan, las pulsaciones sobre la pantalla no producen ningún efecto visual y el botón de prueba de vida (**«Tócame»**) no incrementa su contador. La pantalla entra en un estado de congelamiento total (*UI Freeze*).

### 2. ¿Por qué no se ve el texto «Trabajando...» en la pantalla?
Porque en Compose (al igual que en el sistema de vistas de Android), las modificaciones de estado programadas en un hilo de ejecución no se dibujan inmediatamente en el píxel; requieren que el hilo principal ejecute el ciclo de recomposición, medición y dibujo (*Frame rendering* a 60/120 Hz). Al llamar a `Thread.sleep(4000)` en el hilo principal inmediatamente después de actualizar `_mensaje.value = "Trabajando..."`, se bloquea el hilo antes de que este tenga la oportunidad de procesar la recomposición en el siguiente cuadro. Cuando `sleep` termina y el código reanuda su ejecución en la siguiente línea, el mensaje cambia de inmediato a `"Terminó"`, por lo que el usuario solo llega a ver este último estado.

### 3. ¿Qué crees que mostraría Android si la espera fuera de 10 segundos?
El sistema operativo Android cuenta con un vigilante (*Watchdog*) en el hilo principal. Si el hilo principal permanece bloqueado y no despacha eventos de entrada o intenciones durante más de 5 segundos, Android activa el diálogo del sistema **ANR (Application Not Responding)**, ofreciendo al usuario la opción de forzar el cierre de la app o esperar, degradando severamente la experiencia de usuario y penalizando la app en Google Play Vitals.

---

## 📱 Capturas de Pantalla de la Aplicación

| 1. Tarea con Progreso (62%) | 2. Tarea Cancelada (40%) | 3. Cálculo de Primos |
|:---:|:---:|:---:|
| ![Progreso](docs/screenshots/01-progreso.png) | ![Cancelado](docs/screenshots/02-cancelado.png) | ![Primos](docs/screenshots/03-primos.png) |

| 4. Dos Cargas a la Vez (async) | 5. Simulación de Error y Reintento |
|:---:|:---:|
| ![Cargas](docs/screenshots/04-cargas-async.png) | ![Error](docs/screenshots/05-error-reintento.png) |

---

## ✅ Lista de Verificación y Pruebas de Aceptación (Fase 1)

- [x] **Tarea larga sin bloqueo:** Al ejecutar la tarea con progreso, pulsar el botón «Tócame» continúa sumando los toques en tiempo real sin interrupciones.
- [x] **Inspección en Logcat (filtro HILO):** Se evidencia en consola `Antes: main`, `Dentro: DefaultDispatcher-worker-...` y `Despues: main`.
- [x] **Barra de progreso reactiva:** La barra de progreso avanza suavemente de 0 a 100 % en aproximadamente 4 segundos.
- [x] **Cancelación con Job:** Al pulsar «Cancelar» a la mitad del proceso, la barra se detiene en su posición actual, el texto indica «Cancelado» y el botón «Iniciar» vuelve a habilitarse.
- [x] **Protección contra doble inicio:** Mientras la tarea se encuentra corriendo, el botón «Iniciar» permanece inhabilitado, impidiendo lanzamientos concurrentes no deseados.
- [x] **Cálculo de números primos:** Con N = 1000 devuelve 168 primos; con N = 500 000 devuelve 41 538 primos comprobados.
- [x] **Cancelación de cálculo intensivo:** Al calcular primos con N grande, la pantalla no se congela y pulsar «Cancelar» detiene la computación gracias a `ensureActive()`.
- [x] **Carga secuencial («Una por una»):** Ejecuta la carga de perfil (2 s) y pedidos (3 s) en serie, completando en aproximadamente 5000 ms.
- [x] **Carga paralela («A la vez» con `async`):** Ejecuta ambas peticiones concurrentemente con `async` y `await()`, completando en aproximadamente 3000 ms (el tiempo de la tarea más lenta).
- [x] **Manejo de errores y reintento:** Al activar el interruptor «Simular error», la carga captura la excepción, muestra el contenedor rojo con el mensaje de error y el botón «Reintentar» permite reanudarla exitosamente tras desactivar el interruptor.
- [x] **Sobrevivencia a rotación de pantalla:** Al girar el dispositivo mientras una tarea está en curso, el `ViewModel` preserva el estado, la tarea continúa y no se reinicia el progreso.
- [x] **Auditoría de código limpio:** El comando `git grep` confirma que en el código final no existen instancias de `GlobalScope`, `runBlocking` ni `Thread.sleep`.

---

## 🧠 Preguntas de Reflexión ("Para Pensar")

### 1. ¿Por qué no se veía Trabajando... en el Paso 1?
Porque el hilo principal (*Main Thread*) es el encargado de procesar la cola de eventos de la interfaz gráfica y renderizar los cuadros de recomposición de Jetpack Compose. Al ejecutar `Thread.sleep(4000)`, se congeló el hilo antes de que pudiera ejecutar el frame de redibujado para `"Trabajando..."`. Cuando el hilo despertó, el estado cambió inmediatamente a `"Terminó"`, omitiendo el pintado intermedio.

### 2. ¿Qué diferencia hay entre `Thread.sleep` y `delay`?
* **`Thread.sleep(ms)`:** Es una llamada bloqueante del sistema operativo que detiene por completo el hilo físico actual, impidiendo que dicho hilo procese cualquier otra instrucción o evento de la interfaz.
* **`delay(ms)`:** Es una función de suspensión propia de Kotlin Coroutines. **No bloquea el hilo físico**, sino que suspende temporalmente la corrutina que la invocó y libera el hilo subyacente para que este pueda continuar ejecutando otras tareas, animaciones o eventos de usuario, reanudando la corrutina cuando el temporizador expira.

### 3. ¿Por qué `Dispatchers.Default` para los primos y `Dispatchers.IO` para la red?
* **`Dispatchers.Default`:** Está respaldado por un grupo de hilos cuyo tamaño equivale al número de núcleos del procesador (CPU cores). Está optimizado para operaciones intensivas en cómputo (*CPU-bound*), como cálculos matemáticos (números primos), ordenamiento de listas masivas o procesamiento de algoritmos.
* **`Dispatchers.IO`:** Está diseñado para operaciones de entrada/salida bloqueantes (*I/O-bound*), como lectura/escritura en disco, base de datos local o peticiones de red. Su grupo de hilos puede crecer elásticamente (hasta 64 hilos o más) para evitar que hilos en espera de respuestas externas bloqueen la capacidad de computación de la app.

### 4. ¿Qué pasaría con la corrutina si el usuario cierra la pantalla? ¿Qué papel cumple `viewModelScope`?
Si se utilizara un alcance no acoplado como `GlobalScope`, la corrutina continuaría ejecutándose en segundo plano consumiendo memoria, batería y procesador innecesariamente, e incluso provocando fugas de memoria (*memory leaks*) o excepciones al intentar actualizar un estado inexistente.  
`viewModelScope` es un `CoroutineScope` provisto por Android Jetpack que está directamente vinculado al ciclo de vida del `ViewModel`. Cuando la pantalla se destruye de forma definitiva (ej. el usuario presiona "Atrás"), el método `onCleared()` del ViewModel se ejecuta automáticamente y **cancela todas las corrutinas hijas activas dentro de su scope**, garantizando una liberación de recursos limpia y segura.

### 5. ¿Por qué `async` tarda ≈ 3 s y no ≈ 5 s? ¿Qué pasa si una de las dos cargas falla?
* **Por qué tarda ≈ 3 s:** Porque en lugar de esperar secuencialmente a que termine la primera carga (2 s) para recién iniciar la segunda (3 s), la función `async` despacha ambas corrutinas de manera concurrente al despachador. Ambas corren en paralelo durante los primeros 2 segundos, y cuando concluye el perfil, los pedidos continúan 1 segundo más hasta finalizar, resultando en un tiempo total de `max(2s, 3s) ≈ 3s`.
* **Qué pasa si una falla:** Dentro de un bloque `coroutineScope`, si cualquiera de las dos llamadas lanza una excepción no controlada, esa falla se propaga de inmediato al scope padre cancelando cooperativamente a las demás corrutinas hermanas pendientes y propagando la excepción al bloque `try / catch` que lo envuelve, evitando que queden procesos huérfanos trabajando en segundo plano.
