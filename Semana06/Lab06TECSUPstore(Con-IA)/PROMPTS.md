# Historial de Prompts - Fase 2 (Mejora con IA)

## Prompt 1: Opción Favoritos en el DropdownMenu de la tarjeta
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Agrega la opción 'Favoritos' con su `leadingIcon` al `DropdownMenu` de `TarjetaProducto.kt`, con un parámetro `onFavoritoClick` que se pase desde `PantallaCarrito.kt`, siguiendo el mismo patrón de las opciones que ya existen."

## Prompt 2: Lista de favoritos con su pantalla y destino
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Guarda los favoritos en una lista con `mutableStateListOf` en `PantallaPrincipal.kt`, agrega el destino 'Favoritos' al drawer con sus iconos filled y outlined, crea la pantalla `PantallaFavoritos` con un `LazyColumn` y un mensaje cuando está vacía, y conecta el `onFavoritoClick` desde el carrito sin que se repitan."

## Prompt 3: Badge con contador en Favoritos del drawer
- **Fecha**: 2026-10-03
- **Prompt**:
  > "Conecta las dos piezas: agrega un `Badge` con contador en el ítem 'Favoritos' del drawer que muestre `productosFavoritos.size`, igual al que ya tiene el carrito, y que solo aparezca cuando hay al menos un favorito."
