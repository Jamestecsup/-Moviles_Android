package com.tecsup.mibodega.ui.cliente.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.mibodega.ui.componentes.BotonPrimario
import com.tecsup.mibodega.ui.componentes.BotonSecundario
import com.tecsup.mibodega.ui.componentes.CampoTexto
import com.tecsup.mibodega.ui.theme.BodegaTheme

private const val USUARIO_OK = "987654321"
private const val CLAVE_OK = "tecsup123"

/**
 * Pantalla: Iniciar sesión (mockup "Cliente").
 * Valida contra el usuario fijo; si no coincide muestra el error.
 */
@Composable
fun LoginScreen(
    onVolver: () -> Unit,
    onIngresar: (telefono: String) -> Unit,
    onCrearCuenta: () -> Unit
) {
    var telefono by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Ingresa con tu número y contraseña",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(28.dp))

        CampoTexto(
            etiqueta = "Teléfono",
            valor = telefono,
            onValorCambia = {
                telefono = it
                error = null
            },
            placeholder = "987 654 321",
            teclado = KeyboardType.Phone
        )

        Spacer(Modifier.height(16.dp))

        CampoTexto(
            etiqueta = "Contraseña",
            valor = clave,
            onValorCambia = {
                clave = it
                error = null
            },
            placeholder = "Tu contraseña",
            teclado = KeyboardType.Password,
            esContrasena = true
        )

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = error ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(28.dp))

        BotonPrimario(
            texto = "Ingresar",
            onClick = {
                if (telefono.filter { it.isDigit() } == USUARIO_OK && clave == CLAVE_OK) {
                    onIngresar(telefono)
                } else {
                    error = "Usuario o contraseña incorrectos"
                }
            }
        )

        Spacer(Modifier.height(12.dp))

        BotonSecundario(
            texto = "Crear cuenta",
            onClick = onCrearCuenta
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginPreview() {
    BodegaTheme {
        LoginScreen(onVolver = {}, onIngresar = {}, onCrearCuenta = {})
    }
}
