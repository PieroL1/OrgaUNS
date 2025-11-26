package com.example.orgauns.provider

import android.net.Uri

/**
 * Contrato que define la estructura del ContentProvider
 */
object TasksContract {

    const val AUTHORITY = "com.example.orgauns.provider"

    val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/tasks")

    // Nombres de columnas
    object Columns {
        const val ID = "id"
        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val IS_COMPLETED = "is_completed"
        const val PRIORITY = "priority"
        const val DUE_DATE = "due_date"
        const val CREATED_AT = "created_at"
    }
}

