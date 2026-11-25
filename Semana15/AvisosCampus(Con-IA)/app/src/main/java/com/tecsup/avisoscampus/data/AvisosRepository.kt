package com.tecsup.avisoscampus.data

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tecsup.avisoscampus.avisosDataStore
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface AvisosRepository {
    val avisos: StateFlow<List<Aviso>>
    fun registrar(titulo: String, texto: String, origen: Origen): Aviso
}

class AvisosRepositoryPersistente(context: Context) : AvisosRepository {
    private val _avisos = MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos = _avisos.asStateFlow()
    private var siguiente = 1
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val dataStore = context.applicationContext.avisosDataStore

    init {
        scope.launch {
            val guardados = dataStore.data.first()[HISTORIAL]
                ?.let(::decodificar)
                .orEmpty()
            _avisos.value = guardados
            siguiente = (guardados.maxOfOrNull { it.id } ?: 0) + 1
        }
    }

    @Synchronized
    override fun registrar(titulo: String, texto: String, origen: Origen): Aviso {
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val aviso = Aviso(siguiente++, titulo, texto, origen, hora)
        _avisos.value = listOf(aviso) + _avisos.value
        scope.launch {
            dataStore.edit { preferencias ->
                preferencias[HISTORIAL] = codificar(_avisos.value)
            }
        }
        return aviso
    }

    private fun codificar(avisos: List<Aviso>): String = avisos.joinToString("\n") { aviso ->
        listOf(aviso.id.toString(), aviso.titulo, aviso.texto, aviso.origen.name, aviso.hora)
            .joinToString("\t") { Base64.encodeToString(it.toByteArray(Charsets.UTF_8), Base64.NO_WRAP) }
    }

    private fun decodificar(contenido: String): List<Aviso> = contenido.lineSequence().mapNotNull { linea ->
        runCatching {
            val partes = linea.split('\t').map { String(Base64.decode(it, Base64.NO_WRAP), Charsets.UTF_8) }
            Aviso(partes[0].toInt(), partes[1], partes[2], Origen.valueOf(partes[3]), partes[4])
        }.getOrNull()
    }.toList()

    private companion object {
        val HISTORIAL = stringPreferencesKey("historial")
    }
}
