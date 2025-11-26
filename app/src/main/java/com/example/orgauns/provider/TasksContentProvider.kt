package com.example.orgauns.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

/**
 * ContentProvider que expone las tareas para otras aplicaciones o widgets
 */
class TasksContentProvider : ContentProvider() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val TASKS = 1
        private const val TASK_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(TasksContract.AUTHORITY, "tasks", TASKS)
            addURI(TasksContract.AUTHORITY, "tasks/#", TASK_ID)
        }

        // Contador de consultas para demostración
        var queryCount = 0
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        queryCount++

        val cursor = MatrixCursor(arrayOf(
            TasksContract.Columns.ID,
            TasksContract.Columns.TITLE,
            TasksContract.Columns.DESCRIPTION,
            TasksContract.Columns.IS_COMPLETED,
            TasksContract.Columns.PRIORITY,
            TasksContract.Columns.DUE_DATE,
            TasksContract.Columns.CREATED_AT
        ))

        return runBlocking {
            try {
                val userId = auth.currentUser?.uid ?: return@runBlocking cursor

                val tasksSnapshot = firestore
                    .collection("users")
                    .document(userId)
                    .collection("tasks")
                    .get()
                    .await()

                for (document in tasksSnapshot.documents) {
                    cursor.addRow(arrayOf(
                        document.id,
                        document.getString("title") ?: "",
                        document.getString("description") ?: "",
                        if (document.getBoolean("isCompleted") == true) 1 else 0,
                        document.getLong("priority")?.toInt() ?: 1,
                        document.getLong("dueDate") ?: 0L,
                        document.getLong("createdAt") ?: 0L
                    ))
                }

                cursor
            } catch (e: Exception) {
                cursor
            }
        }
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            TASKS -> "vnd.android.cursor.dir/vnd.${TasksContract.AUTHORITY}.tasks"
            TASK_ID -> "vnd.android.cursor.item/vnd.${TasksContract.AUTHORITY}.tasks"
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Solo lectura por seguridad
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        // Solo lectura por seguridad
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        // Solo lectura por seguridad
        return 0
    }
}

