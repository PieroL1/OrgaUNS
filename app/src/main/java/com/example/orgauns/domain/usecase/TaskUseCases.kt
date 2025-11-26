package com.example.orgauns.domain.usecase

import com.example.orgauns.domain.model.Task
import com.example.orgauns.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.getTasks()
}

class CreateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<String> = repository.createTask(task)
}

class UpdateTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<Unit> = repository.updateTask(task)
}

class DeleteTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(taskId: String): Result<Unit> = repository.deleteTask(taskId)
}

class ToggleTaskDoneUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task): Result<Unit> {
        return repository.updateTask(task.copy(done = !task.done))
    }
}

