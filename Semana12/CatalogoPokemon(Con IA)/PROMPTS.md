# Consultas a la IA — Fase 2

**Proyecto:** Catálogo Pokémon — Laboratorio 11-12 (Semana 12)
**Rama de esta fase:** `Con-IA` (equivale a la rama `mejora-ia` que pide el enunciado)
**Herramienta:** OpenCode (agente de terminal con modelo de lenguaje), ejecutado sobre
el repositorio local. No se usó el plugin de Gemini de Android Studio.

La Fase 1 (sin IA) está en la rama `Sin-IA` y no se modificó: el proyecto se copió a
`Semana12/CatalogoPokemon(Con IA)` como punto de partida. Todo lo que sigue son las
consultas reales de la Fase 2, con lo que se aceptó, lo que se cambió y lo aprendido.

---

## Consulta 1 — Copiar el proyecto y planificar la fase

**Fecha:** 2026-11-08
**Herramienta:** OpenCode

**Prompt:**

> "En la rama Con-IA copia el proyecto actual y realiza las cosas que pide el docx
> con-IA. El proyecto está en `Semana12/CatalogoPokemon(Sin IA)` y la Fase 1 ya
> está cerrada en la rama `Sin-IA`."

**Qué respondió (resumen):**
Propuso copiar el proyecto con `git checkout Sin-IA -- <ruta>` y renombrar la carpeta
a `CatalogoPokemon(Con IA)`, y luego dividir la Fase 2 en cinco commits: copia base,
buscador, pruebas unitarias, `PROMPTS.md` y README. También recordó que la rúbrica pide
mínimo 3 pruebas de `ListaViewModel` y mínimo 3 commits en la rama.

**Qué usamos / qué cambiamos y por qué:**
Se aceptó la copia y el orden de commits. Se descartó crear la rama `mejora-ia` porque
el repositorio ya usa la convención `Con-IA` para todas las fases con IA (Semana 06 y
08), y el docente revisa la rama por la entrega, no por el nombre. La copia se hizo
sin arrastrar `build/`, `.idea/` ni `google-services.json` (este último está ignorado).

**Qué aprendimos:**
Que una rama de trabajo debe seguir la convención del repositorio aunque el enunciado
use otro nombre, y que copiar un proyecto con Git conserva exactamente el contenido
sin arrastrar archivos ignorados.

---

## Consulta 2 — Pruebas unitarias del ViewModel (R12)

**Fecha:** 2026-11-08
**Herramienta:** OpenCode

**Prompt:**

> "Escribe como mínimo tres pruebas unitarias de `ListaViewModel` usando un
> `ItemRepository` falso y un `Notificador` falso, sin Android ni internet. Deben
> cubrir: la lista guardada se publica como éxito, la sincronización con éxito
> notifica el total, el error sin caché muestra `UiState.Error`, el error con caché
> conserva la lista y muestra el aviso. Agrega `kotlinx-coroutines-test` y un
> `MainDispatcherRule` para reemplazar `Dispatchers.Main`."

**Qué respondió (resumen):**
Creó un `RepoFalso` con un `MutableStateFlow` para `items`, un resultado de
`refrescar()` configurable y un contador de elementos guardados; un `NotificadorFalso`
que guarda el último mensaje; y una regla JUnit que cambia `Dispatchers.Main` por un
`StandardTestDispatcher`. Las pruebas usan `runTest(scheduler)` y `advanceUntilIdle()`.

**Qué usamos / qué cambiamos y por qué:**
Se aceptó la estructura. Se corrigió un detalle importante: al principio la regla
creaba su propio `StandardTestDispatcher` y la prueba usaba `runTest` con otro
planificador, así que `advanceUntilIdle()` no avanzaba las corrutinas del ViewModel.
Se solucionó pasando `mainDispatcherRule.dispatcher.scheduler` a `runTest`. También se
agregaron dos pruebas del buscador y una de búsqueda sin coincidencias (6 en total).

**Qué aprendimos:**
Que `viewModelScope` usa `Dispatchers.Main` y hay que inyectarlo en las pruebas, y que
el planificador del `runTest` debe ser el mismo que el del dispatcher de prueba; si no,
las corrutinas quedan pendientes y la prueba falla de forma confusa.

---

## Consulta 3 — Buscador por nombre o tipo

**Fecha:** 2026-11-08
**Herramienta:** OpenCode

**Prompt:**

> "Agrega un buscador en la pantalla de lista para filtrar por nombre o por tipo
> (`fire`, `water`, `grass`) sin volver a llamar a la API. El estado del texto debe
> vivir en `ListaViewModel` para poder probarlo, y al limpiar la búsqueda debe
> reaparecer el catálogo completo. Si no hay coincidencias, muestra un estado vacío
> con un botón para limpiar el filtro."

**Qué respondió (resumen):**
Propuso guardar el texto en un `MutableStateFlow` y filtrar en el ViewModel con
`contains` sobre el título, el subtítulo y la lista de tipos. Para la interfaz propuso
un `OutlinedTextField` con icono de búsqueda y una `X` para limpiar.

