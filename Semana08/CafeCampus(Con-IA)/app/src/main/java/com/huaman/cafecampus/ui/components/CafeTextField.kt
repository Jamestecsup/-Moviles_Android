package com.huaman.cafecampus.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Campo de texto del diseno: etiqueta pequena arriba, contenedor redondeado y
 * icono opcional a la izquierda.
 */
@Composable
fun CafeTextField(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    marcador: String? = null,
    icono: (@Composable () -> Unit)? = null,
    esContrasena: Boolean = false,
    esNumero: Boolean = false,
    decimal: Boolean = false,
    multilinea: Boolean = false,
    minLineas: Int = 1,
    habilitado: Boolean = true,
    supporting: (@Composable () -> Unit)? = null
) {
    val teclado = when {
        esContrasena -> KeyboardType.Password
        esNumero && decimal -> KeyboardType.Decimal
        esNumero -> KeyboardType.Number
        else -> KeyboardType.Text
    }

    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier.fillMaxWidth(),
        enabled = habilitado,
        label = {
            Text(text = etiqueta, style = MaterialTheme.typography.labelMedium)
        },
        placeholder = marcador?.let {
            { Text(text = it, style = MaterialTheme.typography.bodyMedium) }
        },
        leadingIcon = icono,
        singleLine = !multilinea,
        minLines = minLineas,
        shape = MaterialTheme.shapes.small,
        visualTransformation = if (esContrasena) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = teclado,
            imeAction = if (multilinea) ImeAction.Default else ImeAction.Next
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        supportingText = supporting?.let { slot -> { slot() } }
    )
}