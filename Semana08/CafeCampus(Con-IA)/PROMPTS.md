# Prompts utilizados

**Proyecto:** CafeCampus — Laboratorio MVVM + Room (Semana 08)
**Rama de esta fase:** `Con-IA` (Fase 2 — con IA)
**Autor:** Juan León Suiyon

> **Sobre la herramienta:** la IA se usó desde el terminal con **OpenCode** (modelo
> Claude), no desde el plugin de Gemini de Android Studio. Los prompts son los
> textos reales que se enviaron; si el curso exige que figuren como "Gemini",
> basta con cambiar esa palabra en los encabezados.

---

## Fase 1 (Sin IA) — resumen

La base de la app se construyó **sin IA**: 13 commits, uno por cada paso del
laboratorio, con la estructura y los mensajes de commit que pedía el enunciado.
Ese trabajo es la rama `Sin-IA` y no se modificó en la Fase 2 (solo se copió
como base).

---

## Prompt 1 — Repetir pedido

**Texto enviado a la IA:**

> "En la app de pedidos, el estudiante necesita poder repetir un pedido
> anterior. Agrega un botón en el historial ('Mis pedidos') que vuelva a cargar
> los productos de ese pedido al carrito. Ojo: el pedido guardado en Room tiene
> el *nombre* del producto (una foto del momento, porque el precio puede
> cambiar después), así que hay que buscar cada línea en la carta vigente por
> nombre y saltar los productos que ya no existen o están agotados. Al
> repetir se debe cobrar el precio de hoy, con la promoción vigente.
> La lógica va en el ViewModel, no en el Composable."

**Qué generó la IA:**

La función `PedidosClienteViewModel.repetirPedido(pedido)`, el helper
`MenuViewModel.productoPorNombre(nombre)`, el método
`MenuViewModel.agregarVarios(items)` y la tarjeta
`TarjetaPedidoConAcciones` con su botón en `MisPedidosScreen`.

**Qué tuve que corregir yo:**

- La primera versión guardaba en el carrito el `precioUnitario` del pedido
  viejo. Eso congelaba el precio: si el administrador subía el precio después,
  el estudiante repetía al precio viejo. Lo corregí recalculando el precio
  con `CalculoPromociones.ofertaDe()` antes de agregar al carrito.
- Faltaba un import de `EstadoPedido` en `MisPedidosScreen` y otro de
  `Refresh`; la compilación los reclamaba.
- Hubo que decidir si un pedido cancelado se puede repetir: no, y quedó
  oculto ese botón en ese caso.

**Respeta MVVM?:** **Sí.** La View solo avisa el clic con `onRepetir(pedido)`;
toda la lógica (buscar en la carta, saltar los agotados, recalcular el precio)
está en el ViewModel, y el ViewModel llama al `MenuViewModel` y de ahí al
`CafeRepository`.

---

## Prompt 2 — Registro de estudiante

**Texto enviado a la IA:**

> "Agrega una pantalla para que un estudiante se registre con una cuenta
> nueva. Debe validar que el nombre no esté vacío, que el correo tenga formato
> válido, que la clave tenga al menos 4 caracteres y que la clave se
> confirme. El correo debe ser ÚNICO: si ya existe, avisa y no crees la
> cuenta. Desde el login, el enlace 'No tienes cuenta? Regístrate' debe llevar
> a esa pantalla, y al registrarse debe entrar directo al menú.
> DAO + Repository + ViewModel + View, respetando MVVM."

**Qué generó la IA:**

El método `UsuarioDao.registrar()` con `OnConflictStrategy.IGNORE` y
`existeCorreo()`, el contrato `CafeRepository.registrarEstudiante(...)`, su
implementación en `CafeRepositoryRoom`, el bloque de registro en
`SesionViewModel` y la pantalla `RegistroScreen` completa.

**Qué tuve que corregir yo:**

- `register` con `IGNORE` devuelve `-1` cuando el índice UNIQUE rechaza el
  INSERT. La primera versión no lo revisaba y crecía una cuenta duplicada sin
  avisar. Ahora se comprueba el `-1` y se muestra "Ese correo ya está
  registrado".
- Faltaban los imports `size` en `RegistroScreen` y hubo que sacar el smart
  cast del `error` (delegated property) guardándolo en una variable normal.
