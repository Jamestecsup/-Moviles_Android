package com.tecsup.avisoscampus.notificacion

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.avisoscampus.MainActivity
import com.tecsup.avisoscampus.R
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen

object Notificador {
    private const val CANAL_AVISOS = "avisos"
    private const val CANAL_URGENTES = "urgentes"

    fun crearCanales(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val avisos = NotificationChannel(CANAL_AVISOS, "Avisos", NotificationManager.IMPORTANCE_DEFAULT)
            val urgentes = NotificationChannel(CANAL_URGENTES, "Urgentes", NotificationManager.IMPORTANCE_HIGH)
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannels(listOf(avisos, urgentes))
        }
    }

    fun tienePermiso(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun mostrar(context: Context, aviso: Aviso): Boolean {
        if (!tienePermiso(context)) return false

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendiente = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val esRecordatorio = aviso.origen == Origen.RECORDATORIO
        val canal = if (esRecordatorio) CANAL_URGENTES else CANAL_AVISOS
        val prioridad = if (esRecordatorio) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT
        val notificacion = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.texto)
            .setPriority(prioridad)
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(aviso.id, notificacion)
        return true
    }
}
