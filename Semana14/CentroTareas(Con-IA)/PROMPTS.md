# Registro de Consultas y Decisiones Técnicas con IA (PROMPTS.md)

Este documento registra el proceso de diseño, consulta técnica y adaptación arquitectónica de las **3 mejoras profesionales** implementadas en la **Fase 2 (Con IA)** para la aplicación **CentroTareas**, garantizando el cumplimiento estricto del patrón **MVVM + Repository**, las mejores prácticas de **Kotlin Coroutines** y la reactividad con **Jetpack Compose** y **StateFlow**.

---

## 📊 Tabla de Prompts y Criterio Técnico

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué (Criterio Técnico) |
|---|---|---|---|
| *«¿Cómo desacoplar la emisión del progreso de la tarea larga del ViewModel para que provenga directamente de la capa de datos/repositorio usando Kotlin Coroutines y Flow?»* | Sugirió utilizar un `callback` clásico de Java o lanzar una corrutina con `GlobalScope.launch` que actualice un `MutableSharedFlow` global en el repositorio. | **Descarté por completo `GlobalScope` y callbacks heredados**. Implementé `ejecutarTareaConProgreso(): Flow<Float> = flow { ... }.flowOn(Dispatchers.Default)` dentro de `TareasRepository` y `TareasRepositoryLocal`. En `TareasViewModel`, la corrutina en `viewModelScope` recolecta de forma limpia el flujo mediante `collect` y actualiza `_progreso.value` y `_mensaje.value`. | Respeta la arquitectura limpia (Clean Architecture) y MVVM. El Repository debe ser el dueño del ciclo de datos y exponer flujos fríos (*cold streams*) mediante `Flow`. El operador `flowOn(Dispatchers.Default)` asegura que la emisión ocurra en un hilo de background, mientras que el ViewModel decide en qué scope y despachador consumir los datos, manteniendo la cancelación cooperativa y evitando fugas de memoria (*memory leaks*). |
| *«¿Cómo agregar una barra de progreso que muestre en tiempo real el porcentaje de avance (0% a 100%) durante el cálculo intensivo de números primos sin ralentizar el procesamiento?»* | Propuso emitir una actualización de estado en cada iteración del bucle `for (n in 2..hasta)` llamando a un `MutableStateFlow` directamente desde el algoritmo matemático. | **Descarté actualizar el estado en cada número analizado**. Para $N = 500,000$, disparar 500,000 recomposiciones saturaría el hilo principal de Compose provocando congelamiento visual (*jank*). En su lugar, implementé `contarPrimosConProgreso(hasta: Int, onProgreso: (Float) -> Unit)` calculando el paso de reporte (`paso = maxOf(1, hasta / 100)`) para notificar a la UI únicamente cuando se avanza al menos un 1% entero. Además, se preservó `coroutineContext.ensureActive()` en cada iteración para cancelación instantánea. | Reducir la frecuencia de eventos a un reporte porcentual por umbral (muestreo por pasos discretos) optimiza el cómputo intensivo CPU-bound en `Dispatchers.Default` y previene el desbordamiento de eventos (*backpressure*) en el hilo principal (`Main Thread`), logrando una animación fluida de la barra de progreso a 60 FPS sin degradar la velocidad de cálculo. |
| *«¿Cómo evitar que la pantalla quede colgada indefinidamente si una petición concurrente tarda demasiado o falla, implementando un mecanismo de reintento automático con Kotlin Coroutines?»* | Propuso usar un bucle `while(true)` infinito con `Thread.sleep` para reintentar la operación hasta que tenga éxito y capturar `Throwable` de forma genérica sin relanzar excepciones. | **Descarté rotundamente `Thread.sleep`, los bucles infinitos y capturar `Throwable` sin filtrar**. Implementé `cargarConReintentoAutomatico(intentosMaximos = 3)` con control de tiempo límite por intento mediante `withTimeout(4000L)`. Si el tiempo expira (`TimeoutCancellationException`) o la red simula error, se captura específicamente, se espera con `delay(intentoActual * 600L)` (backoff exponencial/progresivo no bloqueante) y se reintenta hasta 3 veces antes de pasar al estado `CargaUi.Error`. Siempre se relanza `CancellationException`. En la UI se agregó el botón "Reintento 3x" y el reintento con backoff en caso de error. | Las peticiones asíncronas deben contar con políticas de timeout para no colgar recursos. El uso de `withTimeout` cancela cooperativamente el árbol de corrutinas (`async`) si se sobrepasa el límite. El backoff progresivo con `delay` previene tormentas de reintentos (*retry storms*) respetando la suspensión cooperativa y asegurando que las cancelaciones del ciclo de vida (`CancellationException`) siempre se propaguen al `viewModelScope`. |

