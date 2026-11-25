# Registro de Consultas y Decisiones Técnicas con IA (PROMPTS.md)

Este documento detalla el proceso de ingeniería de prompts, análisis crítico y adaptación de las **3 mejoras profesionales** implementadas en la **Fase 2 (Con IA)** para la aplicación **MuroCampus**, asegurando la estricta adherencia al patrón arquitectónico **MVVM + Repository**, el principio de flujo unidireccional de datos (UDF) y las buenas prácticas de concurrencia estructurada con **Kotlin Coroutines** y **Jetpack Compose**.

---

## 📊 Tabla de Prompts y Criterio Técnico

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué (Criterio Técnico) |
|---|---|---|---|
| *«¿Cómo configurar OkHttp en RetrofitClient para que todas las peticiones lleven la cabecera Accept: application/json de forma automática?»* | Sugirió crear un método auxiliar que reciba cada llamada o modificar la URL agregando parámetros de consulta, o alternativamente pasar `@Header("Accept")` en cada método de `PostApi`. | **Descarté duplicar anotaciones `@Header` en cada método de la interfaz y descarté modificar manualmente los parámetros en cada llamada.** En su lugar, implementé un `Interceptor` de nivel de aplicación en `OkHttpClient.Builder()` dentro de `RetrofitClient`. El interceptor intercepta la cadena (`chain.request()`), clona la petición mediante `.newBuilder()` y estandariza `Accept: application/json` y `User-Agent: MuroCampus-Android-Client/1.0` en una única capa centralizada. | Centralizar la configuración de cabeceras HTTP mediante un interceptor de OkHttp respeta el principio DRY (*Don't Repeat Yourself*) y el principio de responsabilidad única (SRP). La interfaz `PostApi` debe permanecer limpia, declarando únicamente rutas y verbos REST de dominio sin preocuparse por la fontanería de red. Además, garantiza que cualquier nueva llamada agregada a futuro herede automáticamente los estándares de la API sin riesgo de omisión. |
| *«¿Cómo implementar la eliminación de una publicación con @DELETE("posts/{id}") en Retrofit de modo que el ViewModel sepa si fue exitoso o falló sin violar la arquitectura?»* | Sugirió que la interface de Retrofit devuelva `Response<Unit>` y que el `ViewModel` invoque directamente a `api.eliminar(id)` para evaluar `response.code() == 200` y atrapar `HttpException`. | **Descarté rotundamente que el ViewModel conozca `PostApi`, `Response` o `HttpException`.** Se respetaron las Reglas de Oro del laboratorio: `PostApi` declara `suspend fun eliminar(@Path("id") id: Int): Response<Unit>`, pero es el `PostRepositoryRemote` quien consume `Response<Unit>`, evalúa `respuesta.isSuccessful`, y traduce el resultado a `Result.success(Unit)` o `Result.failure(HttpException(respuesta))`, relanzando siempre `CancellationException`. El `PostsViewModel` únicamente interactúa con `PostRepository.eliminar(id)` recibiendo `Result<Unit>`, actualizando `_eliminarEstado` y filtrando la lista en memoria si la operación fue exitosa. En la UI (`PantallaDetalle`) se agregó un diálogo modal de confirmación antes de disparar la acción. | Mantener las capas desacopladas es fundamental. El ViewModel jamás debe importar clases de Retrofit (`Response`, `HttpException`, `Call`) ni saber cómo se ejecuta la comunicación HTTP. Al transformar la respuesta técnica en un `Result<Unit>` de la biblioteca estándar de Kotlin, el ViewModel se mantiene 100% testeable en pruebas unitarias de JVM pura sin necesidad de emular componentes de red de Android. |
| *«¿Cómo implementar un buscador en tiempo real en la pantalla de publicaciones que filtre por texto el título o contenido sin disparar peticiones de red innecesarias a JSONPlaceholder?»* | Propuso escuchar el cambio de texto del `TextField` y ejecutar una llamada a la API con un `@Query("q")` o ejecutar un bucle `for` manual dentro del Composable que recalcule una nueva lista mutable con `remember`. | **Descarté lanzar llamadas HTTP a la API en cada pulsación de tecla y descarté realizar filtrados imperativos dentro del Composable.** Implementé un enfoque completamente reactivo en el ViewModel combinando flujos con `combine(_lista, _busqueda)`. Si `_lista.value` está en `UiState.Exito`, se aplica el predicado `.filter { it.title.contains(query, true) \|\| it.body.contains(query, true) }` de forma no bloqueante y se expone a la UI como `listaFiltrada: StateFlow<UiState<List<Post>>>` mediante `.stateIn()`. En `PantallaLista`, Compose simplemente recolecta `listaFiltrada` con `collectAsStateWithLifecycle()`, mostrando un mensaje contextual si no hay coincidencias. | Los datos ya fueron descargados en memoria local (100 posts o los del usuario seleccionado). Hacer peticiones de red para filtrar texto estático malgasta ancho de banda y batería del dispositivo móvil. Utilizar operadores de `Flow` como `combine` y exponer un `StateFlow` mantiene la UI puramente declarativa, libre de lógica de negocio y evita recomposiciones innecesarias o inconsistencias de estado. |

---

## 🛡️ Justificación Técnica y Arquitectónica de las Mejoras

### 1. Interceptor de OkHttp (`RetrofitClient`)
* **Propósito**: Estandarizar la comunicación con la plataforma REST asegurando que el servidor sepa que el cliente móvil espera respuestas en formato JSON (`Accept: application/json`).
* **Seguridad y Trazabilidad**: Se incorporó además la cabecera `User-Agent` que identifica la aplicación cliente en los logs del servidor. El interceptor funciona en tándem con el `HttpLoggingInterceptor` sin alterar el cuerpo ni la autenticación de las peticiones.

### 2. Eliminación con `@DELETE` y `Response<Unit>` (`PostApi` + `Repository` + `ViewModel` + `View`)
* **Semántica REST**: El verbo `DELETE` sobre `/posts/{id}` solicita la destrucción del recurso identificado. JSONPlaceholder devuelve código `200 OK` con un cuerpo JSON vacío `{}`.
* **Manejo de Respuestas sin Cuerpo**: Al devolver `Response<Unit>`, Retrofit no intenta deserializar un objeto JSON complejo, permitiendo a la capa de datos comprobar `respuesta.isSuccessful` e inspeccionar el código HTTP exacto (`200`, `204`, `404`).
* **Sincronización Local Reactiva**: Cuando el repositorio confirma la eliminación exitosa en el servidor simulado, el ViewModel actualiza de inmediato el `_lista.value` excluyendo el `post` eliminado (`datos.filter { it.id != id }`), brindando al usuario una experiencia instantánea al regresar a la lista sin necesidad de recargar los 100 elementos de la red.
* **Prevención de Errores Accidentales**: La vista incorpora un `AlertDialog` de Material 3 que previene pulsaciones accidentales solicitando confirmación explícita del usuario.

### 3. Búsqueda Reactiva en Tiempo Real (`ViewModel` + `View`)
* **Flujo Unidireccional Declarativo**: El término de búsqueda se almacena en `_busqueda: MutableStateFlow<String>`. El operador `combine` fusiona el estado original de publicaciones y la consulta del usuario.
* **Búsqueda Bidireccional de Campos**: Coincidencia insensible a mayúsculas y minúsculas tanto en el título (`title`) como en el cuerpo (`body`) de las publicaciones.
* **Experiencia de Usuario**: Incorpora icono de lupa, botón de borrado rápido (`Icons.Default.Clear`) y estados vacíos claros que diferencian cuándo la lista no tiene posts del servidor frente a cuándo no hay coincidencias para el término buscado.

---

## ⚖️ Principios Arquitectónicos Intactos en la Fase 2

1. **Cero `Retrofit` o `HttpException` en el ViewModel**: La capa de presentación y de negocio solo conoce interfaces y clases de Kotlin estándar (`Result`, `UiState`, `PostRepository`).
2. **La Pantalla Jamás Llama a la API**: Todos los eventos de UI se notifican al `PostsViewModel` a través de funciones públicas (`cargar()`, `filtrar()`, `abrir()`, `publicar()`, `eliminar()`, `actualizarBusqueda()`).
3. **Cancelación Estructurada Respetada**: Toda rutina que envuelva llamadas asíncronas preserva el relanzamiento obligatorio de `CancellationException`.
4. **Manejo de Errores con `Result`**: Toda operación en `PostRepositoryRemote` atrapa excepciones técnicas y devuelve `Result.success` o `Result.failure`, protegiendo a la app de cierres inesperados (*crashes*).
