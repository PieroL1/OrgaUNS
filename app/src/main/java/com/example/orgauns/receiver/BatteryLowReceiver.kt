package com.example.orgauns.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.orgauns.utils.NotificationHelper

/**
 * BroadcastReceiver que se activa cuando la batería está baja
 */
class BatteryLowReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BATTERY_LOW) {

            // Mostrar notificación de batería baja
            NotificationHelper.showBatteryLowNotification(context)

            // Aquí podrías implementar lógica adicional como:
            // - Guardar automáticamente cambios pendientes
            // - Sincronizar datos urgentes
            // - Desactivar funciones que consuman mucha batería
        }
    }
}

