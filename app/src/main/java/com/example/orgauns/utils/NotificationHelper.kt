package com.example.orgauns.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.orgauns.MainActivity
import com.example.orgauns.R

object NotificationHelper {

    const val CHANNEL_ID_REMINDERS = "task_reminders"
    const val CHANNEL_ID_SYNC = "sync_notifications"
    const val CHANNEL_ID_SYSTEM = "system_notifications"

    const val NOTIFICATION_ID_REMINDER = 1001
    const val NOTIFICATION_ID_SYNC = 1002
    const val NOTIFICATION_ID_BATTERY = 1003
    const val NOTIFICATION_ID_BOOT = 1004

    /**
     * Crea todos los canales de notificación necesarios
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Canal para recordatorios de tareas
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Recordatorios de Tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de recordatorios de tareas pendientes"
                enableVibration(true)
            }

            // Canal para sincronización
            val syncChannel = NotificationChannel(
                CHANNEL_ID_SYNC,
                "Sincronización",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones de sincronización de datos"
            }

            // Canal para eventos del sistema
            val systemChannel = NotificationChannel(
                CHANNEL_ID_SYSTEM,
                "Sistema",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones del sistema como batería baja o reinicio"
            }

            notificationManager.createNotificationChannel(reminderChannel)
            notificationManager.createNotificationChannel(syncChannel)
            notificationManager.createNotificationChannel(systemChannel)
        }
    }

    /**
     * Muestra una notificación de recordatorio de tarea
     */
    fun showTaskReminderNotification(
        context: Context,
        taskId: String,
        taskTitle: String,
        taskDescription: String?
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle("⏰ Recordatorio: $taskTitle")
            .setContentText(taskDescription ?: "Tienes una tarea pendiente")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID_REMINDER + taskId.hashCode(), notification)
    }

    /**
     * Muestra una notificación de prueba (para demostración)
     */
    fun showTestReminderNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle("🔔 Notificación de Prueba")
            .setContentText("¡El BroadcastReceiver está funcionando correctamente!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
    }

    /**
     * Muestra una notificación de sincronización
     */
    fun showSyncNotification(
        context: Context,
        title: String,
        message: String,
        isOngoing: Boolean = false
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SYNC)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(isOngoing)
            .setAutoCancel(!isOngoing)
            .build()

        notificationManager.notify(NOTIFICATION_ID_SYNC, notification)
    }

    /**
     * Muestra una notificación de batería baja
     */
    fun showBatteryLowNotification(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SYSTEM)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("🔋 Batería Baja Detectada")
            .setContentText("OrgaUNS ha guardado tus tareas automáticamente")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BATTERY, notification)
    }

    /**
     * Muestra una notificación de reinicio completado
     */
    fun showBootCompletedNotification(context: Context, remindersCount: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SYSTEM)
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("📱 OrgaUNS Iniciado")
            .setContentText("$remindersCount recordatorio(s) reprogramado(s)")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BOOT, notification)
    }

    /**
     * Cancela una notificación específica
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}

