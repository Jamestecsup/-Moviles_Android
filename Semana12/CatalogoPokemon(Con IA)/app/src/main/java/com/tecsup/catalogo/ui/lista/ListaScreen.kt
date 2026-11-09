package com.tecsup.catalogo.ui.lista

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.catalogo.domain.model.Rol
import com.tecsup.catalogo.ui.UiState
import com.tecsup.catalogo.ui.components.Cargando
import com.tecsup.catalogo.ui.components.ErrorView
import com.tecsup.catalogo.ui.components.ItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    vm: ListaViewModel,
    rol: Rol,
    onDetalle: (String) -> Unit,
    onFavoritos: () -> Unit,
    onAdmin: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    val estado by vm.estado.collectAsStateWithLifecycle()
    val aviso by vm.aviso.collectAsStateWithLifecycle()

    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(aviso) {
        aviso?.let {
            snackbar.showSnackbar(it)
            vm.limpiarAviso()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokémon") },
                actions = {
                    IconButton(onClick = vm::refrescar) {
                        Icon(Icons.Default.Refresh, contentDescription = "Sincronizar")
                    }
                    IconButton(onClick = onFavoritos) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
                    }
                    if (rol == Rol.ADMIN) {
                        IconButton(onClick = onAdmin) {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Panel Admin"
                            )
                        }
                    }
                    IconButton(onClick = onCerrarSesion) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        when (val actual = estado) {
            is UiState.Cargando -> Cargando(Modifier.padding(padding))

            is UiState.Error -> ErrorView(
                mensaje = actual.mensaje,
                onReintentar = vm::refrescar,
                modifier = Modifier.padding(padding)
            )

            is UiState.Exito -> {
                if (actual.datos.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CatchingPokemon,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Todavía no hay Pokémon guardados",
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        Text(
                            text = "Pulsa Sincronizar para descargar el catálogo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = vm::refrescar,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Sincronizar")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(actual.datos, key = { it.id }) { item ->
                            ItemCard(
                                item = item,
                                onClick = { onDetalle(item.id) },
                                onAlternarFavorito = { vm.alternarFavorito(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
