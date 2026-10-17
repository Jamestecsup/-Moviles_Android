package com.huaman.cafecampus.data.local

import com.huaman.cafecampus.data.local.entity.Producto

/**
 * Datos de prueba que se siembran SOLO la primera vez que se abre la app.
 *
 * Si el menu ya tiene productos, no vuelve a insertar nada.
 *
 * El equivalente en SQL esta en
 * `app/src/main/assets/db/cafecampus.sql`, que es la fuente de verdad del
 * esquema. Si cambias algo aqui, cambialo tambien alla (y al reves).
 *
 * Todos los datos son ficticios (Ley 29733).
 */
object DatosIniciales {

    suspend fun sembrar(db: CafeDatabase) {
        val dao = db.productoDao()
        if (dao.contar() > 0) return // ya sembrado

        PRODUCTOS.forEach { dao.insertar(it) }

        // TODO (Paso 3): sembrar tambien los 2 usuarios
        //   juan@campus.test / 1234  (CLIENTE)
        //   admin@cafe.test  / admin  (ADMIN)
    }

    /** Carta inicial de la cafeteria. */
    private val PRODUCTOS = listOf(
        Producto(
            nombre = "Cafe americano",
            descripcion = "Cafe de grano percolado, sin azucar.",
            categoria = Categoria.BEBIDAS,
            precio = 4.00,
            disponible = true,
            icono = "cafe"
        ),
        Producto(
            nombre = "Sandwich de pollo",
            descripcion = "Pan de molde, pollo a la plancha y lechuga.",
            categoria = Categoria.ALMUERZOS,
            precio = 9.00,
            disponible = true,
            icono = "pan"
        ),
        Producto(
            nombre = "Menu del dia",
            descripcion = "Entrada + segundo + refresco. Cambia cada dia segun el menu de cocina.",
            categoria = Categoria.ALMUERZOS,
            precio = 12.00,
            disponible = true,
            icono = "plato"
        ),
        Producto(
            nombre = "Jugo de naranja",
            descripcion = "Exprimido natural, 400 ml.",
            categoria = Categoria.BEBIDAS,
            precio = 5.00,
            disponible = true,
            icono = "jugo"
        ),
        Producto(
            nombre = "Empanada",
            descripcion = "Masa horneada rellena de carne.",
            categoria = Categoria.DESAYUNOS,
            precio = 4.50,
            disponible = true,
            icono = "pan"
        ),
        Producto(
            nombre = "Chicha morada",
            descripcion = "Maiz morado con canela y pina.",
            categoria = Categoria.BEBIDAS,
            precio = 3.50,
            disponible = true,
            icono = "botella"
        )
    )
}

/**
 * Categorias de la carta. Se guardan como String, por eso se centralizan
 * aqui para que el filtro del menu y el formulario del admin usen las mismas.
 */
object Categoria {
    const val TODAS = "Todo"
    const val DESAYUNOS = "Desayunos"
    const val ALMUERZOS = "Almuerzos"
    const val BEBIDAS = "Bebidas"
    const val POSTRES = "Postres"

    /** Filtros del menu del cliente y del menu del administrador. */
    val paraFiltro = listOf(TODAS, DESAYUNOS, ALMUERZOS, BEBIDAS, POSTRES)

    /** Opciones del desplegable del formulario de producto. */
    val paraFormulario = listOf(DESAYUNOS, ALMUERZOS, BEBIDAS, POSTRES)
}