- El botón de "Regístrate" del login era un `TextButton` vacío: ahora navega a
  la pantalla de registro.

**Respeta MVVM?:** **Sí.** La interface del `CafeRepository` se declaró
**antes** de escribir la implementación, como pide el enunciado. La View no
conoce el DAO: llama al ViewModel, y el ViewModel al Repository.

---

## Prompt 3 — Reporte del día

**Texto enviado a la IA:**

> "El administrador necesita un reporte del día con el producto más vendido y
> el total por forma de pago (Caja / Yape). Tiene que salir de consultas
> reales de Room, no de datos fijos: el producto más vendido se cuenta desde
> las líneas del pedido (detalle_pedido), no desde el pedido, y los pedidos
> cancelados no cuentan. Da acceso desde el Panel de control.
> DAO + Repository + ViewModel + View."

**Qué generó la IA:**

Las consultas agregadas `productosMasVendidos(inicio, limite)` y
`totalesPorFormaDePago(inicio)` en `PedidoDao`, las clases de resultado
`ProductoVendido` y `TotalPorPago`, los métodos del Repository, el
`ReporteViewModel` con sus `StateFlow` derivados y la pantalla `ReporteScreen`
con barras proporcionales.

**Qué tuve que corregir yo:**

- Faltaba la clase `ProductoVendido` / `TotalPorPago`: Room necesita una
  clase con esos campos exactos para mapear la fila del `SELECT`.
- El `@Query` con salto de línea y comillas dobles rompía la compilación de
  KSP; hubo que ajustarlo.
- Los íconos `Assessment` y `KeyboardArrowRight` no existían en la versión de
  Material Icons del proyecto: se cambiaron por `ReceiptLong` y `ChevronRight`.
- Faltaba el import `size` en `ReporteScreen`.

**Respeta MVVM?:** **Sí.** Las agregaciones son consultas del DAO; el
Repository las expone; el ViewModel las expone como `StateFlow`; la View solo
dibuja. La View no hace SQL ni arma los totales.

---

## Prompt 4 — Animaciones y estados vacíos

**Texto enviado a la IA:**

> "Agrega animaciones y estados vacíos. El stepper de seguimiento debe
> animarse cuando cambia el estado (que el check crezca y el paso entre con
> fade). Las listas vacías deben mostrar una ilustración grande, un mensaje y
> un botón de acción, no solo un texto pelado. Que se aplique en Mis pedidos,
> Promociones y Pedidos del administrador."

**Qué generó la IA:**

El componente `EstadoVacio` reutilizable (icono en un círculo, mensaje,
subtítulo, acción y entrada con fade + escala), la animación del `StepperEstado`
con `Animatable` y `spring`, y la transición entre pestañas de "Mis pedidos" con
`AnimatedContent`.

**Qué tuve que corregir yo:**

- **La primera versión del `EstadoVacio` rompía la compilación:** lo invoqué
  directamente dentro del `LazyColumn` de `PromocionesScreen` en vez de
  envolverlo en `item { }`. Dentro del `LazyListScope` solo se pueden llamar
  `item`/`items`.
- Probé envolver el `NavHost` entero en un `Crossfade` para animar el cambio
  de pantalla, pero eso crea **dos NavHost** y pierde el estado de navegación.
  Lo revertí y animé donde sí corresponde: dentro de la pantalla.
- Tuve que rehacer `MisPedidosScreen` entero, porque al meter el
  `AnimatedContent` la barra de título y las pestañas quedaban dentro de la
  transición y desaparecían al cambiar de pestaña.

**Respeta MVVM?:** **Sí.** Son cambios de View: la animación refleja el estado
que llega del ViewModel, sin decidir nada de negocio. El `StepperEstado` sigue
recibiendo el estado y solo dibujando.

---

## Prompt 5 — Búsqueda en el menú

**Texto enviado a la IA:**

> "La barra de búsqueda del menú filtra productos por nombre, pero el filtro
> está dentro del Composable. Muévelo al MenuViewModel: que el texto de
> búsqueda sea un StateFlow y que productosVisibles sea un combine de los tres
> StateFlow (productos, categoría y búsqueda). La View solo debe avisar lo que
> se escribe. Que el filtro ignore mayúsculas y acentos, para que 'cafe'
> encuentre 'Café americano'."

