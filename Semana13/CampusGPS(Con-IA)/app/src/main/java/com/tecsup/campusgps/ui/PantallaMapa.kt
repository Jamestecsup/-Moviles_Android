package com.tecsup.campusgps.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.tecsup.campusgps.model.UbicacionUi
import com.tecsup.campusgps.util.formatearDistancia
import com.tecsup.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(
    vm: MapaViewModel,
    modifier: Modifier = Modifier
) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()
    val contexto = LocalContext.current

    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        val concedido = resultado.values.any { it }
        if (concedido) {
            vm.buscar()
        } else {
            vm.permisoDenegado()
        }
    }

    fun alPulsar() {
        val finePermiso = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarsePermiso = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (finePermiso || coarsePermiso) {
            vm.buscar()
        } else {
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (estado is UbicacionUi.Error) {
            val errorActual = estado as UbicacionUi.Error
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { alPulsar() },
                    enabled = estado !is UbicacionUi.Buscando
                ) {
                    Text("Obtener mi ubicación")
                }

                Spacer(modifier = Modifier.height(16.dp))

                MensajeError(
                    error = errorActual,
                    onReintentar = { alPulsar() }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MapaUbicacion(
                    estado = estado,
                    lugares = lugares,
                    seleccionado = seleccionado,
                    masCercanoId = masCercano?.id,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { alPulsar() },
                        enabled = estado !is UbicacionUi.Buscando
                    ) {
                        Text(
                            if (estado is UbicacionUi.Buscando) "Buscando..."
                            else "Obtener mi ubicación"
                        )
                    }

                    if (masCercano != null) {
                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedButton(
                            onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                            enabled = masCercano != null
                        ) {
                            Text("Ir al más cercano")
                        }

                        val distanciaCercano = lugares.find { it.lugar.id == masCercano?.id }?.metros
                        val textoDistancia = distanciaCercano?.let { " (${formatearDistancia(it)})" } ?: ""
                        Text(
                            text = "Más cercano: ${masCercano?.nombre}$textoDistancia",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    when (val actual = estado) {
                        is UbicacionUi.Inicial -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Pulsa el botón para empezar")
                        }
                        is UbicacionUi.Buscando -> {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Obteniendo ubicación...")
                        }
                        is UbicacionUi.Lista -> {
                            ListaLugares(
                                lugares = lugares,
                                seleccionado = seleccionado,
                                onClick = { vm.seleccionar(it) },
                                masCercanoId = masCercano?.id,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                            )
                        }
                        is UbicacionUi.Error -> {
                            // Handled above
                        }
                    }
                }
            }
        }
    }
}
