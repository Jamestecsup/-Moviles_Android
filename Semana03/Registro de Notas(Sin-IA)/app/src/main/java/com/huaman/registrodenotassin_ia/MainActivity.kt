package com.huaman.registrodenotassin_ia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    // Estados para las notas de los 4 cursos
    var nota1 by remember{mutableStateOf(0f)}
    var nota2 by remember{mutableStateOf(0f)}
    var nota3 by remember{mutableStateOf(0f)}
    var nota4 by remember{mutableStateOf(0f)}

    val primaryPurple = Color(0xFF5E35B1)
    val lightBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Notas", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryPurple)
            )
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
            Column {
                Text(
                    text = "Notas del ciclo",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Desliza para asignar cada nota (0 a 20)",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            CursoSliderRow("Fundamentos de Programación", "(20%)", nota1) { nota1 = it }
            CursoSliderRow("Programación Orientada a Objetos", "(25%)", nota2) { nota2 = it }
            CursoSliderRow("Programación en Móviles", "(30%)", nota3) { nota3 = it }
            CursoSliderRow("Base de Datos", "(25%)", nota4) { nota4 = it }

            var redondear by remember { mutableStateOf(false) }
            var confirmado by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Redondear promedio final", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                Switch(
                    checked = redondear,
                    onCheckedChange = { redondear = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = primaryPurple)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = confirmado,
                    onCheckedChange = { confirmado = it }
                )
                Text(text = "Confirmo que las notas son correctas", fontSize = 14.sp, color = Color.Black)
            }

            Button(
                onClick = {},
                enabled = confirmado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryPurple,
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text(text = "CALCULAR PROMEDIO", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }

            Text(
                text = "Asigna las notas y confirma para calcular",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CursoSliderRow(
    nombre: String,
    pesoText: String,
    notaFloat: Float,
    onNotaChange: (Float) -> Unit
) {
    val notaInt = notaFloat.toInt()

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nombre, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = pesoText, fontSize = 11.sp, color = Color.Gray)
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFEDE7F6)
            ) {
                Text(
                    text = "$notaInt",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF5E35B1)
                )
            }
        }

        Slider(
            value = notaFloat,
            onValueChange = onNotaChange,
            valueRange = 0f..20f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF5E35B1),
                activeTrackColor = Color(0xFF5E35B1)
            )
        )
    }
}