**Qué usamos / qué cambiamos y por qué:**
Se aceptó el filtrado en el ViewModel porque así se puede probar (R12) y la pantalla
queda solo como vista. Se mantuvo la lista completa en `listaCompleta` para no perderla
al filtrar. En la interfaz se usó `OutlinedTextField` en lugar de `SearchBar` de
Material 3: es más simple, funciona en API 25 y no necesita estado ni animaciones
extra. Se añadió el mensaje "No hay Pokémon que coincidan con «texto»" y el botón
"Limpiar búsqueda".

**Qué aprendimos:**
Que filtrar sobre la fuente de verdad local (Room) es más rápido y barato que pedir de
nuevo a la API, y que el estado del filtro debe estar en el ViewModel para que la
prueba no dependa de la interfaz.

---

## Consulta 4 — Mensajes de error y pantalla de error

**Fecha:** 2026-11-08
**Herramienta:** OpenCode

**Prompt:**

> "Mejora los mensajes de error: distingue sin conexión, error del servidor y error
> inesperado, y que la pantalla de error explique qué pasó y qué hacer. Si hay datos
> guardados, no muestres la pantalla de error: conserva la lista y avisa que se están
> mostrando datos del teléfono."

**Qué respondió (resumen):**
Propuso textos más claros en `mensajeDe()`, un título y una ayuda en `ErrorView`, y
mantener el aviso por `Snackbar` cuando ya hay caché. También sugirió agregar un
`Throwable` al estado de error para mostrar el detalle técnico.

**Qué usamos / qué cambiamos y por qué:**
Se aceptaron los textos y la estructura de `ErrorView` (título, mensaje, ayuda y botón
"Reintentar" con icono). Se rechazó guardar el `Throwable` en `UiState.Error`: el
estado debe ser datos simples y el mensaje ya está traducido a lenguaje de usuario.
El aviso sin conexión se amplió a "se muestran los datos guardados en el teléfono" para
que quede claro de dónde salen los datos.

**Qué aprendimos:**
Que el usuario necesita saber qué hacer, no el código de error; y que la caché no debe
desaparecer de la vista solo porque falló la red.

---

## Consulta 5 — Revisión de la entrega y documentación

**Fecha:** 2026-11-08
**Herramienta:** OpenCode

**Prompt:**

> "Revisa la rúbrica de la Fase 2: necesito mínimo 3 commits en la rama, `PROMPTS.md`
> con una entrada por consulta, 3 pruebas unitarias, README con capturas, tag v1.0 y
> Pull Request hacia `main`. Dime qué falta y redacta el README y este archivo."

**Qué respondió (resumen):**
Confirmó que con los commits de copia, buscador y pruebas ya se supera el mínimo, y que
faltaban `PROMPTS.md`, actualizar el README (la ruta del proyecto seguía diciendo
"Sin IA") y crear la versión y el Pull Request. Redactó ambos documentos con enlaces a
las capturas existentes.

**Qué usamos / qué cambiamos y por qué:**
Se usó la estructura de `PROMPTS.md` del enunciado. En el README se cambió la ruta a
`Semana12/CatalogoPokemon(Con IA)`, se agregó la sección de la Fase 2 con las pruebas y
las mejoras, y se actualizó el reparto de tareas. El tag y el Pull Request se hicieron
al final, cuando el build y las pruebas ya estaban verificados.

**Qué aprendimos:**
Que la documentación también es parte de la entrega y que conviene generarla al mismo
tiempo que el código para no olvidar ningún requisito de la rúbrica.

---

## Consulta 6 — Colores por tipo de Pokémon

**Fecha:** 2026-11-08
**Herramienta:** OpenCode

**Prompt:**

> "Haz que las tarjetas y las etiquetas de tipo usen un color según el tipo de
> Pokémon: fuego rojo, agua azul, planta verde, tierra verde, eléctrico amarillo,
> veneno morado, etc. Que se note en la lista y en el detalle."

**Qué respondió (resumen):**
Propuso una función `coloresDeTipo()` que traduce el nombre del tipo (en inglés o en
español) a un par de colores de fondo y texto, un composable `TipoChip` reutilizable y
usar el color del primer tipo para teñir suavemente la tarjeta y la imagen del detalle.

**Qué usamos / qué cambiamos y por qué:**
Se aceptó la idea. Se corrigió el color de `tierra`: primero se puso marrón y luego se
cambió a verde por pedido del usuario, para que coincida con los tipos del grupo
(fuego, agua y planta). La tarjeta usa el color con 12 % de transparencia para no
perder legibilidad, y la etiqueta del tipo va con el color sólido y texto blanco.

**Qué aprendimos:**
Que centralizar la paleta en una sola función evita repetir `when` en cada pantalla y
que usar el color con transparencia mantiene el contraste del texto.
