package com.huaman.lab04_carrito_huaman.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    FAVORITOS("Favoritos", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
    PERFIL("Mi Perfil", Icons.Filled.Person, Icons.Outlined.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(){
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var destinoActual by remember { mutableStateOf(DestinoNavegacion.CARRITO) }
    val productosCarrito = remember { mutableStateListOf<Producto>() }
    val productosFavoritos = remember { mutableStateListOf<Producto>() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(20.dp)
                ){
                    Column{
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ){
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Avatar",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Estudiante TECSUP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "estudiante@tecsup.edu.pe",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                DestinoNavegacion.entries.forEach{ destino ->
                    val esSeleccionado = destinoActual == destino
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = destino.titulo,
                                fontWeight = if(esSeleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = esSeleccionado,
                        onClick = {
                            destinoActual = destino
                            scope.launch{
                                drawerState.close()
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if(esSeleccionado) destino.iconoSeleccionado else destino.iconoNoSeleccionado,
                                contentDescription = destino.titulo
                            )
                        },
                        badge = {
                            if(destino == DestinoNavegacion.CARRITO && productosCarrito.isNotEmpty()){
                                Badge{
                                    Text("${productosCarrito.size}")
                                }
                            }
                            if(destino == DestinoNavegacion.FAVORITOS && productosFavoritos.isNotEmpty()){
                                Badge{
                                    Text("${productosFavoritos.size}")
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
                        Text(
                            text = destinoActual.titulo,
                            fontWeight = FontWeight.Bold
                        )
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
                                contentDescription = "Abrir menú de navegación."
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
                when(destinoActual){
                    DestinoNavegacion.INICIO -> {
                        PantallaInicio(
                            onAgregarAlCarrito = { nuevoProducto ->
                                val index = productosCarrito.indexOfFirst{ it.nombre == nuevoProducto.nombre }
                                if(index != -1){
                                    val existente = productosCarrito[index]
                                    productosCarrito[index] = existente.copy(cantidad = existente.cantidad + 1)
                                }else{
                                    productosCarrito.add(nuevoProducto)
                                }
                            },
                            onFavoritoClick = { producto ->
                                if(productosFavoritos.none{ it.nombre == producto.nombre }){
                                    productosFavoritos.add(producto)
                                }
                            }
                        )
                    }
                    DestinoNavegacion.CARRITO -> {
                        PantallaCarrito(
                            productos = productosCarrito,
                            onFavoritoClick = { producto ->
                                if(productosFavoritos.none{ it.nombre == producto.nombre }){
                                    productosFavoritos.add(producto)
                                }
                            }
                        )
                    }
                    DestinoNavegacion.FAVORITOS -> {
                        PantallaFavoritos(
                            favoritos = productosFavoritos
                        )
                    }
                    DestinoNavegacion.PERFIL -> {
                        PantallaPerfil()
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaFavoritos(
    favoritos: List<Producto>
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Text(
            text = "Mis Favoritos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        if(favoritos.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ){
                Text("No tienes favoritos", color = Color.Gray)
            }
        }else{
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                items(favoritos){ producto ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column(modifier = Modifier.weight(1f)){
                                Text(
                                    text = producto.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "S/ %.2f".format(producto.precio),
                                    color = Color.Gray
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Favorito",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}