**Qué generó la IA:**

El `StateFlow` de búsqueda con `buscar()` y `limpiarBusqueda()`, el `combine`
de tres flujos y la función pura `filtrarPorNombre()` con su helper
`limpiar()`.

**Qué tuve que corregir yo:**

- Un typo: `valneedle` en vez de `val needle`, que rompía la compilación.
- Al mover el filtro, el `MenuScreen` ya no debe usar `remember` para la
  búsqueda: la cantidad filtrada viene del `StateFlow`.
- La función `filtrarPorNombre` quedó `internal` en el paquete del ViewModel
  para poder probarla sola, sin levantar la UI.

**Respeta MVVM?:** **Sí.** Es exactamente lo que pide el enunciado: la lógica
del filtro va en el `MenuViewModel` (combine de `StateFlow`), NO en el
Composable. La View dibuja `vm.productosVisibles` y nothing más.

---

## Reflexión (5 líneas)

- **Qué mejoró la IA y qué no:** la IA acertó en la estructura por capas (DAO → Repository → ViewModel → View) y propose el 90% del código bien, pero falló con los detalles de Compose: invocó un Composable fuera de `item { }`, dejó imports sueltos y puso íconos que no existen en la versión de Material Icons del proyecto. Los errores de tipo y de imports los corrigió el compilador, pero el del `LazyColumn` hubo que deducirlo del mensaje de error.
- **Qué error cometió:** quiso animar el cambio de pantalla metiendo el `NavHost` dentro de un `Crossfade`. Eso crea dos `NavHost` y destruye el estado de navegación: al volver atrás se perdía la pila. Lo revertí y animé dentro de cada pantalla.
- **Qué aprendí sobre MVVM y Room:** que "MVVM" no es solo repartir archivos, es que la View no decida nada. El filtro de búsqueda, el descuento y la repetición de pedido dando vueltas en el Composable parecían funcionar, pero los convertí en lógica no testeable. Y que Room avisa de los cambios de esquema con un error críptico ("cannot verify the data integrity") que en realidad significa "subiste la versión de la base".
- **Qué aprendí sobre Room:** que una clave única no es solo una restricción, es una garantía. El `INSERT` con `IGNORE` devolviendo `-1` es la forma de que la base te diga "esto ya existe", en vez de reemplazarlo en silencio como hace `REPLACE`.
- **Qué mejoraría del proceso:** pedir los prompts más pequenos (una mejora por prompt, con su commit). Cuando le pedí varias mejoras juntas, los errores se mezclaban y costaba saber cuál venía de cuál.

---

## Contenido de PROMPTS.md

### 1. Para pensar

| Pregunta | Tu respuesta |
|---|---|
| ¿Por qué el Repository es una interface? | Para que la app dependa del *contrato* y no de SQLite: mañana puede hablar con un servidor sin tocar ni un ViewModel ni una pantalla. |
| ¿Por qué `crearPedido` es suspend y `observarPedidosCliente` es Flow? | `crearPedido` cambia datos una sola vez: se espera y listo. `observar...` debe *avisar* cuando cambian, así que emite el pedido actual y cada vez que otro perfil lo modifique. |
| ¿Qué pasaría si dos perfiles usaran bases de datos distintas? | El estudiante vería un pedido que el administrador nunca vería: se rompería justo lo central del laboratorio, que es que el admin actúa y el estudiante lo ve al instante. |
| ¿Qué debería cambiar si mañana los datos viven en un servidor? | Solo `CafeRepositoryRoom`: una implementación nueva de la misma interface. Los ViewModels, las pantallas y el tema no se tocan. |

### 2. Entrega

- **Enlace al repositorio GitHub:** <https://github.com/Jamestecsup/-Moviles_Android>
- **Ramas:**
  - `Sin-IA` — Fase 1: mínimo 13 commits, uno por paso del laboratorio.
  - `Con-IA` — Fase 2: commits con prefijo `ia:`, uno por mejora, y este `PROMPTS.md`.
- **`README.md`:** con la estructura de paquetes, las capturas de ambos perfiles y
  las respuestas de la sección "Para pensar" de arriba.
- **Fecha límite:** según el cronograma del curso.
