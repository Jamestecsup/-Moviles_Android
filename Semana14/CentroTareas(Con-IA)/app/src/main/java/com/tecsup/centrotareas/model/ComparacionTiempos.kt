package com.tecsup.centrotareas.model

data class ComparacionTiempos(
    val unaPorUna: List<Long> = emptyList(),
    val aLaVez: List<Long> = emptyList()
) {
    val promedioUnaPorUna: Long?
        get() = if (unaPorUna.isEmpty()) null else unaPorUna.average().toLong()

    val promedioALaVez: Long?
        get() = if (aLaVez.isEmpty()) null else aLaVez.average().toLong()

    val ahorroMs: Long?
        get() {
            val lento = promedioUnaPorUna ?: return null
            val rapido = promedioALaVez ?: return null
            return lento - rapido
        }

    fun registrar(enParalelo: Boolean, milisegundos: Long): ComparacionTiempos =
        if (enParalelo) {
            copy(aLaVez = (aLaVez + milisegundos).takeLast(MAX_REGISTROS))
        } else {
            copy(unaPorUna = (unaPorUna + milisegundos).takeLast(MAX_REGISTROS))
        }

    companion object {
        const val MAX_REGISTROS = 5
    }
}
