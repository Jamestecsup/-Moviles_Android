package com.huaman.cafecampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.huaman.cafecampus.data.local.entity.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Acceso a la tabla `usuarios`.
 *
 * LECTURA -> Flow, ESCRITURA -> suspend. Se complementan, no se reemplazan.
 */
@Dao
interface UsuarioDao {

    /** Login: devuelve el usuario si correo y clave coinciden, o null. */
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun porId(id: Int): Usuario?

    /**
     * Primer usuario de un rol. Lo usa el Splash para recuperar la sesion
     * guardada en DataStore (que solo recuerda el ROL, no el id).
     */
    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY id LIMIT 1")
    suspend fun porRol(rol: String): Usuario?

    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun observarTodos(): Flow<List<Usuario>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(u: Usuario): Long

    /**
     * Registra un usuario NUEVO sin reemplazar el existente.
     *
     * IGNORE hace que el INSERT no haga nada si el correo ya existe (el
     * indice UNIQUE de `usuarios.correo` lo rechaza). Por eso el DAO
     * devuelve el id generado, que es -1 cuando NO se pudo insertar.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun registrar(u: Usuario): Long

    /** El correo ya esta en uso? Sirve para validar antes de insertar. */
    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo")
    suspend fun existeCorreo(correo: String): Int

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}