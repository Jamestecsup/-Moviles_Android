package com.huaman.lab04_carrito_huaman.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.huaman.lab04_carrito_huaman.model.Producto
import kotlinx.coroutines.launch

enum class DestinoNavegacion(
    val titulo: String,
    val iconoSeleccionado: ImageVector,
    val iconoNoSeleccionado: ImageVector
){
    INICIO("Inicio / Catálogo", Icons.Filled.Home, Icons.Outlined.Home),
    CARRITO("Carrito de Compras", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
    PERFIL("Mi Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var destinoActual by remember { mutableStateOf(DestinoNavegacion.INICIO) }
    val productosCarrito = remember { mutableStateListOf<Producto>() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet{
                Text(
                    text = "TECSUP Store",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                DestinoNavegacion.entries.forEach{ destino ->
                    NavigationDrawerItem(
                        label = {
                            Text(text = destino.titulo)
                        },
                        selected = destinoActual == destino,
                        onClick = {
                            destinoActual = destino
                            scope.launch{
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if(destinoActual == destino) destino.iconoSeleccionado else destino.iconoNoSeleccionado,
                                contentDescription = destino.titulo
                            )
                        },
                        badge = {
                            if(destino == DestinoNavegacion.CARRITO && productosCarrito.isNotEmpty()){
                                Badge{
                                    Text("${productosCarrito.size}")
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = destinoActual.titulo)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch{
                                    if(drawerState.isClosed) drawerState.open() else drawerState.close()
                                }
                            }
                        ){
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menu de navegacion"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ){ paddingValores ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValores)
            ){
                Text(
                    text = destinoActual.titulo,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
