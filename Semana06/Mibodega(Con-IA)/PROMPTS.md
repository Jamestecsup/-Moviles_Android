# Historial de Prompts - Fase 2 MiBodega (Mejora con IA)

> Nota: la base ya traía badge en el carrito y buscador en tiempo real
> combinado con categorías; eso no se tocó.

## Prompt 1: Login con usuario y clave fijos
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Agrega campo de contraseña a `LoginScreen.kt`, valida contra un usuario y clave fijos en el código (`987654321` / `tecsup123`) y muestra el error en rojo si no coinciden. Agrega `esContrasena` a `CampoTexto.kt` con `PasswordVisualTransformation`."

## Prompt 2: Campos en rojo si están vacíos
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Agrega `esError` a `CampoTexto.kt` con `isError` y mensaje 'Completa este campo'. En Registro, Entrega y Login marca en rojo los vacíos al intentar enviar (`intentoEnviar`) y no avances hasta que estén llenos."

## Prompt 3: Carrito vacío y confirmar antes de eliminar
- **Fecha**: 2026-10-03
- **Prompt**:
  > "En `CarritoScreen.kt` muestra 'Tu carrito está vacío' cuando no hay productos (ocultando el resumen) y pide confirmación con un `AlertDialog` antes de eliminar un producto."

## Prompt 4: Mis pedidos con historial
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Crea `MisPedidosScreen.kt` con un `LazyColumn` del historial (`numero`, total, método y dirección) y mensaje si está vacío. Guarda cada confirmado en un `mutableStateListOf` en `ClienteApp.kt`, agrega la ruta `pedidos` y lleva ahí la pestaña Pedidos del `NavigationBar` (el carrito queda en el icono de la topBar)."

## Prompt 6: Orden por precio y recojo o delivery
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Ordena el grid de Inicio por precio con dos chips (`Menor precio` / `Mayor precio` usando `sortedBy` y `sortedByDescending`, se apagan al repetir). En `DatosEntregaScreen.kt` agrega `RadioButton` de Recojo en tienda (gratis) o Delivery (S/ 4.00) que cambie el total, pasando el `subtotal` y devolviendo el total final en `onConfirmarPedido`."

## Prompt 5: Favoritos con corazón y su pantalla
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Marca favoritos con un corazón (`Favorite` / `FavoriteBorder`) en `ProductoCard.kt` y en el detalle, guarda los ids en un `mutableStateSetOf` en `ClienteApp.kt`, crea `FavoritosScreen.kt` con grid y ruta `favoritos`, y abre esa ruta desde un icono de corazón en la topBar de Inicio."
