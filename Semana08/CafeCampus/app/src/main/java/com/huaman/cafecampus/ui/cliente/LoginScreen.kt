package com.huaman.cafecampus.ui.cliente

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.huaman.cafecampus.model.Rol
import com.huaman.cafecampus.ui.components.CafeButton
import com.huaman.cafecampus.ui.components.CafeTextField
import com.huaman.cafecampus.viewmodel.SesionViewModel

/**
 * Inicio de sesion (mockup 2 del cliente / mockup 1 del administrador).
 *
 * Es UN SOLO Composable usado por ambos perfiles: lo que cambia es el color
 * (viene del tema segun el rol) y el texto de la tarjeta de "Modo practica".
 *
 * La View no decide nada de negocio: avisa al ViewModel y muestra el error
 * que este le devuelve.
 */
@Composable
fun LoginScreen(
    vm: SesionViewModel,
    modifier: Modifier = Modifier,
    onIngresoExitoso: (Rol) -> Unit = {}
) {
    val rol by vm.rolSeleccionado.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val usuario by vm.usuarioActual.collectAsStateWithLifecycle()

    // El ViewModel confirma el login (y guarda el perfil en DataStore).
    // Cuando llega el Usuario, recien ahi la pantalla navega a su grafo.
    LaunchedEffect(usuario?.id) {
        val u = usuario ?: return@LaunchedEffect
        onIngresoExitoso(u.rolTipado)
    }

    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    val esAdmin = rol == Rol.ADMIN

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {

        // Titulo
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 48.dp)
        )
        Text(
            text = "Ingresa para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 22.dp)
        )

        // Selector de rol
        SelectorRol(
            seleccionado = rol,
            onSeleccion = vm::elegirRol
        )

        // Campos
        CafeTextField(
            valor = correo,
            onValorChange = {
                correo = it
                vm.limpiarError()
            },
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
            modifier = Modifier.padding(top = 20.dp)
        )

        CafeTextField(
            valor = clave,
            onValorChange = {
                clave = it
                vm.limpiarError()
            },
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
            modifier = Modifier.padding(top = 12.dp),
            supporting = {
                if (error != null) {
                    Text(
                        text = error.orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        )

        TextButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 2.dp)
        ) {
            Text(
                text = "Olvidaste tu contrasena?",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Boton del color del rol
        CafeButton(
            texto = "Ingresar",
            onClick = { vm.login(correo, clave, rol) },
            modifier = Modifier.padding(top = 14.dp),
            cargando = cargando
        )

        TextButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            Text(
                text = "No tienes cuenta? Registrate",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Aviso de modo practica (solo administrador, como en el mockup)
        if (esAdmin) {
            TarjetaModoPractica(modifier = Modifier.padding(top = 12.dp))
        }
    }
}

/**
 * Selector segmentado Estudiante / Administrador.
 * El color sale del tema, que ya cambio segun el rol activo.
 */
@Composable
fun SelectorRol(
    seleccionado: Rol,
    onSeleccion: (Rol) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        listOf(Rol.CLIENTE, Rol.ADMIN).forEach { rol ->
            val activo = rol == seleccionado
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (activo) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        }
                    )
                    .height(44.dp)
                    .clickable { onSeleccion(rol) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (rol == Rol.CLIENTE) "Estudiante" else "Administrador",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (activo) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

/** Tarjeta informativa del modo practica (mockup del administrador). */
@Composable
private fun TarjetaModoPractica(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        Text(
            text = "Modo practica",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}