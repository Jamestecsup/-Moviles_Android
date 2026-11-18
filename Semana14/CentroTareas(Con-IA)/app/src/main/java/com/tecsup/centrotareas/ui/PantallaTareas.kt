package com.tecsup.centrotareas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()

    var toques by rememberSaveable { mutableIntStateOf(0) }
    var textoPrimos by rememberSaveable { mutableStateOf("500000") }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // TARJETA 1: Tarea con progreso
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Tarea con progreso",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Button(
                        onClick = { toques++ },
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("Tócame: $toques")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = { vm.tareaLarga() },
                            enabled = !corriendo
                        ) {
                            Text("Iniciar")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = { vm.cancelar() },
                            enabled = corriendo
                        ) {
                            Text("Cancelar")
                        }
                    }

                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Text(
                        text = "${(progreso * 100).toInt()} %",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = mensaje,
                        fontSize = 14.sp,
                        color = if (mensaje == "Cancelado") RojoError else Color.DarkGray
                    )
                }
            }

            // TARJETA 2: Contar números primos
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Contar números primos",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    OutlinedTextField(
                        value = textoPrimos,
                        onValueChange = { textoPrimos = it },
                        label = { Text("Hasta el número") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val calculando = primos is PrimosUi.Calculando

                        Button(
                            onClick = {
                                val n = textoPrimos.toIntOrNull() ?: 0
                                if (n >= 2) vm.contar(n)
                            },
                            enabled = !calculando
                        ) {
                            Text("Contar")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedButton(
                            onClick = { vm.cancelarPrimos() },
                            enabled = calculando
                        ) {
                            Text("Cancelar")
                        }
                    }

                    when (val actual = primos) {
                        is PrimosUi.Inicial -> {}
                        is PrimosUi.Calculando -> {
                            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                            Text("Calculando primos en segundo plano...", fontSize = 13.sp, color = Color.Gray)
                        }
                        is PrimosUi.Cancelado -> {
                            Text("Cálculo cancelado", color = RojoError, fontWeight = FontWeight.Medium)
                        }
                        is PrimosUi.Listo -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VerdeContenedor)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Primos hasta $textoPrimos: ${actual.cantidad}",
                                        color = VerdeExito,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Tiempo: ${actual.milisegundos} ms",
                                        color = VerdeExito,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // TARJETA 3: Dos cargas
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Dos cargas",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Switch(
                            checked = simularError,
                            onCheckedChange = { vm.cambiarSimularError(it) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simular error", fontSize = 14.sp)
                    }

                    val cargando = carga is CargaUi.Cargando

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = { vm.cargarUnaPorUna() },
                            enabled = !cargando
                        ) {
                            Text("Una por una")
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { vm.cargarALaVez() },
                            enabled = !cargando
                        ) {
                            Text("A la vez")
                        }
                    }

                    when (val actual = carga) {
                        is CargaUi.Inicial -> {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF0F4F8))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "«Una por una» tarda ≈ 5000 ms · «A la vez» tarda ≈ 3000 ms",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                        is CargaUi.Cargando -> {
                            CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                        }
                        is CargaUi.Exito -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(VerdeContenedor)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Modo: ${actual.modo}",
                                        fontWeight = FontWeight.Bold,
                                        color = VerdeExito,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Perfil: ${actual.perfil}", color = VerdeExito, fontSize = 13.sp)
                                    Text("Pedidos: ${actual.pedidos}", color = VerdeExito, fontSize = 13.sp)
                                    Text("Tiempo: ${actual.milisegundos} ms", color = VerdeExito, fontSize = 13.sp)
                                }
                            }
                        }
                        is CargaUi.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RojoContenedor)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = actual.mensaje,
                                    color = RojoError,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }

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
}
