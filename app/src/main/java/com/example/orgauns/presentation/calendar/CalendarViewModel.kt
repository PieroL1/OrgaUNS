package com.example.orgauns.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orgauns.data.repository.TaskRepositoryImpl
import com.example.orgauns.domain.model.Task
import com.example.orgauns.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val tasks: List<Task> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isWeekView: Boolean = false // Vista semanal o mensual
)

class CalendarViewModel(
    private val taskRepository: TaskRepositoryImpl = TaskRepositoryImpl()
) : ViewModel() {

    private val getTasksUseCase = GetTasksUseCase(taskRepository)
    private val updateTaskUseCase = UpdateTaskUseCase(taskRepository)
    private val deleteTaskUseCase = DeleteTaskUseCase(taskRepository)
    private val toggleTaskDoneUseCase = ToggleTaskDoneUseCase(taskRepository)

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            getTasksUseCase()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Error al cargar tareas"
                    )
                }
                .collect { tasks ->
                    _uiState.value = _uiState.value.copy(
                        tasks = tasks,
                        isLoading = false
                    )
                }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    fun selectToday() {
        _uiState.value = _uiState.value.copy(
            selectedDate = LocalDate.now(),
            currentYearMonth = YearMonth.now()
        )
    }

    fun previousMonth() {
        val newYearMonth = _uiState.value.currentYearMonth.minusMonths(1)
        _uiState.value = _uiState.value.copy(currentYearMonth = newYearMonth)
    }

    fun nextMonth() {
        val newYearMonth = _uiState.value.currentYearMonth.plusMonths(1)
        _uiState.value = _uiState.value.copy(currentYearMonth = newYearMonth)
    }

    fun toggleViewMode() {
        _uiState.value = _uiState.value.copy(isWeekView = !_uiState.value.isWeekView)
    }

    /**
     * Obtiene las tareas del día seleccionado
     */
    fun getTasksForSelectedDate(): List<Task> {
        val selectedDate = _uiState.value.selectedDate
        return _uiState.value.tasks.filter { task ->
            task.dueAt?.let { dueAt ->
                val taskDate = CalendarUtils.epochMillisToLocalDate(dueAt)
                taskDate == selectedDate
            } ?: false
        }
    }

    /**
     * Obtiene un Set de LocalDate con todos los días que tienen tareas en el mes visible
     */
    fun getDaysWithTasks(): Set<LocalDate> {
        val yearMonth = _uiState.value.currentYearMonth
        val firstDay = yearMonth.atDay(1)
        val lastDay = yearMonth.atEndOfMonth()

        return _uiState.value.tasks
            .mapNotNull { task ->
                task.dueAt?.let { dueAt ->
                    CalendarUtils.epochMillisToLocalDate(dueAt)
                }
            }
            .filter { date ->
                // Solo incluir fechas del mes visible
                (date.isEqual(firstDay) || date.isAfter(firstDay)) &&
                (date.isEqual(lastDay) || date.isBefore(lastDay))
            }
            .toSet()
    }

    /**
     * Cambia el estado done de una tarea
     */
    fun toggleTaskDone(task: Task) {
        viewModelScope.launch {
            toggleTaskDoneUseCase(task)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    /**
     * Actualiza una tarea
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }

    /**
     * Elimina una tarea
     */
    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
        }
    }
}

