package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.Categoria
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.data.repository.CafeRepository
import com.huaman.cafecampus.model.ItemCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Menu del cliente: productos del Repository + carrito del estudiante.
 *
 * Conoce el Repository, NO los Composables y NO el DAO.
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

    /** Categoria seleccionada en los FilterChip. */
    private val _categoria = MutableStateFlow(Categoria.TODAS)
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    /** Productos ya filtrados por categoria: es lo que dibuja la pantalla. */
    val productosVisibles: StateFlow<List<Producto>> =
        combine(productos, _categoria) { lista, cat ->
            if (cat == Categoria.TODAS) lista else lista.filter { it.categoria == cat }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // -----------------------------------------------------------------------
    // Carrito  (se completa en el Paso 7)
    // -----------------------------------------------------------------------
    // private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    // val carrito: StateFlow<List<ItemCarrito>> = _carrito.asStateFlow()
    //
    // fun agregar(producto: Producto, cantidad: Int = 1, nota: String = "") { ... }
    // fun quitar(productoId: Int) { ... }
    // fun vaciar() { _carrito.value = emptyList() }
    // fun total(): Double = _carrito.value.sumOf { it.subtotal }
    //
    // REGLA: si el producto ya esta en el carrito, se SUMA la cantidad,
    // nunca se duplica la linea.

    /** Categories para los chips del menu. */
    val categorias: List<String> = Categoria.paraFiltro

    /** Codigo de producto abierto en la pantalla de detalle, o null. */
    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()

    fun seleccionarCategoria(cat: String) {
        _categoria.value = cat
    }

    fun abrirProducto(id: Int) {
        _productoSeleccionado.value = productos.value.firstOrNull { it.id == id }
    }
}