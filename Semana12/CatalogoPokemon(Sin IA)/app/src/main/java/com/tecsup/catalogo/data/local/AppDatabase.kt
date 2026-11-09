package com.tecsup.catalogo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ItemEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        private const val NOMBRE = "catalogo.db"

        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, NOMBRE).build()
    }
}
