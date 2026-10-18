# PROMPTS.md

Registro de los prompts usados para construir y revisar **CafeCampus**
(Laboratorio MVVM + Room, Semana 08).

La idea es que cualquier pueda reproducir el resultado: qué se le pidió a la
IA y qué decisiones se tomaron como respuesta.

---

## 0. Contexto que se le dio

Se le pasó el enunciado del laboratorio (texto + imágenes de los mockups) y se
le pidió respetar:

- el contrato de cada paso (firmas de la interface, tablas, rutas);
- la capa de cada archivo: la **View** no toca el DAO, el **ViewModel** no
  conoce Composables, el **Repository** no expone entidades de UI;
- un commit por paso con el mensaje exacto de la rúbrica.

### Prompt 1 — Extraer el enunciado

> "Lee el PDF del laboratorio, extrae el texto y las secciones de 'Para pensar',
> y resume: la arquitectura pedida, las 4 tablas con sus claves, los 12 pasos
> con su contrato, y los 13 mensajes de commit exactos."

### Prompt 2 — Proyecto base y decisiones

> "Con el enunciado delante, arma el proyecto de Android con Kotlin, Compose
> Material 3, Room, DataStore y Navigation. Mantén el paquete del proyecto.
> Usa una versión de Room que funcione con el Kotlin y el KSP que ya tiene el
> proyecto. Si algo del enunciado es incompatible con el toolchain actual,
> explícame la diferencia y sigue con la versión que sí compile."

### Prompt 3 — Imágenes de producto

> "En los espacios donde va una imagen, descarga una imagen genérica de
> internet, guárdala en una carpeta dentro del proyecto y conéctala desde ahí.
> Si la imagen no existe, la app debe seguir viéndose bien."

### Prompt 4 — Script SQL

> "Crea un script SQL dentro de la estructura del proyecto con el esquema
> relacional completo: claves primarias, foráneas, compuestas y candidatas, más
> los datos iniciales."

### Prompt 5 — Un commit por paso

> "Haz un commit por cada funcionalidad o parte que vayas avanzando. El mensaje
> debe ser el que pide la rúbrica."

---

## 1. Por capa

### Capa de datos

> "Escribe las entidades `Usuario`, `Pedido` y `DetallePedido` siguiendo el
> modelo de `Producto.kt` y la tabla del enunciado. Declara la clave foránea
> con su `Index` para que Room no avise. Guarda `nombreProducto` y
> `precioUnitario` como una foto del momento."

> "Escribe `UsuarioDao`, `PedidoDao` y la relación `PedidoConDetalle`. Las
> consultas que devuelven `PedidoConDetalle` necesitan `@Transaction`. Para
> sumar ventas sin nulos usa `COALESCE(SUM(total), 0.0)` y no cuentes los
> cancelados."

### Repository

> "Implementa `CafeRepositoryRoom` con TODAS las funciones de la interface. En
> `crearPedido` convierte `List<ItemCarrito>` en `DetallePedido` copiando nombre
> y precio del producto, calcula el total y llama al DAO transaccional. Los
> `observar*` solo delegan en los DAOs y no usan `suspend`."

### ViewModels

> "Cada ViewModel recibe el Repository por constructor y expone `StateFlow` con
> `stateIn`. Lo que cambia datos una sola vez va en `viewModelScope.launch`.
> La regla de avanzar estados vive en `EstadoPedido`, nunca en un Composable."

### Pantallas

> "Arma cada pantalla siguiendo el mockup: tarjetas de 12.dp con borde fino,
> botones de 10.dp, márgenes de 16.dp, `FilterChip` para categorías y estados,
> `LazyVerticalGrid` para la carta, `FloatingActionButton` en la gestión del
> menú. Usa siempre `MaterialTheme.colorScheme.primary`, nunca un color fijo."

> "El panel tiene 3 contadores, una tarjeta de ventas del día, un gráfico de
> barras por hora y la lista de pedidos nuevos con su botón de preparar."

> "El administrador avanza un pedido con `EstadoPedido.siguiente()`; la lógica
> de negocio no va en la pantalla."

### Ajustes

> "En Ajustes: el modo oscuro se guarda en DataStore y cambia el tema de toda la
> app al instante; 'recibir pedidos' apagado hace que el cliente vea
> 'Cafetería cerrada' y no pueda confirmar; 'cambiar de perfil' guarda el otro
> perfil y navega a su grafo limpiando la pila; cerrar sesión borra el perfil."

---

## 2. Revisión y correcciones

Después de escribir el código se le pidió **revisar contra los mockups** y
**probar la app de verdad**. Eso destapó cuatro errores que en el papel no se veían:

### Prompt 6 — Revisar la UI contra los mockups

> "Revisa cada pantalla contra la imagen del mockup: ¿coinciden los colores,
> los textos y la posición de los elementos? Y no dejes colores sueltos: todo
> debe salir del tema."

### Prompt 7 — Probar en el emulador

> "Compila, instala en el emulador y recorre la app: login del estudiante, agregar
> un producto, confirmar el pedido, entrar como administrador y mover el estado.
> Avísame si algo falla."

**Errores encontrados y corregidos:**

| # | Error | Por qué pasó | Corrección |
|---|---|---|---|
| 1 | El login funcionaba pero **no navegaba** | `LoginScreen` recibía `onIngresoExitoso` y nunca lo invocaba | Observar `usuarioActual` con `LaunchedEffect` y navegar con `popUpTo(0)` |
| 2 | El **título de la barra superior no se veía** | La caja de `actions` usaba `fillMaxWidth()` y se comía el ancho del título | Quitar el `fillMaxWidth()` |
| 3 | El panel decía "**1 pedidos**" | String fijo en vez de plural | Usar el helper `plural(n, singular, plural)` |
| 4 | Pestañas raíz con **flecha de volver** | `PedidosAdminScreen` tenía `onAtras` por defecto | Pasar `null` en las raíces de la barra inferior |

> El error 1 es el mejor ejemplo de por qué el enunciado pide *verificar antes
> del commit*: compilaba perfecto y aun así la app no hacía nada.

### Prompt 8 — Imágenes

> "Las imágenes que bajaste: revísalas una por una, porque algunas no tienen
> nada que ver. Chicha morada salió con envoltorios de caramelos y el menú del
> día con una hamburguesa. Búscalas de nuevo y comprueba visualmente."

---

## 3. Decisiones que se tomaron

| Decisión | Motivo |
|---|---|
| Room **2.8.5** en vez de 2.6.1 | La 2.6.1 no compila con KSP2 (Kotlin 2.2.10). Las APIs del enunciado son las mismas. |
| Fotos + icono de respaldo | Se cumplen las dos cosas: imágenes reales y el componente `ProductoIcono` del enunciado. |
| `PedidoCard` recibe datos primitivos, no `PedidoConDetalle` | Los componentes de UI no deberían depender de entidades de base. |
| ViewModels compartidos en la fábrica (`by lazy`) | `PedidosClienteViewModel` necesita el mismo carrito que `MenuViewModel`. |
| `popUpTo(0) { inclusive = true }` al cambiar de perfil | Limpia la pila: el login no debe quedar atrás. |

---

## 4. Cómo se verificó

1. `./gradlew :app:assembleDebug` — compila sin errores ni advertencias.
2. Se instaló en el emulador y se recorrió el flujo completo con capturas.
3. Se comprobó la base con `Database Inspector`: 4 tablas, datos sembrados y el
   pedido creado por el estudiante visible desde el panel del administrador.
4. Se revisó cada captura contra su mockup (de ahí salieron las correcciones).