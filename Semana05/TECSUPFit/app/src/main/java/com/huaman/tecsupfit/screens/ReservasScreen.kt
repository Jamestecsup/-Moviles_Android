package com.tecsup.tecsupfit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.tecsupfit.model.Reserva
import com.tecsup.tecsupfit.ui.components.ReservaItem

@Composable
fun ReservasScreen(reservas: List<Reserva>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Mis reservas", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))

        if (reservas.isEmpty()) {
            Text(
                "Aún no tienes reservas.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(reservas) { reserva -> ReservaItem(reserva = reserva) }
            }
        }
    }
}