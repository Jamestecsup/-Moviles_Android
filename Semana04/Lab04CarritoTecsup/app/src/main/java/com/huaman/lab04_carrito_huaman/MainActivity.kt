package com.huaman.lab04_carrito_huaman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.huaman.lab04_carrito_huaman.ui.theme.Lab04carritohuamanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab04carritohuamanTheme {
                PantallaCarrito()
                }
            }
        }
    }

