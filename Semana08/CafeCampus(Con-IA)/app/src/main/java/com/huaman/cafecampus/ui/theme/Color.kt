package com.huaman.cafecampus.ui.theme

import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Paleta del cliente (naranja)
// ---------------------------------------------------------------------------
val NaranjaPrimario = Color(0xFFEA580C)
val NaranjaContenedor = Color(0xFFFFEDD5)
val NaranjaFondo = Color(0xFFFFFBF7)

// ---------------------------------------------------------------------------
// Paleta del administrador (indigo)
// ---------------------------------------------------------------------------
val IndigoPrimario = Color(0xFF4F46E5)
val IndigoContenedor = Color(0xFFE0E7FF)
val IndigoFondo = Color(0xFFF7F7FD)

// ---------------------------------------------------------------------------
// Estados de pedido (comunes a los dos perfiles)
//
//   Recibido  azul   |  Preparando ambar
//   Listo     verde  |  Entregado gris  |  Cancelado rojo
//
// El admin y el cliente usan EXACTAMENTE los mismos colores de estado.
// ---------------------------------------------------------------------------
val EstadoRecibido = Color(0xFF2563EB)
val EstadoRecibidoContenedor = Color(0xFFDBEAFE)
val EstadoPreparando = Color(0xFFD97706)
val EstadoPreparandoContenedor = Color(0xFFFEF3C7)
val EstadoListo = Color(0xFF16A34A)
val EstadoListoContenedor = Color(0xFFDCFCE7)
val EstadoEntregado = Color(0xFF6B7280)
val EstadoEntregadoContenedor = Color(0xFFF3F4F6)
val EstadoCancelado = Color(0xFFDC2626)
val EstadoCanceladoContenedor = Color(0xFFFEE2E2)

// Modo oscuro
val OscuroPrimario = Color(0xFFFFB68A)
val OscuroPrimarioContenedor = Color(0xFF7C2D12)
val OscuroFondo = Color(0xFF1A1614)
val OscuroSuperficie = Color(0xFF221D1A)