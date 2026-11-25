package com.tecsup.avisoscampus

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.avisoscampus.notificacion.Notificador

val Context.avisosDataStore by preferencesDataStore(name = "avisos_historial")

class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanales(this)
    }
}
