package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.CafeButton
import com.huaman.cafecampus.ui.components.CafeButtonSecundario
import com.huaman.cafecampus.ui.components.CafeTextField

/**
 * Crear / editar una promocion.
 *
 * Si [promocion] viene con datos, se edita esa; si viene null, se crea una
 * nueva. [onGuardar] recibe el id (0 = nueva) y los datos del formulario.
 */
@Composable
fun FormularioPromocionScreen(
    promocion: Promocion?,
    error: String?,
    onGuardar: (
        id: Int,
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
    var tipo by remember(promocion?.id) {
        mutableStateOf(promocion?.tipo ?: "PRODUCTO")
    }
    var valorReferencia by remember(promocion?.id) {
        mutableStateOf(promocion?.valorReferencia ?: "")
    }
    var descuento by remember(promocion?.id) {
        // OJO: promocion?.descuentoPorcentaje.toString() devolvia la cadena
        // "null" cuando la promocion es nueva. Hay que comprobar el null
        // ANTES de convertir a texto.
        mutableStateOf(promocion?.descuentoPorcentaje?.let { formatearDescuento(it) } ?: "")
    }
    var dias by remember(promocion?.id) {
        mutableStateOf(promocion?.let { calcularDiasRestantes(it.fechaFin) } ?: 7)
    }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            titulo = if (promocion == null) "Nueva promocion" else "Editar promocion",
            onAtras = onAtras
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

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
                marcador = "Ej: Cafe americano o Bebidas",
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

            CafeTextField(
                valor = dias.toString(),
                onValorChange = { texto ->
                    val n = texto.filter { it.isDigit() }.toIntOrNull()
                    if (n != null) dias = n
                },
                etiqueta = "Vigencia (dias)",
                esNumero = true,
                marcador = "Ej: 7",
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
                texto = "Guardar promocion",
                onClick = {
                    val fechaFin = System.currentTimeMillis() + dias.toLong() * 24 * 60 * 60 * 1000
                    onGuardar(promocion?.id ?: 0, tipo, valorReferencia, descuento, fechaFin)
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

/** "20" en vez de "20.0". */
private fun formatearDescuento(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toInt().toString() else valor.toString()

/** Dias que faltan para que termine la promocion (minimo 1). */
private fun calcularDiasRestantes(fechaFin: Long): Int {
    val ms = fechaFin - System.currentTimeMillis()
    if (ms <= 0L) return 1
    return ((ms / (24 * 60 * 60 * 1000L)).toInt()).coerceAtLeast(1)
}