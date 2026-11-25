package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

@Composable
fun PantallaLista(
    viewModel: PostsViewModel,
    onPostClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val estado by viewModel.lista.collectAsStateWithLifecycle()
    val usuarioFiltro by viewModel.usuarioFiltro.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Fila de chips: Todos, Usuario 1 a Usuario 10
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            item {
                FilterChip(
                    selected = usuarioFiltro == null,
                    onClick = { viewModel.filtrar(null) },
                    label = {
                        Text(
                            text = "Todos",
                            fontWeight = if (usuarioFiltro == null) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            items((1..10).toList()) { userId ->
                val seleccionado = usuarioFiltro == userId
                FilterChip(
                    selected = seleccionado,
                    onClick = { viewModel.filtrar(userId) },
                    label = {
                        Text(
                            text = "Usuario $userId",
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (val actual = estado) {
                is UiState.Cargando -> {
                    Cargando()
                }
                is UiState.Error -> {
                    ErrorConReintento(
                        mensaje = actual.mensaje,
                        onReintentar = { viewModel.cargar() }
                    )
                }
                is UiState.Exito -> {
                    if (actual.datos.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin publicaciones",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(actual.datos, key = { it.id }) { post ->
                                TarjetaPost(
                                    post = post,
                                    onClick = { onPostClick(post.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
