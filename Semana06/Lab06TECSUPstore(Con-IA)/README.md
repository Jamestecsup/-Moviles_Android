# Lab04 Carrito Tecsup

## Nombre

James Huaman Quispe

## Descripción

Aplicación de carrito de compras desarrollada con Kotlin y Jetpack Compose.
La aplicación permite ingresar productos con su nombre, precio y cantidad. Los productos se muestran en una lista mediante LazyColumn y cada producto puede ser eliminado.
También se calcula automáticamente el subtotal, el IGV del 18% y el total de la compra.

## Captura 1: Estado vacío

<img width="542" height="1202" alt="image" src="https://github.com/user-attachments/assets/d71f5039-060e-4173-bd63-a1baa088bc54" />


## Captura 2: Carrito con productos

<img width="565" height="1227" alt="image" src="https://github.com/user-attachments/assets/bfa6c20b-1748-49b5-9531-b25d854c6531" />

### a) ¿Por qué mutableStateListOf y no una MutableList normal?

Porque mutableStateListOf es una lista observable por Jetpack Compose. Cuando agregamos o eliminamos elementos, Compose detecta el cambio y actualiza la interfaz.
Una MutableList normal no notifica automáticamente a Compose cuando cambia, por lo que la interfaz podría no actualizarse correctamente.

### b) ¿Por qué la lista es val?

Porque val impide cambiar la referencia de la lista, pero no impide modificar su contenido.
Por eso podemos hacer:
productos.add(...)
productos.remove(...)
La variable productos sigue apuntando a la misma lista, pero podemos agregar o eliminar elementos de ella.

### c) ¿Qué hace weight(1f) en la LazyColumn?

Hace que la LazyColumn ocupe el espacio disponible que queda dentro del Column después de los otros elementos.
En este caso permite que la lista ocupe el espacio restante y deja espacio para el panel de totales que está debajo.
y ya que son 2 textos, el espacio se comparte de manera equitativa.
