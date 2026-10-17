package com.huaman.cafecampus.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
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
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.entity.Producto
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.CafeButton
import com.huaman.cafecampus.ui.components.CafeButtonSecundario
import com.huaman.cafecampus.ui.components.CafeTextField
import com.huaman.cafecampus.ui.components.ImagenProducto
import com.huaman.cafecampus.ui.theme.EstadoPreparandoContenedor

/**
 * Nuevo / editar producto (mockup 6).
 *
 * Sirve para los dos casos: si recibe un producto con id != 0 edita ese, si no
 * crea uno nuevo. El ViewModel valida que el nombre no este vacio y que el
 * precio sea mayor que 0.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioProductoScreen(
    producto: Producto?,
    categorias: List<String>,
    iconos: List<String>,
    error: String?,
    onGuardar: (
        id: Int,
        nombre: String,
        descripcion: String,
        categoria: String,
        precio: String,
        icono: String,
        disponible: Boolean
    ) -> Unit,
    modifier: Modifier = Modifier,
    onCancelar: () -> Unit = {},
    onAtras: () -> Unit = {},
    onLimpiarError: () -> Unit = {}
) {
    var nombre by remember(producto?.id) { mutableStateOf(producto?.nombre ?: "") }
    var descripcion by remember(producto?.id) { mutableStateOf(producto?.descripcion ?: "") }
    var categoria by remember(producto?.id) {
        mutableStateOf(producto?.categoria ?: categorias.firstOrNull().orEmpty())
    }
    var precio by remember(producto?.id) {
        mutableStateOf(producto?.precio?.let { formatearPrecio(it) } ?: "")
    }
    var icono by remember(producto?.id) { mutableStateOf(producto?.icono ?: "plato") }
    var disponible by remember(producto?.id) { mutableStateOf(producto?.disponible ?: true) }

    var menuCategoriasAbierto by remember { mutableStateOf(false) }

    // Producto de prueba para poder previsualizar la foto / icono mientras se
    // escribe. Si ya se esta editando, se usa el original.
    val vistaPrevia = remember(producto, nombre, icono) {
        producto?.copy(nombre = nombre.ifBlank { producto.nombre }, icono = icono)
            ?: Producto(
                nombre = nombre.ifBlank { "Nuevo producto" },
                descripcion = descripcion,
                categoria = categoria,
                precio = precio.toDoubleOrNull() ?: 0.0,
                disponible = disponible,
                icono = icono
            )
    }

    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(
            titulo = if (producto == null) "Nuevo producto" else "Editar producto",
            onAtras = onAtras
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // ------------------------------------------------ imagen / icono
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EstadoPreparandoContenedor)
                    .clickable {
                        // Tocar la imagen cambia de icono, como el
                        // "Toca para elegir color" del mockup.
                        val i = iconos.indexOf(icono)
                        icono = iconos[(i + 1).coerceAtLeast(0) % iconos.size]
                    },
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImagenProducto(
                        producto = vistaPrevia,
                        modifier = Modifier.size(72.dp),
                        tamanio = 36
                    )
                    Column(modifier = Modifier.padding(start = 14.dp)) {
                        Text(
                            text = "Imagen del producto",
                            style = MaterialTheme.typography.titleSmall,
                            color = com.huaman.cafecampus.ui.theme.EstadoPreparando
                        )
                        Text(
                            text = "Toca para elegir icono",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ------------------------------------------------ campos
            CafeTextField(
                valor = nombre,
                onValorChange = {
                    nombre = it
                    onLimpiarError()
                },
                etiqueta = "Nombre de producto",
                modifier = Modifier.padding(top = 16.dp)
            )

            // Categoria (desplegable)
            ExposedDropdownMenuBox(
                expanded = menuCategoriasAbierto,
                onExpandedChange = { menuCategoriasAbierto = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoria") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuCategoriasAbierto)
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
                    expanded = menuCategoriasAbierto,
                    onDismissRequest = { menuCategoriasAbierto = false }
                ) {
                    categorias.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                categoria = opcion
                                menuCategoriasAbierto = false
                            }
                        )
                    }
                }
            }

            CafeTextField(
                valor = precio,
                onValorChange = {
                    precio = it
                    onLimpiarError()
                },
                etiqueta = "Precio (S/)",
                esNumero = true,
                decimal = true,
                modifier = Modifier.padding(top = 12.dp)
            )

            CafeTextField(
                valor = descripcion,
                onValorChange = { descripcion = it },
                etiqueta = "Descripcion",
                marcador = "Que incluye este producto",
                multilinea = true,
                minLineas = 3,
                modifier = Modifier.padding(top = 12.dp)
            )

            // ------------------------------------------------ disponible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Disponible en el menu",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = if (disponible) {
                            "Los estudiantes pueden pedirlo"
                        } else {
                            "Agotado: no se puede pedir"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = disponible,
                    onCheckedChange = { disponible = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Error de validacion
            if (error != null) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            CafeButton(
                texto = "Guardar producto",
                onClick = {
                    onGuardar(
                        producto?.id ?: 0,
                        nombre,
                        descripcion,
                        categoria,
                        precio,
                        icono,
                        disponible
                    )
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

/** "4.50" en vez de "4.5". */
private fun formatearPrecio(valor: Double): String =
    if (valor % 1.0 == 0.0) valor.toInt().toString() else valor.toString()