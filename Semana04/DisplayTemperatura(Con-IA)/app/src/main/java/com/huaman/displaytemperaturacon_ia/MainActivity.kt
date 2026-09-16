package com.huaman.displaytemperaturacon_ia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.huaman.displaytemperaturacon_ia.ui.theme.DisplayTemperaturaConIATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DisplayTemperaturaConIATheme {
                TemperatureDisplay()
            }
        }
    }
}

@Composable
fun TemperatureDisplay() {
    var temperatura by remember { mutableStateOf(20) }
    val control: TemperatureControl = remember { TemperatureNormal() }

    Column {
        Text(
            text = "Temperatura: $temperatura °C"
        )

        Button(
            onClick = {
                control.subir()
                temperatura = control.obtenerTemperatura()
            }
        ) {
            Text("Subir")
        }

        Button(
            onClick = {
                control.bajar()
                temperatura = control.obtenerTemperatura()
            }
        ) {
            Text("Bajar")
        }

        Button(
            onClick = {
                control.resetear()
                temperatura = control.obtenerTemperatura()
            }
        ) {
            Text("Resetear")
        }
    }
}
fun probarPolimorfismo() {
    val control: TemperatureControl = TemperatureNormal()

    control.subir()

    println(control.obtenerTemperatura())
}