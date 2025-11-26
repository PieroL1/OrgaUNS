package com.example.orgauns.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.orgauns.utils.NotificationHelper

/**
 * BroadcastReceiver que se activa cuando llega la hora de recordar una tarea
 */
class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Obtener datos de la tarea desde el Intent
        val taskId = intent.getStringExtra("taskId") ?: return
        val taskTitle = intent.getStringExtra("taskTitle") ?: "Tarea Pendiente"
        val taskDescription = intent.getStringExtra("taskDescription")

        // Mostrar notificación de recordatorio
        NotificationHelper.showTaskReminderNotification(
            context = context,
            taskId = taskId,
            taskTitle = taskTitle,
            taskDescription = taskDescription
        )
    }
}

