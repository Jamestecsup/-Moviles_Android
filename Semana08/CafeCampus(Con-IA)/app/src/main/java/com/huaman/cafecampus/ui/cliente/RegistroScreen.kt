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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.data.local.entity.Usuario
import com.huaman.cafecampus.ui.components.AppTopBar
import com.huaman.cafecampus.ui.components.CafeButton
import com.huaman.cafecampus.ui.components.CafeButtonSecundario
import com.huaman.cafecampus.ui.components.CafeTextField
import com.huaman.cafecampus.viewmodel.SesionViewModel

/**
 * Registro de estudiante (Fase 2, mejora 2).
 *
 * Permite crear una cuenta nueva. El correo es UNIQUE: si ya existe, la
 * base de datos lo rechaza y se avisa en pantalla.
 */
@Composable
fun RegistroScreen(
    vm: SesionViewModel,
    modifier: Modifier = Modifier,
    onRegistrado: (Usuario) -> Unit = {},
    onVolverAlLogin: () -> Unit = {}
) {
    val error by vm.registroError.collectAsStateWithLifecycle()
    val mensajeError = error

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var claveConfirmada by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize()) {

        AppTopBar(titulo = "Crear cuenta", onAtras = onVolverAlLogin)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            // Encabezado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Text(
                    text = "CafeCampus",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Crea tu cuenta de estudiante para pedir y seguir tus pedidos.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            CafeTextField(
                valor = nombre,
                onValorChange = { nombre = it; vm.limpiarRegistroError() },
                etiqueta = "Nombre completo",
                marcador = "Juan Perez",
                icono = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.padding(top = 20.dp)
            )

            CafeTextField(
                valor = correo,
                onValorChange = { correo = it; vm.limpiarRegistroError() },
                etiqueta = "Correo",
                marcador = "juan@campus.test",
                icono = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.padding(top = 12.dp)
            )

            CafeTextField(
                valor = clave,
                onValorChange = { clave = it; vm.limpiarRegistroError() },
                etiqueta = "Contrasena",
                esContrasena = true,
                icono = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.padding(top = 12.dp)
            )

            CafeTextField(
                valor = claveConfirmada,
                onValorChange = { claveConfirmada = it; vm.limpiarRegistroError() },
                etiqueta = "Repetir contrasena",
                esContrasena = true,
                icono = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.padding(top = 12.dp)
            )

            if (mensajeError != null) {
                Text(
                    text = mensajeError,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            CafeButton(
                texto = "Crear cuenta",
                onClick = {
                    vm.registrar(nombre, correo, clave, claveConfirmada) { nuevo ->
                        onRegistrado(nuevo)
                    }
                },
                modifier = Modifier.padding(top = 20.dp)
            )

            CafeButtonSecundario(
                texto = "Ya tengo cuenta",
                onClick = onVolverAlLogin,
                modifier = Modifier.padding(top = 10.dp, bottom = 24.dp)
            )
        }
    }
}
