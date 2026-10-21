package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.Categoria
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.model.CalculoPromociones
import com.huaman.cafecampus.model.ItemCarrito
import com.huaman.cafecampus.model.ProductoEnOferta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Menu del cliente: productos del Repository + carrito del estudiante.
 *
 * Conoce el Repository, NO los Composables y NO el DAO.
 *
 * Ademas aplica las promociones vigentes: los productos se exponen como
 * [ProductoEnOferta], con el precio de lista y el precio final.
 */
class MenuViewModel(private val repo: CafeRepository) : ViewModel() {

    /**
     * Seccion de datos del proyecto base: la lectura que actualiza la pantalla
     * es un Flow convertido a StateFlow con `stateIn`.
     */
    val productos: StateFlow<List<Producto>> =
        repo.observarProductos()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    /**
     * Promociones vigentes. Es un Flow de Room, asi que si el administrador
     * crea, edita o desactiva una promocion, los precios del menu se
     * actualizan solos.
     */
    val promociones = repo.observarPromociones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /**
     * La carta con el precio final ya calculado (promocion aplicada).
     */
    val ofertas: StateFlow<List<ProductoEnOferta>> =
        combine(productos, promociones) { lista, promos ->
            lista.map { CalculoPromociones.ofertaDe(it, promos) }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Categoria seleccionada en los FilterChip. */
    private val _categoria = MutableStateFlow(Categoria.TODAS)
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    /** Productos ya filtrados por categoria: es lo que dibuja la pantalla. */
    val productosVisibles: StateFlow<List<ProductoEnOferta>> =
        combine(ofertas, _categoria) { lista, cat ->
            if (cat == Categoria.TODAS) lista else lista.filter { it.categoria == cat }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // -----------------------------------------------------------------------
    // Carrito (Paso 7)
    //
    // Vive SOLO en memoria: al confirmar, el Repository lo convierte en
    // filas de `detalle_pedido`. Como es estado del ViewModel, sobrevive a
    // rotar la pantalla.
    // -----------------------------------------------------------------------

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito.asStateFlow()

    /** Cuantas unidades hay en el carrito (badge de la bolsa). */
    val unidadesCarrito: StateFlow<Int> =
        MutableStateFlow(0).also { flow ->
            viewModelScope.launch {
                carrito.collect { flow.value = it.sumOf { i -> i.cantidad } }
            }
        }.asStateFlow()

    /** Total del carrito (con promociones aplicadas), para el boton de confirmar. */
    val totalCarrito: StateFlow<Double> =
        MutableStateFlow(0.0).also { flow ->
            viewModelScope.launch {
                carrito.collect { flow.value = it.sumOf { i -> i.subtotal } }
            }
        }.asStateFlow()

    /** Total del carrito si no hubiera promociones (para tachar). */
    val totalCarritoOriginal: StateFlow<Double> =
        MutableStateFlow(0.0).also { flow ->
            viewModelScope.launch {
                carrito.collect { flow.value = it.sumOf { i -> i.subtotalOriginal } }
            }
        }.asStateFlow()

    /**
     * Agrega un producto al carrito.
     *
     * REGLA: si el producto YA esta en el carrito, se SUMA la cantidad en vez
     * de duplicar la linea. Esto es lo que hace valida la clave compuesta
     * `(pedidoId, productoId)` de `detalle_pedido`.
     *
     * Se guarda el PRECIO FINAL (con la promocion aplicada): si despues el
     * administrador cambia la promocion, este pedido no se altera.
     */
    fun agregar(
        producto: Producto,
        cantidad: Int = 1,
        nota: String = "",
        precioUnitario: Double = producto.precio,
        descuentoPorcentaje: Double = 0.0
    ) {
        if (!producto.disponible || cantidad <= 0) return

        val actual = _carrito.value
        val indice = actual.indexOfFirst { it.producto.id == producto.id }

        _carrito.value = if (indice >= 0) {
            actual.mapIndexed { i, item ->
                if (i == indice) {
                    item.copy(
                        cantidad = item.cantidad + cantidad,
                        nota = if (nota.isNotBlank()) nota else item.nota,
                        precioUnitario = precioUnitario,
                        descuentoPorcentaje = descuentoPorcentaje
                    )
                } else {
                    item
                }
            }
        } else {
            actual + ItemCarrito(
                producto = producto,
                cantidad = cantidad,
                nota = nota,
                precioUnitario = precioUnitario,
                descuentoPorcentaje = descuentoPorcentaje
            )
        }
    }

    /** Cambia la cantidad de una linea ya existente (minimo 1). */
    fun cambiarCantidad(productoId: Int, cantidad: Int) {
        _carrito.value = _carrito.value.mapNotNull { item ->
            if (item.producto.id != productoId) {
                item
            } else if (cantidad < 1) {
                null // cantidad 0 = quitar la linea
            } else {
                item.copy(cantidad = cantidad)
            }
        }
    }

    /** Quita una linea del carrito. */
    fun quitar(productoId: Int) {
        _carrito.value = _carrito.value.filterNot { it.producto.id == productoId }
    }

    /** Vacia el carrito (se llama al confirmar el pedido). */
    fun vaciar() {
        _carrito.value = emptyList()
    }

    /** Total del carrito. */
    fun total(): Double = _carrito.value.sumOf { it.subtotal }

    /** Unidades en el carrito (para el badge de la bolsa). */
    fun unidades(): Int = _carrito.value.sumOf { it.cantidad }

    /** -----------------------------------------------------------------------
     * Categorias para los chips del menu.
     * -------------------------------------------------------------------- */
    val categorias: List<String> = Categoria.paraFiltro

    fun seleccionarCategoria(cat: String) {
        _categoria.value = cat
    }

    /** La oferta de un producto, con la promocion ya aplicada. */
    fun ofertaDe(id: Int): ProductoEnOferta? =
        ofertas.value.firstOrNull { it.id == id }
}