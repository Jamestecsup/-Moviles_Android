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
import com.tecsup.campusgps.model.LugarConDistancia
import com.tecsup.campusgps.model.UbicacionUi
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.ScaleBarOverlay

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercanoId: Int? = null,
    radioCercania: Float? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            // Mejora 3: Controles nativos en pantalla
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
            controller.setZoom(14.5)
            controller.setCenter(GeoPoint(-12.0464, -77.0428))

            val scaleBar = ScaleBarOverlay(this).apply {
                setCentred(false)
                setScaleBarOffset(20, 20)
            }
            overlays.add(scaleBar)
        }
    }

    val iconUsuario = remember(context) {
        ContextCompat.getDrawable(context, R.drawable.ic_marker_usuario)
    }
    val iconLugar = remember(context) {
        ContextCompat.getDrawable(context, R.drawable.ic_marker_lugar)
    }
    val iconCercano = remember(context) {
        ContextCompat.getDrawable(context, R.drawable.ic_marker_cercano)
    }

    DisposableEffect(Unit) {
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDetach()
        }
    }

    LaunchedEffect(estado, lugares, masCercanoId, radioCercania) {
        // Mantener la barra de escala y limpiar los demás
        val scaleBar = mapView.overlays.filterIsInstance<ScaleBarOverlay>().firstOrNull()
        mapView.overlays.clear()
        if (scaleBar != null) {
            mapView.overlays.add(scaleBar)
        }

        // Marcador de usuario y círculo de radio de cercanía
        if (estado is UbicacionUi.Lista) {
            val userPos = GeoPoint(estado.posicion.latitud, estado.posicion.longitud)

            // Mejora 2: Polígono circular de radio en OpenStreetMap
            if (radioCercania != null) {
                val circle = Polygon(mapView).apply {
                    points = Polygon.pointsAsCircle(userPos, radioCercania.toDouble())
                    fillPaint.color = android.graphics.Color.argb(45, 21, 101, 192)
                    outlinePaint.color = android.graphics.Color.rgb(21, 101, 192)
                    outlinePaint.strokeWidth = 3f
                }
                mapView.overlays.add(circle)
            }

            val markerUsuario = Marker(mapView).apply {
                position = userPos
                title = "Estás aquí"
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = iconUsuario
            }
            mapView.overlays.add(markerUsuario)
        }

        // Marcadores de los lugares del campus
        for (item in lugares) {
            val esMasCercano = item.lugar.id == masCercanoId
            val markerLugar = Marker(mapView).apply {
                position = GeoPoint(item.lugar.latitud, item.lugar.longitud)
                title = item.lugar.nombre
                snippet = item.lugar.categoria
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = if (esMasCercano) iconCercano else iconLugar
            }
            mapView.overlays.add(markerLugar)
        }

        mapView.invalidate()
    }

    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            val userPos = GeoPoint(estado.posicion.latitud, estado.posicion.longitud)
            mapView.controller.animateTo(userPos, 16.0, 1000L)
        }
    }

    LaunchedEffect(seleccionado) {
        if (seleccionado != null) {
            val lugar = lugares.find { it.lugar.id == seleccionado }?.lugar
            if (lugar != null) {
                val destino = GeoPoint(lugar.latitud, lugar.longitud)
                mapView.controller.animateTo(destino, 17.5, 1000L)
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
