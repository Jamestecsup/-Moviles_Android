package com.huaman.cafecampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.huaman.cafecampus.data.local.dao.PedidoDao
import com.huaman.cafecampus.data.local.dao.PromocionDao
import com.huaman.cafecampus.data.local.dao.ProductoDao
import com.huaman.cafecampus.data.local.dao.UsuarioDao
import com.huaman.cafecampus.data.local.entity.DetallePedido
import com.huaman.cafecampus.data.local.entity.Pedido
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.data.local.entity.Usuario

/**
 * Base de datos local de CafeCampus (SQLite: `cafecampus.db`).
 *
 * Es un SINGLETON: toda la app usa la misma instancia, asi el estudiante y el
 * administrador ven exactamente los mismos datos.
 *
 * NOTA (pista del laboratorio): si cambias una entidad despues de ejecutar la
 * app, sube `version` o desinstala la app del emulador. Si no, Room falla
 * porque el esquema guardado no coincide con el nuevo.
 */
@Database(
    entities = [
        Producto::class,
        Usuario::class,
        Pedido::class,
        DetallePedido::class,
        Promocion::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CafeDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    abstract fun usuarioDao(): UsuarioDao

    abstract fun pedidoDao(): PedidoDao

    abstract fun promocionDao(): PromocionDao

    companion object {
        private const val NOMBRE = "cafecampus.db"

        @Volatile
        private var INSTANCE: CafeDatabase? = null

        fun obtener(context: Context): CafeDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CafeDatabase::class.java,
                    NOMBRE
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}