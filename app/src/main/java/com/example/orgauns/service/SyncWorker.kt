package com.example.orgauns.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.orgauns.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

/**
 * Worker que ejecuta la sincronización en segundo plano
 */
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        return try {
            // Verificar que el usuario esté autenticado
            val userId = auth.currentUser?.uid ?: return Result.failure()

            // Mostrar notificación de inicio
            NotificationHelper.showSyncNotification(
                context = applicationContext,
                title = "Sincronizando...",
                message = "Actualizando tus tareas",
                isOngoing = true
            )

            // Obtener tareas de Firebase
            val tasksSnapshot = firestore
                .collection("users")
                .document(userId)
                .collection("tasks")
                .get()
                .await()

            val tasksCount = tasksSnapshot.documents.size

            // Obtener notas de Firebase
            val notesSnapshot = firestore
                .collection("users")
                .document(userId)
                .collection("notes")
                .get()
                .await()

            val notesCount = notesSnapshot.documents.size

            // Guardar última sincronización
            val prefs = applicationContext.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putLong("last_sync_time", System.currentTimeMillis())
                putInt("last_sync_tasks_count", tasksCount)
                putInt("last_sync_notes_count", notesCount)
                apply()
            }

            // Mostrar notificación de éxito
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            NotificationHelper.showSyncNotification(
                context = applicationContext,
                title = "✅ Sincronización Completada",
                message = "$tasksCount tareas y $notesCount notas sincronizadas a las $currentTime",
                isOngoing = false
            )

            Result.success()
        } catch (e: Exception) {
            // Mostrar notificación de error
            NotificationHelper.showSyncNotification(
                context = applicationContext,
                title = "❌ Error en Sincronización",
                message = "No se pudo sincronizar: ${e.message}",
                isOngoing = false
            )
            Result.failure()
        }
    }
}

