package com.example.orgauns.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orgauns.data.repository.TaskRepositoryImpl
import com.example.orgauns.domain.model.Task
import com.example.orgauns.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TasksUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class TasksViewModel(
    private val taskRepository: TaskRepositoryImpl = TaskRepositoryImpl()
) : ViewModel() {

    private val getTasksUseCase = GetTasksUseCase(taskRepository)
    private val createTaskUseCase = CreateTaskUseCase(taskRepository)
    private val updateTaskUseCase = UpdateTaskUseCase(taskRepository)
    private val deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
    private val toggleTaskDoneUseCase = ToggleTaskDoneUseCase(taskRepository)

    private val _uiState = MutableStateFlow(TasksUiState(isLoading = true))
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            getTasksUseCase()
                .catch { e ->
                    _uiState.value = TasksUiState(error = e.message ?: "Error al cargar tareas")
                }
                .collect { tasks ->
                    _uiState.value = TasksUiState(tasks = tasks)
                }
        }
    }

    fun createTask(task: Task) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            createTaskUseCase(task)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al crear tarea"
                    )
                }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun toggleTaskDone(task: Task) {
        viewModelScope.launch {
            toggleTaskDoneUseCase(task)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

