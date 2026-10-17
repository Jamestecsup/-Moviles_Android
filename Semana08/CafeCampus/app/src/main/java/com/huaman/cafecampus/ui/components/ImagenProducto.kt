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
import java.text.Normalizer

/**
 * Imagenes de producto.
 *
 * Las fotos viven en `app/src/main/assets/img/productos/` (descargadas de
 * Wikimedia Commons, ver ATRIBUCIONES.md). Se leen por convencion:
 * el nombre del producto se convierte en un "slug" y se busca
 * `assets/img/productos/<slug>.jpg`.
 *
 * ```
 * "Cafe americano"    -> cafe_americano.jpg
 * "Sandwich de pollo" -> sandwich_pollo.jpg
 * "Menu del dia"      -> menu_del_dia.jpg
 * "Empanada"          -> empanada.jpg
 * ```
 *
 * Si el archivo NO existe (por ejemplo, un producto nuevo creado por el
 * administrador desde el formulario), la app cae automaticamente al icono
 * vectorial [ProductoIcono]. Borrar un .jpg no rompe la compilacion.
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
            // quita conectores: "de", "del", "la", "el", "con", "y"
            .replace(Regex("\\b(de|del|la|el|los|las|con|y)\\b"), " ")
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
    }

    /** Nombre del recurso dentro de assets, o null si no hay foto. */
    fun archivoDe(nombre: String): String? {
        val candidato = "$CARPETA/${slug(nombre)}.jpg"
        return candidato
    }

    /** Intenta leer la foto. Devuelve null si no existe. */
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
}

/**
 * Carga la foto del producto una sola vez y la recuerda.
 * La lectura ocurre en Dispatchers.IO porque toca el disco.
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
 * Dibuja la foto del producto dentro de un cuadro redondeado de [tamanio] dp.
 *
 * - Si hay foto en assets -> la muestra.
 * - Si no hay -> muestra [ProductoIcono] con el icono de la columna `icono`.
 *
 * Nunca falla: siempre se ve algo.
 */
@Composable
fun ImagenProducto(
    producto: Producto,
    modifier: Modifier = Modifier,
    tamanio: Int = 56
) {
    val foto = rememberFotoProducto(producto.nombre)

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
    modifier: Modifier = Modifier
) {
    val foto = rememberFotoProducto(producto.nombre)

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