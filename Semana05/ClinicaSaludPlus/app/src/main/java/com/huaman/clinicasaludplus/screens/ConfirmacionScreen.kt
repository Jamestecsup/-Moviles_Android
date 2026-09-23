package com.example.clinicasaludplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.clinicasaludplus.model.medicosMock
import com.example.clinicasaludplus.ui.theme.PurpuraPrimario
import com.example.clinicasaludplus.ui.theme.Verde

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmacionScreen(
        navController: NavController,
        medicoId: Int,
        fecha: String,
        hora: String
) {
    val medico = medicosMock.find { it.id == medicoId } ?: medicosMock[0]

    Scaffold(
            topBar = {
                    TopAppBar(
                            title = { Text("", color = Color.White) },
                            colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = PurpuraPrimario
                            )
                    )
            }
    ) { paddingValues ->
            Column(
                    modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues)
                            .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
            ) {
        // Ícono de confirmación (checkmark verde)
        Box(
                modifier = Modifier
                        .size(80.dp)
                        .background(
                                color = Color(0xFFD1FAE5),
                                shape = CircleShape
                        ),
                contentAlignment = Alignment.Center
        ) {
            Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Confirmado",
                    tint = Verde,
                    modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Título
        Text(
                text = "¡Cita agendada!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Resumen de la cita
        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(
                                color = Color(0xFFF3E8FF),
                                shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
        ) {
            Column {
                ResumenItem(
                        label = "Médico",
                        value = medico.nombre
                )
                Spacer(modifier = Modifier.height(12.dp))
                ResumenItem(
                        label = "Especialidad",
                        value = medico.especialidad
                )
                Spacer(modifier = Modifier.height(12.dp))
                ResumenItem(
                        label = "Fecha",
                        value = "Viernes $fecha"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ResumenItem(
                        label = "Hora",
                        value = hora
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón volver al inicio
        Button(
                onClick = {
                        navController.navigate("inicio") {
                        popUpTo("inicio") { inclusive = true }
                }
                },
                modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = PurpuraPrimario
                ),
                shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                    text = "Volver al inicio",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
            )
        }
    }
    }
}

@Composable
fun ResumenItem(
        label: String,
        value: String
) {
    Column {
        Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
        )
        Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
        )
    }
}