package com.huaman.clinicasaludplus.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huaman.clinicasaludplus.ui.theme.PurpuraPrimario

@Composable
fun AppDrawer(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(280.dp),
        drawerContainerColor = Color.White
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PurpuraPrimario)
                .padding(16.dp)
        ) {
            Text(
                text = "Juan Pérez",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Paciente",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Menu Items
        DrawerMenuItem(
            label = "Inicio",
            icon = Icons.Default.Home,
            isSelected = currentScreen == "inicio",
            onClick = { onNavigate("inicio") }
        )

        DrawerMenuItem(
            label = "Mis citas",
            icon = Icons.Default.Person,
            isSelected = currentScreen == "mis_citas",
            onClick = { onNavigate("mis_citas") }
        )

        DrawerMenuItem(
            label = "Historial médico",
            icon = Icons.Default.History,
            isSelected = currentScreen == "historial",
            onClick = { onNavigate("historial") }
        )
    }
}

@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        Color(0xFFF3E8FF) // Lavanda clara
    } else {
        Color.White
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PurpuraPrimario,
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = label,
                color = if (isSelected) PurpuraPrimario else Color.Black,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}
