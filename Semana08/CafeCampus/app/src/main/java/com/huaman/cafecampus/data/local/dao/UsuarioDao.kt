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

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}