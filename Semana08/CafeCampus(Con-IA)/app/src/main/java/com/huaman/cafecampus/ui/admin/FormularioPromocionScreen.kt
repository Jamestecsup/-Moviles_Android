package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.entity.Promocion
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.CafeButton
import com.huaman.cafecampus.ui.components.CafeButtonSecundario
import com.huaman.cafecampus.ui.components.CafeTextField

/**
 * Crear / editar una promocion.
 *
 * El TIPO y el PRODUCTO/CATEGORIA son desplegables: no se escriben a mano.
 * La lista de productos viene del Flow de Room, asi que se actualiza sola
 * cuando el administrador agrega un producto nuevo en "Menu".
 *
 * Si [promocion] viene con datos, se edita esa; si viene null, se crea una
 * nueva. [onGuardar] recibe el id (0 = nueva) y los datos del formulario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioPromocionScreen(
    promocion: Promocion?,
    productos: List<Producto>,
    categorias: List<String>,
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

    var menuTipoAbierto by remember { mutableStateOf(false) }
    var menuValorAbierto by remember { mutableStateOf(false) }

    // Las opciones dependen del tipo elegido.
    val opcionesValor: List<String> = if (tipo == "CATEGORIA") {
        categorias
    } else {
        productos.map { it.nombre }
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

            // ------------------------------------------------ TIPO
            ExposedDropdownMenuBox(
                expanded = menuTipoAbierto,
                onExpandedChange = { menuTipoAbierto = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                OutlinedTextField(
                    value = if (tipo == "CATEGORIA") "Categoria" else "Producto",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de promocion") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTipoAbierto)
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                ExposedDropdownMenu(
                    expanded = menuTipoAbierto,
                    onDismissRequest = { menuTipoAbierto = false }
                ) {
                    listOf("PRODUCTO" to "Producto", "CATEGORIA" to "Categoria").forEach { (valor, texto) ->
                        DropdownMenuItem(
                            text = { Text(texto) },
                            onClick = {
                                tipo = valor
                                valorReferencia = ""   // la lista de opciones cambio
                                menuTipoAbierto = false
                            }
                        )
                    }
                }
            }

            // ------------------------------------------------ PRODUCTO O CATEGORIA
            ExposedDropdownMenuBox(
                expanded = menuValorAbierto,
                onExpandedChange = { menuValorAbierto = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                OutlinedTextField(
                    value = valorReferencia,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        // Etiquetas distintas a las del desplegable de arriba,
                        // para no confusing "Producto" con "Producto".
                        Text(if (tipo == "CATEGORIA") "Categoria a ofertar" else "Producto a ofertar")
                    },
                    placeholder = {
                        Text(
                            if (opcionesValor.isEmpty()) {
                                "No hay nada para elegir"
                            } else {
                                "Toca para elegir"
                            }
                        )
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuValorAbierto)
                    },
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                ExposedDropdownMenu(
                    expanded = menuValorAbierto,
                    onDismissRequest = { menuValorAbierto = false }
                ) {
                    if (opcionesValor.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (tipo == "CATEGORIA") {
                                        "No hay categorias"
                                    } else {
                                        "No hay productos en la carta"
                                    }
                                )
                            },
                            onClick = { menuValorAbierto = false }
                        )
                    } else {
                        opcionesValor.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = {
                                    valorReferencia = opcion
                                    menuValorAbierto = false
                                }
                            )
                        }
                    }
                }
            }

            // ------------------------------------------------ DESCUENTO
            CafeTextField(
                valor = descuento,
                onValorChange = { descuento = it; onLimpiarError() },
                etiqueta = "Descuento (%)",
                esNumero = true,
                decimal = true,
                marcador = "Ej: 20",
                modifier = Modifier.padding(top = 12.dp)
            )

            // ------------------------------------------------ VIGENCIA
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