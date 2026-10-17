package com.huaman.cafecampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.huaman.cafecampus.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * La extension se declara UNA SOLA VEZ y a nivel de archivo, como pide el
 * laboratorio. Si se declarara dentro de una clase, cada instancia crearia su
 * propio DataStore y la app fallaria.
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes")

/**
 * Preferencias PEQUENAS guardadas en DataStore.
 *
 * DataStore es para clave-valor (perfil activo, modo oscuro, si la cafeteria
 * recibe pedidos). Los pedidos y los productos NO van aqui: van en Room.
 */
class PreferenciasStore(private val context: Context) {

    private object Claves {
        val PERFIL_ACTIVO = stringPreferencesKey("perfil_activo")
        val MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
        val RECIBIR_PEDIDOS = booleanPreferencesKey("recibir_pedidos")
    }

    /**
     * Si el archivo se corrompe, se devuelven las preferencias vacias en vez de
     * reventar la app. (IOException = disco lleno, archivo danado, etc.)
     */
    private val prefs: Flow<Preferences> = context.dataStore.data
        .catch { e ->
            if (e is IOException) emit(emptyPreferences()) else throw e
        }

    // -----------------------------------------------------------------------
    // Lectura -> un Flow por preferencia
    // -----------------------------------------------------------------------

    /** Perfil activo: "CLIENTE" | "ADMIN" | "" si no hay sesion. */
    val perfilActivo: Flow<String> = prefs.map { it[Claves.PERFIL_ACTIVO] ?: "" }

    val modoOscuro: Flow<Boolean> = prefs.map { it[Claves.MODO_OSCURO] ?: false }

    /** Por defecto la cafeteria SI recibe pedidos. */
    val recibirPedidos: Flow<Boolean> = prefs.map { it[Claves.RECIBIR_PEDIDOS] ?: true }

    // -----------------------------------------------------------------------
    // Escritura -> funciones suspend
    // -----------------------------------------------------------------------

    suspend fun guardarPerfil(rol: Rol) {
        context.dataStore.edit { it[Claves.PERFIL_ACTIVO] = rol.name }
    }

    /** Cambiar de perfil en modo practica, sin volver a loguearse. */
    suspend fun guardarPerfilNombre(rol: String) {
        context.dataStore.edit { it[Claves.PERFIL_ACTIVO] = rol }
    }

    suspend fun guardarModoOscuro(oscuro: Boolean) {
        context.dataStore.edit { it[Claves.MODO_OSCURO] = oscuro }
    }

    suspend fun guardarRecibirPedidos(acepta: Boolean) {
        context.dataStore.edit { it[Claves.RECIBIR_PEDIDOS] = acepta }
    }

    /** Cerrar sesion: borra el perfil. El resto de ajustes se conserva. */
    suspend fun cerrarSesion() {
        context.dataStore.edit { it.remove(Claves.PERFIL_ACTIVO) }
    }
}