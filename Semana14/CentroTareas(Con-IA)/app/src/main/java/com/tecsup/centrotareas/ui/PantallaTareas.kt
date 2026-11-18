package com.tecsup.centrotareas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.centrotareas.model.CargaUi
import com.tecsup.centrotareas.model.PrimosUi
import com.tecsup.centrotareas.ui.theme.AzulPrincipal
import com.tecsup.centrotareas.ui.theme.RojoContenedor
import com.tecsup.centrotareas.ui.theme.RojoError
import com.tecsup.centrotareas.ui.theme.VerdeContenedor
import com.tecsup.centrotareas.ui.theme.VerdeExito
import com.tecsup.centrotareas.viewmodel.TareasViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(
    vm: TareasViewModel,
    modifier: Modifier = Modifier
) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    val primos by vm.primos.collectAsState()
    val progresoPrimos by vm.progresoPrimos.collectAsState()
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    val comparacion by vm.comparacion.collectAsState()

    var toques by rememberSaveable { mutableIntStateOf(0) }
    var textoPrimos by rememberSaveable { mutableStateOf("500000") }
    val hasta = textoPrimos.toIntOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // TARJETA 1: Tarea con progreso
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tarea con progreso",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(onClick = { toques++ }) {
                        Text("Tócame: $toques")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { vm.tareaLarga() },
                            enabled = !corriendo,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Iniciar")
                        }

                        OutlinedButton(
                            onClick = { vm.cancelar() },
                            enabled = corriendo,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "${(progreso * 100).toInt()} %",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (mensaje == "Cancelado") RojoError else Color.DarkGray
                    )
                }
            }

            // TARJETA 2: Contar números primos (con reporte de progreso en vivo)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Contar números primos",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val calculando = primos is PrimosUi.Calculando

                    OutlinedTextField(
                        value = textoPrimos,
                        onValueChange = { textoPrimos = it },
                        label = { Text("Hasta el número") },
                        singleLine = true,
                        enabled = !calculando,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (hasta != null && hasta >= 2) vm.contar(hasta)
                            },
                            enabled = hasta != null && hasta >= 2 && !calculando,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Contar")
                        }

                        OutlinedButton(
                            onClick = { vm.cancelarPrimos() },
                            enabled = calculando,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar")
                        }
                    }

                    when (val actual = primos) {
                        is PrimosUi.Inicial -> {}
                        is PrimosUi.Calculando -> {
                            LinearProgressIndicator(
                                progress = { progresoPrimos },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = "Progreso: ${(progresoPrimos * 100).toInt()} %",
                                style = MaterialTheme.typography.bodySmall,
                                color = AzulPrincipal
                            )
                        }
                        is PrimosUi.Cancelado -> {
                            Text("Cancelado", color = RojoError, style = MaterialTheme.typography.bodyMedium)
                        }
                        is PrimosUi.Listo -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = VerdeContenedor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Primos hasta $textoPrimos: ${actual.cantidad}",
                                        color = VerdeExito,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Tiempo: ${actual.milisegundos} ms",
                                        color = VerdeExito,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TARJETA 3: Dos cargas (Reintento automático transparente con backoff)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Dos cargas",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Switch(
                            checked = simularError,
                            onCheckedChange = { vm.cambiarSimularError(it) }
                        )
                        Text("Simular error", style = MaterialTheme.typography.bodyMedium)
                    }

                    val cargando = carga is CargaUi.Cargando

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { vm.cargarUnaPorUna() },
                            enabled = !cargando,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Una por una")
                        }

                        Button(
                            onClick = { vm.cargarALaVez() },
                            enabled = !cargando,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("A la vez")
                        }
                    }

                    when (val actual = carga) {
                        is CargaUi.Inicial -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF0F4F8))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "«Una por una» tarda ≈ 5000 ms · «A la vez» tarda ≈ 3000 ms",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        is CargaUi.Cargando -> {
                            CircularProgressIndicator(modifier = Modifier.padding(6.dp))
                        }
                        is CargaUi.Exito -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = VerdeContenedor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Modo: ${actual.modo}",
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeExito,
                                        fontSize = 14.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Perfil: ${actual.perfil}", color = VerdeExito, style = MaterialTheme.typography.bodySmall)
                                    Text("Pedidos: ${actual.pedidos}", color = VerdeExito, style = MaterialTheme.typography.bodySmall)
                                    Text("Tiempo: ${actual.milisegundos} ms", color = VerdeExito, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        is CargaUi.Error -> {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = RojoContenedor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = actual.mensaje,
                                        color = RojoError,
                                        fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedButton(
                                        onClick = { vm.reintentarCarga() }
                                    ) {
                                        Text("Reintentar")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // TARJETA 4: Comparación de tiempos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Comparación de tiempos",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        CeldaTabla("Modo", 1.4f, negrita = true)
                        CeldaTabla("Veces", 0.8f, negrita = true)
                        CeldaTabla("Último", 1f, negrita = true)
                        CeldaTabla("Promedio", 1.1f, negrita = true)
                    }

                    FilaComparacion(
                        modo = "Una por una",
                        tiempos = comparacion.unaPorUna,
                        promedio = comparacion.promedioUnaPorUna
                    )
                    FilaComparacion(
                        modo = "A la vez",
                        tiempos = comparacion.aLaVez,
                        promedio = comparacion.promedioALaVez
                    )

                    val ahorro = comparacion.ahorroMs
                    Text(
                        text = if (ahorro == null) {
                            "Ejecuta ambos modos para ver la diferencia"
                        } else {
                            "«A la vez» es $ahorro ms más rápido en promedio"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (ahorro == null) Color.Gray else VerdeExito
                    )

                    OutlinedButton(
                        onClick = { vm.limpiarComparacion() },
                        enabled = comparacion.unaPorUna.isNotEmpty() || comparacion.aLaVez.isNotEmpty(),
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text("Limpiar tabla")
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.CeldaTabla(texto: String, peso: Float, negrita: Boolean = false) {
    Text(
        text = texto,
        modifier = Modifier.weight(peso),
        fontWeight = if (negrita) FontWeight.Bold else FontWeight.Normal,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun FilaComparacion(modo: String, tiempos: List<Long>, promedio: Long?) {
    Row(modifier = Modifier.fillMaxWidth()) {
        CeldaTabla(modo, 1.4f)
        CeldaTabla("${tiempos.size}", 0.8f)
        CeldaTabla(tiempos.lastOrNull()?.let { "$it ms" } ?: "—", 1f)
        CeldaTabla(promedio?.let { "$it ms" } ?: "—", 1.1f)
    }
}
