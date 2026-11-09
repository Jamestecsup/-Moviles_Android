package com.huaman.cafecampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaman.cafecampus.data.local.Categoria
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.data.repository.CafeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Gestion del menu del ADMINISTRADOR (mockups 5 y 6).
 *
 * Guardar funciona para crear y para editar: si el producto trae `id == 0` se
 * inserta, si trae id se actualiza.
 */
class MenuAdminViewModel(
    private val repo: CafeRepository
) : ViewModel() {

    /** Carta completa (el admin ve tambien los agotados). */
    val productos: StateFlow<List<Producto>> =
        repo.observarProductos()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _categoria = MutableStateFlow(Categoria.TODAS)
    val categoria: StateFlow<String> = _categoria.asStateFlow()

    val productosVisibles: StateFlow<List<Producto>> =
        combine(productos, _categoria) { lista, cat ->
            if (cat == Categoria.TODAS) lista else lista.filter { it.categoria == cat }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Error de validacion del formulario, o null. */
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val categorias: List<String> = Categoria.paraFiltro
    val categoriasFormulario: List<String> = Categoria.paraFormulario

    /** Iconos que el admin puede asignar (columna `icono` del producto). */
    val iconos: List<String> = listOf("cafe", "plato", "jugo", "pan", "botella", "dulce")

    fun seleccionarCategoria(cat: String) {
        _categoria.value = cat
    }

    /**
     * Crea o actualiza un producto.
     *
     * Validaciones del laboratorio:
     *  - el NOMBRE es obligatorio,
     *  - el PRECIO debe ser mayor que 0.
     *
     * Ademas se avisa si el nombre ya existe en otro producto, porque
     * `productos.nombre` es clave candidata (UNIQUE).
     *
     * [imagenRuta] es la ruta de una foto de galeria, o null para usar el
     * icono vectorial.
     */
    fun guardar(
        id: Int,
        nombre: String,
        descripcion: String,
        categoria: String,
        precioTexto: String,
        icono: String,
        disponible: Boolean,
        imagenRuta: String?,
        alGuardar: () -> Unit
    ) {
        viewModelScope.launch {
            val nombreLimpio = nombre.trim()

            if (nombreLimpio.isEmpty()) {
                _error.value = "El nombre es obligatorio"
                return@launch
            }

            val precio = precioTexto.trim().replace(",", ".").toDoubleOrNull()
            if (precio == null || precio <= 0.0) {
                _error.value = "El precio debe ser mayor que 0"
                return@launch
            }

            // clave candidata: no puede haber dos productos con el mismo nombre
            val duplicado = productos.value.any {
                it.id != id && it.nombre.equals(nombreLimpio, ignoreCase = true)
            }
            if (duplicado) {
                _error.value = "Ya existe un producto con ese nombre"
                return@launch
            }

            repo.guardarProducto(
                Producto(
                    id = id,
                    nombre = nombreLimpio,
                    descripcion = descripcion.trim(),
                    categoria = categoria,
                    precio = precio,
                    disponible = disponible,
                    icono = icono,
                    imagenRuta = imagenRuta
                )
            )

            _error.value = null
            alGuardar()
        }
    }

    fun eliminar(producto: Producto) {
        viewModelScope.launch { repo.eliminarProducto(producto) }
    }

    /** Marca el producto como agotado o disponible. */
    fun alternarDisponibilidad(producto: Producto) {
        viewModelScope.launch {
            repo.cambiarDisponibilidad(producto.id, !producto.disponible)
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}