package com.example.orgauns.domain.repository

import com.example.orgauns.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(): Flow<List<Task>>
    fun getTaskById(taskId: String): Flow<Task?>
    suspend fun createTask(task: Task): Result<String>
    suspend fun updateTask(task: Task): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
}

