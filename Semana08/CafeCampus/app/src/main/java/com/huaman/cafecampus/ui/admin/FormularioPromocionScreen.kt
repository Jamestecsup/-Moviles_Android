package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.CafeButton
import com.huaman.cafecampus.ui.components.CafeButtonSecundario
import com.huaman.cafecampus.ui.components.CafeTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioPromocionScreen(
    promocion: Promocion?,
    categorias: List<String>,
    error: String?,
    onGuardar: (
        tipo: String,
        valorReferencia: String,
        descuentoPorcentaje: String,
        fechaFinMillis: Long
    ) -> Unit,
    modifier: Modifier = Modifier,
    onCancelar: () -> Unit = {},
    onAtras: () -> Unit = {},
    onLimpiarError: () -> Unit = {}
) {
    var tipo by remember { mutableStateOf(promocion?.tipo ?: "PRODUCTO") }
    var valorReferencia by remember { mutableStateOf(promocion?.valorReferencia ?: "") }
    var descuento by remember { mutableStateOf(promocion?.descuentoPorcentaje.toString() ?: "") }

    var menuTipoAbierto by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            titulo = if (promocion == null) "Nueva promoción" else "Editar promoción",
            onAtras = onAtras
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // Tipo de promocion - campo de texto simple
            CafeTextField(
                valor = tipo,
                onValorChange = { tipo = it; onLimpiarError() },
                etiqueta = "Tipo (PRODUCTO / CATEGORIA)",
                marcador = "Ej: PRODUCTO o CATEGORIA",
                modifier = Modifier.padding(top = 16.dp)
            )

            CafeTextField(
                valor = valorReferencia,
                onValorChange = { valorReferencia = it; onLimpiarError() },
                etiqueta = "Producto o Categoria",
                marcador = "Ej: Café americano o Bebidas",
                modifier = Modifier.padding(top = 12.dp)
            )

            CafeTextField(
                valor = descuento,
                onValorChange = { descuento = it; onLimpiarError() },
                etiqueta = "Descuento (%)",
                esNumero = true,
                decimal = true,
                marcador = "Ej: 20",
                modifier = Modifier.padding(top = 12.dp)
            )

            if (error != null) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            CafeButton(
                texto = "Guardar promoción",
                onClick = {
                    val fechaFin = System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000
                    onGuardar(tipo, valorReferencia, descuento, fechaFin)
                },
                modifier = Modifier.padding(top = 20.dp)
            )

            CafeButtonSecundario(
                texto = "Cancelar",
                onClick = onCancelar,
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp)
            )
        }
    }
}