package com.huaman.cafecampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.entity.Usuario
import com.huaman.cafecampus.model.Rol
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.CafeButtonSecundario
import com.huaman.cafecampus.ui.components.DatoConIcono
import com.huaman.cafecampus.ui.components.soles

/**
 * Perfil del estudiante (tercer item de la barra inferior).
 *
 * Muestra los datos de la cuenta y permite cerrar sesion.
 */
@Composable
fun PerfilScreen(
    usuario: Usuario?,
    unidades: Int,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Perfil")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // Tarjeta de la cuenta
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = usuario?.nombre ?: "Estudiante",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = usuario?.correo ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (usuario?.rolTipado == Rol.ADMIN) {
                                "Perfil Administrador"
                            } else {
                                "Perfil Estudiante"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Resumen rapido
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "En este momento",
                        style = MaterialTheme.typography.titleSmall
                    )
                    DatoConIcono(
                        icon = Icons.Filled.Person,
                        texto = if (unidades == 0) {
                            "No tienes productos en el pedido"
                        } else {
                            "$unidades producto(s) en la bolsa"
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Text(
                text = "Datos ficticios de demostracion (Ley 29733).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )

            CafeButtonSecundario(
                texto = "Cerrar sesion",
                onClick = onCerrarSesion,
                modifier = Modifier.padding(top = 20.dp, bottom = 24.dp),
                colorTexto = MaterialTheme.colorScheme.error
            )
        }
    }
}

/** Texto suelto del total, reutilizado en varias pantallas. */
@Composable
fun TotalGrande(total: Double, modifier: Modifier = Modifier) {
    Text(
        text = soles(total),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}