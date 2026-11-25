package com.tecsup.murocampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.murocampus.data.PostRepositoryRemote
import com.tecsup.murocampus.data.remote.RetrofitClient
import com.tecsup.murocampus.ui.PantallaCrear
import com.tecsup.murocampus.ui.PantallaDetalle
import com.tecsup.murocampus.ui.PantallaLista
import com.tecsup.murocampus.ui.theme.MuroCampusTheme
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.PostsViewModelFactory

enum class PantallaActual {
    LISTA,
    DETALLE,
    CREAR
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = PostRepositoryRemote(RetrofitClient.api)
            val viewModel: PostsViewModel = viewModel(
                factory = PostsViewModelFactory(repository)
            )

            var pantalla by rememberSaveable { mutableStateOf(PantallaActual.LISTA) }
            var idSeleccionado by rememberSaveable { mutableStateOf<Int?>(null) }

            MuroCampusTheme {
                when (pantalla) {
                    PantallaActual.LISTA -> {
                        Scaffold(
                            topBar = {
                                TopAppBar(
                                    title = { Text("MuroCampus") },
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            },
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = {
                                        viewModel.limpiarCrearEstado()
                                        pantalla = PantallaActual.CREAR
                                    },
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Crear publicación"
                                    )
                                }
                            }
                        ) { innerPadding ->
                            PantallaLista(
                                viewModel = viewModel,
                                onPostClick = { id ->
                                    idSeleccionado = id
                                    viewModel.abrir(id)
                                    pantalla = PantallaActual.DETALLE
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            )
                        }
                    }

                    PantallaActual.DETALLE -> {
                        PantallaDetalle(
                            id = idSeleccionado ?: 1,
                            viewModel = viewModel,
                            onVolver = {
                                pantalla = PantallaActual.LISTA
                            }
                        )
                    }

                    PantallaActual.CREAR -> {
                        PantallaCrear(
                            viewModel = viewModel,
                            onVolver = {
                                pantalla = PantallaActual.LISTA
                            }
                        )
                    }
                }
            }
        }
    }
}
