package com.tecsup.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.campusgps.ui.PantallaMapa
import com.tecsup.campusgps.ui.theme.CampusGPSTheme
import com.tecsup.campusgps.viewmodel.CampusViewModelFactory
import com.tecsup.campusgps.viewmodel.MapaViewModel
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = packageName
        enableEdgeToEdge()
        setContent {
            CampusGPSTheme {
                val factory = CampusViewModelFactory(this)
                val viewModel: MapaViewModel = viewModel(factory = factory)
                PantallaMapa(vm = viewModel)
            }
        }
    }
}
