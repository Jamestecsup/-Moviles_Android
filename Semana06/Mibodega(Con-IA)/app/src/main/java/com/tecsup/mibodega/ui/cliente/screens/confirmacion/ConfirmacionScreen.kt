package com.tecsup.mibodega.ui.cliente.screens.confirmacion

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.mibodega.ui.cliente.modelo.PedidoConfirmado
import com.tecsup.mibodega.ui.componentes.BotonPrimario
import com.tecsup.mibodega.ui.componentes.BotonSecundario
import com.tecsup.mibodega.ui.theme.BodegaTheme
import com.tecsup.mibodega.ui.theme.VerdeBodega

/**
 * Pantalla 7: Confirmación (mockup "Cliente").
 * Solo informa que el pedido quedó registrado.
 */
@Composable
fun ConfirmacionScreen(
    pedido: PedidoConfirmado,
    onVolverAlInicio: () -> Unit
) {
    var mostrarEstado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Pedido realizado",
            tint = VerdeBodega,
            modifier = Modifier.size(96.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "¡Pedido realizado!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Tu pedido está siendo preparado\ny será entregado pronto.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Pedido #${pedido.numero}",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Total: S/ %.2f".format(pedido.total),
            style = MaterialTheme.typography.titleMedium,
            color = VerdeBodega
        )

        Text(
            text = "Dirección: ${pedido.direccion}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        BotonPrimario(
            texto = "Ver estado del pedido",
            onClick = { mostrarEstado = true }
        )

        Spacer(Modifier.height(12.dp))

        BotonSecundario(
            texto = "Volver al inicio",
            onClick = onVolverAlInicio
        )

        Spacer(Modifier.height(24.dp))
    }

    if (mostrarEstado) {
        AlertDialog(
            onDismissRequest = { mostrarEstado = false },
            confirmButton = {
                TextButton(onClick = { mostrarEstado = false }) {
                    Text("Entendido")
                }
            },
            title = { Text("Pedido #${pedido.numero}") },
            text = { Text("Tu pedido está siendo preparado y llegará en 30-45 min.") }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ConfirmacionPreview() {
    BodegaTheme {
        ConfirmacionScreen(
            pedido = PedidoConfirmado(
                numero = "1024",
                total = 25.90,
                metodoPago = "Yape",
                direccion = "Av. Los Olivos 123",
                referencia = "Frente al parque"
            ),
            onVolverAlInicio = {}
        )
    }
}
