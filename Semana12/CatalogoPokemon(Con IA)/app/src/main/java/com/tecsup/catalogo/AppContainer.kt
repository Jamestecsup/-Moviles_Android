package com.tecsup.catalogo

import android.content.Context
import com.tecsup.catalogo.data.auth.AuthRepository
import com.tecsup.catalogo.data.auth.FirebaseAuthRepository
import com.tecsup.catalogo.data.local.AppDatabase
import com.tecsup.catalogo.data.remote.RetrofitClient
import com.tecsup.catalogo.data.repository.ItemRepository
import com.tecsup.catalogo.data.repository.ItemRepositoryImpl
import com.tecsup.catalogo.domain.model.Sesion
import com.tecsup.catalogo.notification.Notificador
import com.tecsup.catalogo.notification.NotificadorAndroid

/**
 * Contenedor de dependencias. Se crea una sola vez en MainActivity y se pasa
 * a los ViewModel mediante la fábrica (nunca se usa Context dentro del VM).
 */
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val baseDeDatos by lazy { AppDatabase.crear(appContext) }
    private val api by lazy { RetrofitClient.crear() }

    val itemRepository: ItemRepository by lazy {
        ItemRepositoryImpl(api, baseDeDatos.itemDao())
    }

    /**
     * Si todavía no existe google-services.json, Firebase no puede inicializar.
     * En vez de cerrar la app, se entrega un repositorio que explica el problema
     * en la pantalla de login.
     */
    val authRepository: AuthRepository by lazy {
        try {
            FirebaseAuthRepository()
        } catch (e: Exception) {
            AuthRepositorySinConfigurar(e)
        }
    }

    val notificador: Notificador by lazy { NotificadorAndroid(appContext) }
}

private class AuthRepositorySinConfigurar(
    private val causa: Exception
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> =
        Result.failure(IllegalStateException(mensaje, causa))

    override suspend fun sesionActual(): Sesion? = null

    override fun cerrarSesion() = Unit

    private companion object {
        const val mensaje = "Firebase no está configurado. Agrega app/google-services.json."
    }
}
