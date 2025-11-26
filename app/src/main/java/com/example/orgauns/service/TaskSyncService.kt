package com.example.orgauns.service

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Servicio para gestionar la sincronización de tareas
 */
object TaskSyncService {

    private const val SYNC_WORK_NAME = "task_sync_work"
    private const val PERIODIC_SYNC_WORK_NAME = "periodic_task_sync"

    /**
     * Ejecuta una sincronización inmediata (para el botón de prueba)
     */
    fun syncNow(context: Context) {
        val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    /**
     * Programa sincronización periódica cada 6 horas
     */
    fun schedulePeriodicSync(context: Context) {
        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            6, TimeUnit.HOURS // Sincronizar cada 6 horas
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

    /**
     * Cancela la sincronización periódica
     */
    fun cancelPeriodicSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(PERIODIC_SYNC_WORK_NAME)
    }

    /**
     * Obtiene información de la última sincronización
     */
    fun getLastSyncInfo(context: Context): SyncInfo {
        val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
        val lastSyncTime = prefs.getLong("last_sync_time", 0)
        val tasksCount = prefs.getInt("last_sync_tasks_count", 0)
        val notesCount = prefs.getInt("last_sync_notes_count", 0)

        return SyncInfo(lastSyncTime, tasksCount, notesCount)
    }
}

/**
 * Datos de la última sincronización
 */
data class SyncInfo(
    val timestamp: Long,
    val tasksCount: Int,
    val notesCount: Int
) {
    fun getTimeAgo(): String {
        if (timestamp == 0L) return "Nunca"

        val diff = System.currentTimeMillis() - timestamp
        val minutes = diff / (1000 * 60)
        val hours = diff / (1000 * 60 * 60)
        val days = diff / (1000 * 60 * 60 * 24)

        return when {
            minutes < 1 -> "Hace un momento"
            minutes < 60 -> "Hace $minutes minuto${if (minutes > 1) "s" else ""}"
            hours < 24 -> "Hace $hours hora${if (hours > 1) "s" else ""}"
            else -> "Hace $days día${if (days > 1) "s" else ""}"
        }
    }
}

