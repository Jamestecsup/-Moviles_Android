package com.tecsup.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.tecsup.campusgps.R
import com.tecsup.campusgps.model.UbicacionUi
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(14.5)
            controller.setCenter(GeoPoint(-12.0464, -77.0428))
        }
    }

    val iconUsuario = remember(context) {
        ContextCompat.getDrawable(context, R.drawable.ic_marker_usuario)
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    LaunchedEffect(estado) {
        mapView.overlays.clear()
        if (estado is UbicacionUi.Lista) {
            val userPos = GeoPoint(estado.posicion.latitud, estado.posicion.longitud)
            val markerUsuario = Marker(mapView).apply {
                position = userPos
                title = "Estás aquí"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = iconUsuario
            }
            mapView.overlays.add(markerUsuario)
            mapView.controller.animateTo(userPos, 16.0, 1000L)
        }
        mapView.invalidate()
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
