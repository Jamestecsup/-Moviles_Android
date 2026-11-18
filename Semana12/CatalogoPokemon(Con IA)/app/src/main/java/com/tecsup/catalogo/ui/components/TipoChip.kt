package com.tecsup.catalogo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.catalogo.ui.theme.coloresDeTipo

/** Etiqueta del tipo de Pokémon pintada con el color de ese tipo. */
@Composable
fun TipoChip(
    tipo: String,
    modifier: Modifier = Modifier
) {
    val colores = coloresDeTipo(tipo)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = colores.fondo,
        contentColor = colores.texto
    ) {
        Text(
            text = tipo,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
