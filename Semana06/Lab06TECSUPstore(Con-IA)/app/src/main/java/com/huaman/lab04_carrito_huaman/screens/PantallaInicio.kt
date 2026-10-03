package com.huaman.lab04_carrito_huaman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huaman.lab04_carrito_huaman.components.TarjetaProducto
import com.huaman.lab04_carrito_huaman.model.Producto

@Composable
fun PantallaInicio(
    onAgregarAlCarrito: (Producto) -> Unit = {},
    onFavoritoClick: (Producto) -> Unit = {}
){
    var busqueda by remember { mutableStateOf("") }
    val productosDestacados = remember{
        listOf(
            Producto("Audifonos", 89.00, 1),
            Producto("Smartwatch", 199.00, 1),
            Producto("Funda celular", 25.00, 1),
            Producto("Laptop HP Pavilion 15", 3499.00, 1),
            Producto("Teclado Mecánico", 199.00, 1)
        )
    }
    val productosFiltrados = productosDestacados.filter{
        it.nombre.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ){
            Column(
                modifier = Modifier.padding(16.dp)
            ){
                Text(
                    text = "TECSUP Store",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mas vendidos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar productos...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ){
            items(productosFiltrados){ producto ->
                TarjetaProducto(
                    producto = producto,
                    onFavoritoClick = { onFavoritoClick(producto) },
                    onCompartirClick = {},
                    onReportarClick = {}
                )
            }
        }
    }
}
