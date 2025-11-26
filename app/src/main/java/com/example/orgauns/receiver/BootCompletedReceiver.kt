package com.example.orgauns.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.orgauns.utils.NotificationHelper

/**
 * BroadcastReceiver que se activa cuando el teléfono se reinicia
 */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // Crear canales de notificación
            NotificationHelper.createNotificationChannels(context)

            // Aquí podrías reprogramar alarmas de tareas guardadas
            // Por ahora solo mostramos una notificación de que el sistema se inició

            // Simular que se reprogramaron algunos recordatorios
            val remindersCount = 0 // TODO: Obtener de base de datos local

            NotificationHelper.showBootCompletedNotification(context, remindersCount)
        }
    }
}

