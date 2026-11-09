package com.huaman.lab04_carrito_huaman.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huaman.lab04_carrito_huaman.components.TarjetaProducto
import com.huaman.lab04_carrito_huaman.model.Producto

@Composable
fun PantallaCarrito(
    productos: SnapshotStateList<Producto> = remember { mutableStateListOf() },
    onFavoritoClick: (Producto) -> Unit = {}
){
    var productoAEliminar by remember { mutableStateOf<Producto?>(null) }
    var mostrarCompraExitosa by remember { mutableStateOf(false) }
    val subtotal = productos.sumOf { it.precio * it.cantidad }
    val igv = subtotal * 0.18
    val total = subtotal + igv
    val descuentoPorcentaje = when{
        total > 5000 -> 0.10
        total > 3000 -> 0.05
        else -> 0.0
    }
    val descuento = total * descuentoPorcentaje
    val totalFinal = total - descuento
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ){
        if(productos.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "No hay productos",
                        color = Color.Gray
                    )
                    Text(
                        text = "Agrega productos",
                        color = Color.Gray
                    )
                }
            }
        }else{
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                items(productos){ producto ->
                    TarjetaProducto(
                        producto = producto,
                        onFavoritoClick = { onFavoritoClick(producto) },
                        onAumentar = {
                            val index = productos.indexOf(producto)
                            if(index != -1){
                                productos[index] = producto.copy(cantidad = producto.cantidad + 1)
                            }
                        },
                        onDisminuir = {
                            val index = productos.indexOf(producto)
                            if(index != -1 && producto.cantidad > 1){
                                productos[index] = producto.copy(cantidad = producto.cantidad - 1)
                            }
                        },
                        onEliminar = { productoAEliminar = producto }
                    )
                }
            }
        }
        Button(
            onClick = { mostrarCompraExitosa = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = productos.isNotEmpty()
        ){
            Text("Finalizar compra")
        }
        Text("Productos: ${productos.size}")
        Column(
            modifier = Modifier.fillMaxWidth()
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("Subtotal")
                Text("S/ %.2f".format(subtotal))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text("IGV (18%)")
                Text("S/ %.2f".format(igv))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Text(
                    text = "TOTAL",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "S/ %.2f".format(total),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            if(descuentoPorcentaje > 0){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text("Descuento")
                    Text(
                        text = "- S/ %.2f".format(descuento)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = "TOTAL FINAL",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ %.2f".format(totalFinal),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    if(productoAEliminar != null){
        AlertDialog(
            onDismissRequest = { productoAEliminar = null },
            title = {
                Text("¿Eliminar este producto?")
            },
            text = {
                Text("¿Deseas eliminar ${productoAEliminar!!.nombre}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        productos.remove(productoAEliminar)
                        productoAEliminar = null
                    }
                ){
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        productoAEliminar = null
                    }
                ){
                    Text("Cancelar")
                }
            }
        )
    }
    if(mostrarCompraExitosa){
        AlertDialog(
            onDismissRequest = { mostrarCompraExitosa = false },
            title = {
                Text("Compra exitosa")
            },
            text = {
                Text("Tu pedido fue registrado. Gracias por comprar en TECSUP Store.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        productos.clear()
                        mostrarCompraExitosa = false
                    }
                ){
                    Text("Aceptar")
                }
            }
        )
    }
}
