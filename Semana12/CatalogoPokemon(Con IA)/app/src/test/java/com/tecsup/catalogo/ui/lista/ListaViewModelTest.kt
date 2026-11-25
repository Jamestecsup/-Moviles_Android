package com.tecsup.catalogo.ui.lista

import com.tecsup.catalogo.MainDispatcherRule
import com.tecsup.catalogo.data.repository.ItemRepository
import com.tecsup.catalogo.domain.model.Item
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.ui.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.io.IOException

/**
 * Pruebas del ViewModel de la lista con un repositorio y un notificador falsos
 * (R12). No usan Android ni internet: solo verifican los estados y avisos.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ListaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val charmander = Item("charmander", "Charmander", "Fire")
    private val squirtle = Item("squirtle", "Squirtle", "Water")
    private val bulbasaur = Item("bulbasaur", "Bulbasaur", "Grass")

    @Test
    fun `la lista guardada se publica como exito`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val repo = RepoFalso(itemsIniciales = listOf(charmander, squirtle))
            val vm = ListaViewModel(repo, NotificadorFalso())

            advanceUntilIdle()

            val estado = vm.estado.value
            assertTrue(estado is UiState.Exito)
            assertEquals(
                listOf(charmander, squirtle),
                (estado as UiState.Exito).datos
            )
        }

    @Test
    fun `al sincronizar con exito se notifica el total guardado`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val repo = RepoFalso(resultadoRefresco = Result.success(3))
            val notificador = NotificadorFalso()
            val vm = ListaViewModel(repo, notificador)

            advanceUntilIdle()

            assertEquals("Datos actualizados", notificador.ultimoTitulo)
            assertEquals("Se guardaron 3 elementos", notificador.ultimoTexto)
            assertNull(vm.aviso.value)
        }

    @Test
    fun `sin conexion y sin cache se muestra el error con reintentar`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val repo = RepoFalso(
                resultadoRefresco = Result.failure(IOException("sin red")),
                cantidadGuardadaValor = 0
            )
            val vm = ListaViewModel(repo, NotificadorFalso())

            advanceUntilIdle()

            val estado = vm.estado.value
            assertTrue(estado is UiState.Error)
            assertTrue(
                (estado as UiState.Error).mensaje.contains("No hay conexión")
            )
        }

    @Test
    fun `sin conexion con datos guardados se avisa y se conserva la lista`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val repo = RepoFalso(
                itemsIniciales = listOf(charmander),
                resultadoRefresco = Result.failure(IOException("sin red")),
                cantidadGuardadaValor = 1
            )
            val vm = ListaViewModel(repo, NotificadorFalso())

            advanceUntilIdle()

            assertTrue(vm.estado.value is UiState.Exito)
            assertEquals(
                listOf(charmander),
                (vm.estado.value as UiState.Exito).datos
            )
            assertTrue(vm.aviso.value.orEmpty().contains("Sin conexión"))
        }

    @Test
    fun `el buscador filtra por nombre y por tipo`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val repo = RepoFalso(
                itemsIniciales = listOf(charmander, squirtle, bulbasaur)
            )
            val vm = ListaViewModel(repo, NotificadorFalso())
            advanceUntilIdle()

            vm.buscar("char")
            assertEquals(
                listOf(charmander),
                (vm.estado.value as UiState.Exito).datos
            )

            vm.buscar("water")
            assertEquals(
                listOf(squirtle),
                (vm.estado.value as UiState.Exito).datos
            )

            vm.limpiarBusqueda()
            assertEquals(3, (vm.estado.value as UiState.Exito).datos.size)
        }

    @Test
    fun `una busqueda sin coincidencias devuelve una lista vacia`() =
        runTest(mainDispatcherRule.dispatcher.scheduler) {
            val repo = RepoFalso(itemsIniciales = listOf(charmander, squirtle))
            val vm = ListaViewModel(repo, NotificadorFalso())
            advanceUntilIdle()

            vm.buscar("pikachu")

            assertEquals(
                emptyList<Item>(),
                (vm.estado.value as UiState.Exito).datos
            )
        }
}

/** Repositorio falso: no toca Room ni Retrofit. */
private class RepoFalso(
    itemsIniciales: List<Item> = emptyList(),
    private val resultadoRefresco: Result<Int> = Result.success(0),
    private val cantidadGuardadaValor: Int = itemsIniciales.size
) : ItemRepository {

    private val itemsFlow = MutableStateFlow(itemsIniciales)
    private val favoritosFlow = MutableStateFlow(itemsIniciales.filter { it.esFavorito })

    override val items: Flow<List<Item>> = itemsFlow
    override val favoritos: Flow<List<Item>> = favoritosFlow

    override suspend fun refrescar(): Result<Int> = resultadoRefresco

    override suspend fun obtenerDetalle(id: String): Result<Item> {
        val item = itemsFlow.value.firstOrNull { it.id == id }
        return if (item == null) {
            Result.failure(NoSuchElementException("No existe $id"))
        } else {
            Result.success(item)
        }
    }

    override suspend fun alternarFavorito(id: String) {
        itemsFlow.value = itemsFlow.value.map { item ->
            if (item.id == id) item.copy(esFavorito = !item.esFavorito) else item
        }
    }

    override suspend fun cantidadGuardada(): Int = cantidadGuardadaValor

    override suspend fun cantidadFavoritos(): Int =
        itemsFlow.value.count { it.esFavorito }

    override suspend fun limpiarCache() {
        itemsFlow.value = emptyList()
        favoritosFlow.value = emptyList()
    }
}

/** Notificador falso: solo guarda lo último que se mostró. */
private class NotificadorFalso : Notificador {

    var ultimoTitulo: String? = null
        private set
    var ultimoTexto: String? = null
        private set

    override fun mostrar(titulo: String, texto: String) {
        ultimoTitulo = titulo
        ultimoTexto = texto
    }
}
