package com.tecsup.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.tecsup.campusgps.model.Posicion
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(
    private val context: Context
) : UbicacionRepository {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    override fun playServicesDisponible(): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        val result = availability.isGooglePlayServicesAvailable(context)
        return result == ConnectionResult.SUCCESS
    }

    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> {
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()

            if (location != null) {
                Result.success(Posicion(location.latitude, location.longitude))
            } else {
                Result.failure(GpsDesactivadoException("No se pudo obtener la ubicación. Activa el GPS e intenta de nuevo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GpsDesactivadoException(mensaje: String) : Exception(mensaje)
