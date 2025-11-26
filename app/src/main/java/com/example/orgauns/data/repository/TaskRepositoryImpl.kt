package com.example.orgauns.data.repository

import com.example.orgauns.domain.model.Task
import com.example.orgauns.domain.repository.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TaskRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : TaskRepository {

    private fun getUserTasksCollection() = auth.currentUser?.uid?.let { uid ->
        firestore.collection("users").document(uid).collection("tasks")
    }

    override fun getTasks(): Flow<List<Task>> = callbackFlow {
        val collection = getUserTasksCollection()
        if (collection == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val tasks = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(Task::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            trySend(tasks)
        }
        awaitClose { listener.remove() }
    }

    override fun getTaskById(taskId: String): Flow<Task?> = callbackFlow {
        val collection = getUserTasksCollection()
        if (collection == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = collection.document(taskId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            val task = snapshot?.toObject(Task::class.java)?.copy(id = snapshot.id)
            trySend(task)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createTask(task: Task): Result<String> {
        return try {
            val collection = getUserTasksCollection()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val docRef = collection.document()
            val taskWithTimestamp = task.copy(
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            docRef.set(taskWithTimestamp).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(task: Task): Result<Unit> {
        return try {
            val collection = getUserTasksCollection()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val taskWithTimestamp = task.copy(updatedAt = System.currentTimeMillis())
            collection.document(task.id).set(taskWithTimestamp).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            val collection = getUserTasksCollection()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            collection.document(taskId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