---

## 🛡️ Justificación Técnica y Arquitectónica de las Mejoras

### 1. Desacoplamiento de Flujos de Datos con `Flow` y `flowOn`
* **Principio de Responsabilidad Única (SRP):** El ViewModel no debe simular contadores ni temporizadores de negocio en sus funciones internas. El cálculo o generación del progreso pertenece a la fuente de datos (`Repository`).
* **Preservación del Contexto:** Al utilizar `flowOn(Dispatchers.Default)`, garantizamos que la emisión de datos ocurra fuera del hilo de interfaz de usuario. Al recolectar con `viewModelScope.launch`, el consumo se ejecuta en el despachador del scope (por defecto `Dispatchers.Main.immediate`), eliminando la necesidad de cambios manuales de contexto en la capa de presentación.

### 2. Muestreo Discreto y Cancelación Cooperativa en Algoritmos Pesados
* **Control de Backpressure y Rendimiento:** La recomposición en Jetpack Compose es sumamente eficiente, pero despachar cientos de miles de estados por segundo al `StateFlow` satura el despachador de eventos del hilo principal. Al cuantizar el reporte al 1% de resolución, la interfaz gráfica se actualiza exactamente 100 veces, logrando fluidez perfecta y dejando la CPU libre para ejecutar el test de primalidad a máxima velocidad.
* **Cancelación Instantánea con `ensureActive()`:** Si el usuario presiona "Cancelar" o abandona la pantalla durante el cálculo de $N = 500,000$, la función `coroutineContext.ensureActive()` detecta la cancelación del `Job` en la siguiente iteración y detiene el trabajo inmediatamente sin desperdiciar batería.

### 3. Resiliencia en Peticiones Concurrentes con `withTimeout` y Backoff Progresivo
* **Prevención de Tareas Huérfanas (*Hanging Tasks*):** Si un servicio remoto o base de datos no responde, `withTimeout` cancela automáticamente las corrutinas creadas con `async` dentro del `coroutineScope`, liberando sockets y memoria.
* **Tolerancia a Fallos:** En entornos móviles donde la conectividad es inestable (ej. túneles, cambio de Wi-Fi a datos), los reintentos automáticos con espera exponencial progresiva (`delay(intento * factor)`) resuelven fallas transitorias de forma transparente para el usuario sin saturar la red ni bloquear la aplicación.

---

## ⚖️ Principios Arquitectónicos Intactos en la Fase 2

1. **Cero `Context` en el ViewModel:** La capa de presentación y lógica permanece desacoplada de Android y es 100% testeable mediante pruebas unitarias.
2. **Cero `GlobalScope`, `runBlocking` o `Thread.sleep`:** Todas las operaciones asíncronas respetan el ciclo de vida del `ViewModel` a través de `viewModelScope` y la suspensión no bloqueante de `delay`.
3. **Manejo Estricto de `CancellationException`:** Nunca se silencia ni se captura genéricamente la excepción de cancelación de corrutinas; siempre se relanza para preservar la estructura jerárquica de corrutinas estructuradas de Kotlin (*Structured Concurrency*).
4. **Estado Unidireccional (UDF):** La UI solo observa `StateFlow` inmutables y emite eventos hacia el ViewModel.
