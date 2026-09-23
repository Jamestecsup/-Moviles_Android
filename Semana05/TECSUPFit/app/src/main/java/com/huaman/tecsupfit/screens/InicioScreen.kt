package com.huaman.tecsupfit.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.huaman.tecsupfit.components.ClaseCard
import com.huaman.tecsupfit.components.FiltroChip
import com.huaman.tecsupfit.model.ClaseGym

@Composable
fun InicioScreen(
    clases: List<ClaseGym>,
    onClaseClick: (String) -> Unit
) {
    var categoriaSeleccionada by remember { mutableStateOf("Todos") }
    val categorias = listOf("Todos", "Fuerza", "Cardio", "Yoga")

    val clasesFiltradas = if (categoriaSeleccionada == "Todos") {
        clases
    } else {
        clases.filter { it.categoria.equals(categoriaSeleccionada, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Clases disponibles",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categorias.forEach { cat ->
                FiltroChip(
                    text = cat,
                    selected = categoriaSeleccionada == cat,
                    onClick = { categoriaSeleccionada = cat }
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(clasesFiltradas) { clase ->
                ClaseCard(
                    clase = clase,
                    onClick = { onClaseClick(clase.id) }
                )
            }
        }
    }
}
