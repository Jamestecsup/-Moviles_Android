package com.huaman.cafecampus.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.huaman.cafecampus.data.local.entity.Producto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.Normalizer

/**
 * Utilidades para imagenes de producto.
 *
 * Las fotos de la galeria se guardan en el almacenamiento privado de la app
 * y se referencia por su ruta absoluta en [Producto.imagenRuta].
 *
 * object ImagenProductos
 *   - slug(nombre): convierte "Cafe americano" -> "cafe_americano"
 *   - archivoDe(nombre): "img/productos/cafe_americano.jpg"
 *   - leer(context, nombre): lee de assets (imagenes genericas de Wikimedia Commons)
 *   - leerArchivo(context, ruta): lee de un archivo guardado en el dispositivo
 *
 * Si no hay archivo de galeria, se usa la imagen de assets o, como ultimo
 * recurso, el icono vectorial [ProductoIcono].
 */
object ImagenProductos {
    const val CARPETA = "img/productos"

    /** Sin tildes, en minuscula, sin conectores, separado por guiones bajos. */
    fun slug(nombre: String): String {
        val sinTildes = Normalizer
            .normalize(nombre, Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
            .lowercase()

        return sinTildes
            // quita conectores: "de", "del", "la", "el", "los", "las", "con", "y"
            .replace(Regex("\\b(de|del|la|el|los|las|con|y)\\b"), " ")
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
    }

    /** Nombre del recurso dentro de assets, o null si no hay foto. */
    fun archivoDe(nombre: String): String? {
        val candidato = "$CARPETA/${slug(nombre)}.jpg"
        return candidato
    }

    /** Intenta leer la foto de assets. Devuelve null si no existe. */
    fun leer(context: Context, nombre: String): ImageBitmap? {
        val ruta = archivoDe(nombre) ?: return null
        return try {
            context.assets.open(ruta).use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (e: java.io.IOException) {
            null // no hay foto para este producto -> se usa el icono
        }
    }

    /** Lee una imagen guardada en el almacenamiento privado de la app. */
    fun leerArchivo(context: Context, ruta: String): ImageBitmap? {
        return try {
            File(ruta).inputStream().use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        } catch (e: Exception) {
            null // archivo no existe o no se puede leer -> se usa el icono
        }
    }
}

/**
 * Carga la foto de assets de un producto una sola vez y la recuerda.
 */
@Composable
fun rememberFotoProducto(nombre: String): ImageBitmap? {
    val context = LocalContext.current
    val clave = ImagenProductos.archivoDe(nombre)
    val estado = produceState<ImageBitmap?>(initialValue = null, key1 = clave) {
        value = if (clave == null) null else withContext(Dispatchers.IO) {
            ImagenProductos.leer(context, nombre)
        }
    }
    return estado.value
}

/**
 * Carga una imagen guardada en el dispositivo una sola vez y la recuerda.
 */
@Composable
fun rememberImagenArchivo(ruta: String): ImageBitmap? {
    val context = LocalContext.current
    val estado = produceState<ImageBitmap?>(initialValue = null, key1 = ruta) {
        value = withContext(Dispatchers.IO) {
            ImagenProductos.leerArchivo(context, ruta)
        }
    }
    return estado.value
}

/**
 * Dibuja la foto del producto dentro de un cuadro redondeado de [tamanio] dp.
 *
 * - Si se recibe [imagenRutaPersonalizada] y existe un archivo, la muestra.
 * - Si no, intenta leer la foto de assets del producto.
 * - Si tampoco existe, muestra [ProductoIcono] con el icono de la columna `icono`.
 *
 * Nunca falla: siempre se ve algo.
 */
@Composable
fun ImagenProducto(
    producto: Producto,
    imagenRutaPersonalizada: String? = null,
    modifier: Modifier = Modifier,
    tamanio: Int = 56
) {
    val ruta = imagenRutaPersonalizada ?: producto.imagenRuta

    val foto = if (ruta != null) {
        rememberImagenArchivo(ruta)
    } else {
        rememberFotoProducto(producto.nombre)
    }

    if (foto != null) {
        Image(
            bitmap = foto,
            contentDescription = producto.nombre,
            modifier = modifier
                .clip(RoundedCornerShape(12.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer),
            contentScale = ContentScale.Crop
        )
    } else {
        ProductoIcono(
            icono = producto.icono,
            modifier = modifier,
            tamanio = tamanio
        )
    }
}

/**
 * Igual que [ImagenProducto] pero ocupa todo el espacio disponible (detalle
 * del producto y vista previa del formulario del administrador).
 */
@Composable
fun ImagenProductoGrande(
    producto: Producto,
    imagenRutaPersonalizada: String? = null,
    modifier: Modifier = Modifier
) {
    val ruta = imagenRutaPersonalizada ?: producto.imagenRuta
    val foto = if (ruta != null) {
        rememberImagenArchivo(ruta)
    } else {
        rememberFotoProducto(producto.nombre)
    }

    Box(modifier = modifier) {
        if (foto != null) {
            Image(
                bitmap = foto,
                contentDescription = producto.nombre,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            ProductoIcono(
                icono = producto.icono,
                modifier = Modifier.fillMaxSize(),
                tamanio = 96
            )
        }
    }
}

/** Altura estandar del area de imagen; se usa en el Carrito tambien. */
val AlturaImagenDetalle = 220.dp