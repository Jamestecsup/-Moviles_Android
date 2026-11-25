package com.huaman.registrodenotascon_ia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huaman.registrodenotascon_ia.model.*
import com.huaman.registrodenotascon_ia.logic.Evaluador

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RegistroNotasScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNotasScreen() {
    var nota1 by remember { mutableStateOf(0f) }
    var nota2 by remember { mutableStateOf(0f) }
    var nota3 by remember { mutableStateOf(0f) }
    var nota4 by remember { mutableStateOf(0f) }

    var redondear by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }

    var calculado by remember { mutableStateOf(false) }
    var resultadoPonderado by remember { mutableStateOf(0.0) }
    var resultadoFinal by remember { mutableStateOf(0.0) }
    var resultadoObservacion by remember { mutableStateOf("") }
    var resultadoColor by remember { mutableStateOf(Color.Gray) }
    var listaDetalles by remember { mutableStateOf(listOf<String>()) }

    val primaryPurple = Color(0xFF5E35B1)
    val lightBackground = Brush.verticalGradient(listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5)))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Notas", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryPurple)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                Text("Desarrollado por: James Huaman Quispe", fontSize = 12.sp, color = Color.Gray)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(lightBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Notas del ciclo", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Desliza para asignar cada nota (0 a 20)", fontSize = 12.sp, color = Color.Gray)

            CursoSliderRow("Fundamentos de Programación", "(20%)", nota1) { nota1 = it }
            CursoSliderRow("Programación Orientada a Objetos", "(25%)", nota2) { nota2 = it }
            CursoSliderRow("Programación en Móviles", "(30%)", nota3) { nota3 = it }
            CursoSliderRow("Base de Datos", "(25%)", nota4) { nota4 = it }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Redondear promedio final", fontSize = 14.sp)
                Switch(checked = redondear, onCheckedChange = { redondear = it })
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = confirmado, onCheckedChange = { confirmado = it })
                Text("Confirmo que las notas son correctas", fontSize = 14.sp)
            }

            Button(
                onClick = {
                    val cursoF = CursoFundamentos().apply { nota = nota1.toInt() }
                    val cursoP = CursoPOO().apply { nota = nota2.toInt() }
                    val cursoM = CursoMoviles().apply { nota = nota3.toInt() }
                    val cursoB = CursoBaseDatos().apply { nota = nota4.toInt() }

                    val listaCursos = listOf(cursoF, cursoP, cursoM, cursoB)
                    val evaluador = Evaluador(listaCursos, redondear)

                    resultadoPonderado = evaluador.calcularPromedioPonderado()
                    resultadoFinal = evaluador.calcularPromedioFinal()
                    resultadoObservacion = evaluador.obtenerObservacion(resultadoFinal)
                    resultadoColor = Color(evaluador.obtenerColorHex(resultadoFinal))

                    listaDetalles = listaCursos.map { it.obtenerDetalle() }

                    calculado = true
                },
                enabled = confirmado,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("CALCULAR PROMEDIO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            if (!calculado) {
                Text("Asigna las notas y confirma para calcular", fontSize = 13.sp, color = Color.Gray)
            } else {
                Text("✓ Promedio calculado correctamente", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Promedio ponderado: %.2f".format(resultadoPonderado), color = Color.DarkGray)

                        Text(
                            text = if(redondear) "Promedio final: ${resultadoFinal.toInt()}" else "Promedio final: %.2f".format(resultadoFinal),
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = primaryPurple
                        )
                        if (redondear) Text("(redondeado)", fontSize = 11.sp, color = Color.Gray)

                        Surface(shape = RoundedCornerShape(16.dp), color = resultadoColor.copy(alpha = 0.2f)) {
                            Text(resultadoObservacion, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = resultadoColor, fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = DividerDefaults.Thickness,
                            color = DividerDefaults.color
                        )
                        Text("Aporte por curso:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        listaDetalles.forEach { detalle ->
                            Text(detalle, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CursoSliderRow(nombre: String, pesoText: String, notaFloat: Float, onNotaChange: (Float) -> Unit) {
    val notaInt = notaFloat.toInt()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(pesoText, fontSize = 11.sp, color = Color.Gray)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFEDE7F6)) {
                Text("$notaInt", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFF5E35B1), fontWeight = FontWeight.Bold)
            }
        }
        Slider(value = notaFloat, onValueChange = onNotaChange, valueRange = 0f..20f, steps = 19)
    }
}