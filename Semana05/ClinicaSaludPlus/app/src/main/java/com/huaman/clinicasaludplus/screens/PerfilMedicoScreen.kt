package com.huaman.clinicasaludplus.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.huaman.clinicasaludplus.model.medicosMock
import com.huaman.clinicasaludplus.ui.theme.PurpuraPrimario
import com.huaman.clinicasaludplus.ui.theme.PurpuraClaro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilMedicoScreen(
    navController: NavController,
    medicoId: Int
) {
    val medico = medicosMock.find { it.id == medicoId } ?: medicosMock[0]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del médico", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar grande
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = PurpuraClaro,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = medico.nombre.take(1),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpuraPrimario
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre
            Text(
                text = medico.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = medico.especialidad,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = medico.experiencia,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Calificación
            Box(
                modifier = Modifier
                    .background(
                        color = PurpuraClaro,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "⭐ ${medico.calificacion} (${medico.resenas} reseñas)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PurpuraPrimario
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descripción
            Text(
                text = medico.descripcion,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botón Agendar Cita
            Button(
                onClick = { navController.navigate("agendar_cita/${medico.id}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpuraPrimario
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Agendar cita",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
