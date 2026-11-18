package com.tecsup.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.centrotareas.ui.PantallaTareas
import com.tecsup.centrotareas.ui.theme.CentroTareasTheme
import com.tecsup.centrotareas.viewmodel.TareasViewModel
import com.tecsup.centrotareas.viewmodel.TareasViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentroTareasTheme {
                val factory = TareasViewModelFactory()
                val viewModel: TareasViewModel = viewModel(factory = factory)
                PantallaTareas(vm = viewModel)
            }
        }
    }
}
