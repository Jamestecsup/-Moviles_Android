package com.huaman.displaytemperaturacon_ia

open class TemperatureControl {

    protected var temperatura = 20

    open fun subir() {
        temperatura++
    }

    open fun bajar() {
        temperatura--
    }

    open fun resetear() {
        temperatura = 20
    }

    fun obtenerTemperatura(): Int {
        return temperatura
    }
}

class TemperatureNormal : TemperatureControl() {

    override fun subir() {
        temperatura++
    }

    override fun bajar() {
        temperatura--
    }

    override fun resetear() {
        temperatura = 20
    }
}