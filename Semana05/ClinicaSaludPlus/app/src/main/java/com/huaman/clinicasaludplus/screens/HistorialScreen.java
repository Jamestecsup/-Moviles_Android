package com.example.clinicasaludplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.clinicasaludplus.ui.theme.PurpuraPrimario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
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
                    IconButton(onClick = { onNavigateToDrawer("historial") }) {
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
            text = "Historial médico",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
    )
}

item {
    Column(
            modifier = Modifier
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))
        Text(
                text = "Tu historial médico aparecerá aquí",
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
        )
    }
}
        }
                }
                }