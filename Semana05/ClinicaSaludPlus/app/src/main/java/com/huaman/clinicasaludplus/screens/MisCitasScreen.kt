package com.huaman.clinicasaludplus.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import com.huaman.clinicasaludplus.model.Cita
import com.huaman.clinicasaludplus.model.EstadoCita
import com.huaman.clinicasaludplus.model.citasMock
import com.huaman.clinicasaludplus.ui.theme.PurpuraPrimario
import com.huaman.clinicasaludplus.ui.theme.Verde

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen(
    navController: NavController,
    onNavigateToDrawer: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Clínica Salud+",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Hola, Juan",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateToDrawer("mis_citas") }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Mis citas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(citasMock) { cita ->
                CitaCard(cita = cita)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CitaCard(cita: Cita) {
    val backgroundColor = if (cita.estado == EstadoCita.CONFIRMADA) {
        Color(0xFFF3E8FF)
    } else {
        Color(0xFFF0F9FF)
    }

    val borderColor = if (cita.estado == EstadoCita.CONFIRMADA) {
        PurpuraPrimario
    } else {
        Color(0xFF93C5FD)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Barra lateral de color
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(60.dp)
                        .background(
                            color = borderColor,
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Información de la cita
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = cita.medico.nombre,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${cita.medico.especialidad} • Viernes ${cita.fecha}, ${cita.hora}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Badge de estado
                Box(
                    modifier = Modifier
                        .background(
                            color = if (cita.estado == EstadoCita.CONFIRMADA) Verde else Color(0xFF60A5FA),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (cita.estado == EstadoCita.CONFIRMADA) "Confirmada" else "Completada",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
