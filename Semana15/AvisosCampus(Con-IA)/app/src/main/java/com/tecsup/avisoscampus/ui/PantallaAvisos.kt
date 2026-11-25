package com.tecsup.avisoscampus.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tecsup.avisoscampus.model.PermisoUi
import com.tecsup.avisoscampus.notificacion.Notificador
import com.tecsup.avisoscampus.receiver.CargadorReceiver
import com.tecsup.avisoscampus.receiver.ModoAvionReceiver
import com.tecsup.avisoscampus.viewmodel.AvisosViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val faltan by vm.faltan.collectAsState()
    val escuchandoAvion by vm.escuchandoAvion.collectAsState()
    val escuchandoCargador by vm.escuchandoCargador.collectAsState()
    val avisos by vm.avisos.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        vm.permisoRespondido(it)
    }

    val propietario = LocalLifecycleOwner.current
    DisposableEffect(propietario, contexto, vm) {
        val observador = LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_RESUME) {
                val concedido = Notificador.tienePermiso(contexto)
                if (concedido || vm.permiso.value != PermisoUi.SinPedir) {
                    vm.permisoRespondido(concedido)
                }
            }
        }
        propietario.lifecycle.addObserver(observador)
        onDispose { propietario.lifecycle.removeObserver(observador) }
    }

    LaunchedEffect(contexto, vm) {
        vm.eventos.collect { aviso ->
            if (!Notificador.mostrar(contexto, aviso)) vm.noSePudoMostrar()
        }
    }

    if (escuchandoAvion) {
        DisposableEffect(contexto, vm) {
            val receptor = ModoAvionReceiver(vm::alCambiarModoAvion)
            ContextCompat.registerReceiver(
                contexto,
                receptor,
                IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose { contexto.unregisterReceiver(receptor) }
        }
    }

    if (escuchandoCargador) {
        DisposableEffect(contexto, vm) {
            val receptor = CargadorReceiver(vm::alCambiarCargador)
            val filtro = IntentFilter().apply {
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            }
            ContextCompat.registerReceiver(contexto, receptor, filtro, ContextCompat.RECEIVER_NOT_EXPORTED)
            onDispose { contexto.unregisterReceiver(receptor) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AvisosCampus", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { margenes ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(margenes)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaAvisos("Permiso de notificaciones") {
                EtiquetaPermiso(permiso)
                Button(onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        vm.permisoRespondido(true)
                    }
                }) {
                    Text("Permitir notificaciones")
                }
                mensaje?.let { texto ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ) {
                        Text(texto, modifier = Modifier.padding(12.dp))
                    }
                }
                if (permiso == PermisoUi.Denegado || mensaje != null) {
                    OutlinedButton(onClick = {
                        val ajustes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName)
                        } else {
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", contexto.packageName, null)
                            )
                        }
                        contexto.startActivity(ajustes)
                    }) {
                        Text("Abrir ajustes")
                    }
                }
            }
            TarjetaAvisos("Avisos") {
                Button(onClick = vm::avisarAhora) {
                    Text("Mostrar aviso")
                }
            }
            TarjetaAvisos("Recordatorio") {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(onClick = { vm.recordarEn() }, enabled = faltan == null) {
                        Text("Recordar en 10 s")
                    }
                    OutlinedButton(onClick = vm::cancelarRecordatorio, enabled = faltan != null) {
                        Text("Cancelar")
                    }
                }
                faltan?.let { segundos ->
                    Text("Faltan $segundos s", style = MaterialTheme.typography.bodyMedium)
                }
            }
            TarjetaAvisos("Receptor de anuncios") {
                InterruptorEscucha("Escuchar el modo avión", escuchandoAvion, vm::cambiarEscucha)
                InterruptorEscucha("Escuchar el cargador", escuchandoCargador, vm::cambiarEscuchaCargador)
            }
            HistorialAvisos(avisos)
        }
    }
}

@Composable
private fun InterruptorEscucha(texto: String, activo: Boolean, alCambiar: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = activo, role = Role.Switch, onValueChange = alCambiar),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(texto, modifier = Modifier.weight(1f))
        Switch(checked = activo, onCheckedChange = null)
    }
}

@Composable
internal fun TarjetaAvisos(titulo: String, contenido: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            contenido()
        }
    }
}

@Composable
private fun EtiquetaPermiso(permiso: PermisoUi) {
    val (texto, fondo, tinta) = when (permiso) {
        PermisoUi.SinPedir -> Triple("Sin pedir", Color(0xFFE8EAED), Color(0xFF3B4148))
        PermisoUi.Concedido -> Triple("Permitido", Color(0xFFD6EFDE), Color(0xFF135A36))
        PermisoUi.Denegado -> Triple("Denegado", Color(0xFFFBE1DF), Color(0xFF8B2721))
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Estado", style = MaterialTheme.typography.bodyMedium)
        Surface(shape = RoundedCornerShape(50), color = fondo, contentColor = tinta) {
            Text(
                text = texto,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
