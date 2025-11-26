package com.example.orgauns.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.orgauns.MainActivity
import com.example.orgauns.R
import com.example.orgauns.provider.TasksContract

/**
 * Widget que muestra las tareas usando el ContentProvider
 */
class TasksWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Crear RemoteViews
        val views = RemoteViews(context.packageName, R.layout.widget_tasks)

        // Consultar tareas desde el ContentProvider
        val cursor = context.contentResolver.query(
            TasksContract.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        var tasksText = "No hay tareas"
        var tasksCount = 0

        cursor?.use {
            tasksCount = it.count
            if (tasksCount > 0) {
                val tasksList = mutableListOf<String>()
                var count = 0
                while (it.moveToNext() && count < 3) {
                    val title = it.getString(it.getColumnIndexOrThrow(TasksContract.Columns.TITLE))
                    val isCompleted = it.getInt(it.getColumnIndexOrThrow(TasksContract.Columns.IS_COMPLETED)) == 1
                    if (!isCompleted) {
                        tasksList.add("• $title")
                        count++
                    }
                }
                tasksText = if (tasksList.isEmpty()) {
                    "✅ Todas completadas"
                } else {
                    tasksList.joinToString("\n")
                }
            }
        }

        // Actualizar textos
        views.setTextViewText(R.id.widget_title, "OrgaUNS ($tasksCount)")
        views.setTextViewText(R.id.widget_tasks, tasksText)

        // Intent para abrir la app al hacer click
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_title, pendingIntent)

        // Actualizar widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

