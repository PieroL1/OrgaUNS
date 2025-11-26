package com.example.orgauns.presentation.calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orgauns.domain.model.Task
import com.example.orgauns.utils.AlarmScheduler
import com.example.orgauns.utils.NotificationHelper
import android.content.Intent
import android.provider.CalendarContract
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Estados para editar y eliminar
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }

    val tasksForSelectedDate = remember(uiState.selectedDate, uiState.tasks) {
        viewModel.getTasksForSelectedDate()
    }
    val daysWithTasks = remember(uiState.currentYearMonth, uiState.tasks) {
        viewModel.getDaysWithTasks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendario") },
                windowInsets = WindowInsets(0.dp) // Eliminar padding superior
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp) // Reducir padding
        ) {
            // Header compacto con mes/año y controles
            CompactMonthHeader(
                yearMonth = uiState.currentYearMonth,
                isWeekView = uiState.isWeekView,
                onPreviousMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() },
                onTodayClick = { viewModel.selectToday() },
                onToggleView = { viewModel.toggleViewMode() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Días de la semana (encabezados)
            DaysOfWeekHeader()

            Spacer(modifier = Modifier.height(4.dp))

            // Grid del calendario (mensual o semanal)
            if (uiState.isWeekView) {
                WeekGrid(
                    selectedDate = uiState.selectedDate,
                    daysWithTasks = daysWithTasks,
                    onDateSelected = { date -> viewModel.selectDate(date) }
                )
            } else {
                MonthGrid(
                    yearMonth = uiState.currentYearMonth,
                    selectedDate = uiState.selectedDate,
                    daysWithTasks = daysWithTasks,
                    onDateSelected = { date -> viewModel.selectDate(date) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección de tareas del día seleccionado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tareas del día",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${tasksForSelectedDate.size}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (tasksForSelectedDate.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay tareas para este día",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(items = tasksForSelectedDate, key = { it.id }) { task ->
                        CompactTaskItem(
                            task = task,
                            onToggleDone = { viewModel.toggleTaskDone(task) },
                            onEdit = { taskToEdit = task },
                            onDelete = { taskToDelete = task },
                            onShareToCalendar = {
                                val intent = Intent(Intent.ACTION_INSERT).apply {
                                    data = CalendarContract.Events.CONTENT_URI
                                    putExtra(CalendarContract.Events.TITLE, task.title)
                                    putExtra(CalendarContract.Events.DESCRIPTION, task.description)
                                    task.dueAt?.let { dueTime ->
                                        putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dueTime)
                                        putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dueTime + 3600000)
                                    }
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de edición (reutilizamos el de TasksScreen)
    taskToEdit?.let { task ->
        com.example.orgauns.presentation.tasks.EditTaskDialog(
            task = task,
            onDismiss = { taskToEdit = null },
            onUpdate = { updatedTask ->
                viewModel.updateTask(updatedTask)

                // Actualizar alarma si tiene fecha/hora futura
                updatedTask.dueAt?.let { dueTime ->
                    if (dueTime > System.currentTimeMillis()) {
                        NotificationHelper.createNotificationChannels(context)
                        AlarmScheduler.scheduleTaskReminder(
                            context = context,
                            taskId = updatedTask.id,
                            taskTitle = updatedTask.title,
                            taskDescription = updatedTask.description.takeIf { it.isNotEmpty() },
                            triggerTime = dueTime
                        )
                    }
                }

                taskToEdit = null
            }
        )
    }

    // Diálogo de confirmación para eliminar tarea
    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Eliminar tarea") },
            text = {
                Text("¿Seguro que deseas eliminar la tarea \"${task.title}\"? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteTask(task.id)
                        taskToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CompactMonthHeader(
    yearMonth: java.time.YearMonth,
    isWeekView: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onTodayClick: () -> Unit,
    onToggleView: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMonth, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Anterior",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = CalendarUtils.formatYearMonth(yearMonth),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                IconButton(onClick = onNextMonth, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Siguiente",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = onTodayClick,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hoy", style = MaterialTheme.typography.labelMedium)
                }

                FilledTonalButton(
                    onClick = onToggleView,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Icon(
                        if (isWeekView) Icons.Default.CalendarViewMonth else Icons.Default.CalendarViewWeek,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (isWeekView) "Mes" else "Semana",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CalendarUtils.getDaysOfWeekHeaders().forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MonthGrid(
    yearMonth: java.time.YearMonth,
    selectedDate: LocalDate,
    daysWithTasks: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val days = remember(yearMonth) {
        CalendarUtils.getDaysInMonth(yearMonth)
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 240.dp), // Reducido de 350dp a 240dp
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(days) { calendarDay ->
            DayCell(
                day = calendarDay,
                isSelected = calendarDay.date == selectedDate,
                hasTasks = daysWithTasks.contains(calendarDay.date),
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
fun WeekGrid(
    selectedDate: LocalDate,
    daysWithTasks: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val days = remember(selectedDate) {
        CalendarUtils.getDaysInWeek(selectedDate)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp), // Vista semanal muy compacta
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        days.forEach { calendarDay ->
            Box(modifier = Modifier.weight(1f)) {
                DayCell(
                    day = calendarDay,
                    isSelected = calendarDay.date == selectedDate,
                    hasTasks = daysWithTasks.contains(calendarDay.date),
                    onDateSelected = onDateSelected
                )
            }
        }
    }
}

@Composable
fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    hasTasks: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isToday = day.date == LocalDate.now()

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.secondaryContainer
                    else -> MaterialTheme.colorScheme.surface
                }
            )
            .clickable(enabled = day.isCurrentMonth) {
                onDateSelected(day.date)
            }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = when {
                    !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.onSecondaryContainer
                    else -> MaterialTheme.colorScheme.onSurface
                }
            )

            // Indicador de tareas mejorado
            if (hasTasks && day.isCurrentMonth) {
                Spacer(modifier = Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                )
            }
        }
    }
}

@Composable
fun CompactTaskItem(
    task: Task,
    onToggleDone: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShareToCalendar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Primera fila: Checkbox + Título + Prioridad
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.done,
                    onCheckedChange = { onToggleDone() },
                    modifier = Modifier.size(40.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (task.done)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        else
                            MaterialTheme.colorScheme.onSurface,
                        maxLines = 2
                    )
                }

                // Badge de prioridad
                val (priorityText, priorityColor) = when (task.priority) {
                    3 -> "Alta" to MaterialTheme.colorScheme.error
                    2 -> "Media" to MaterialTheme.colorScheme.tertiary
                    else -> "Baja" to MaterialTheme.colorScheme.secondary
                }
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = priorityColor.copy(alpha = 0.2f),
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = priorityText,
                        style = MaterialTheme.typography.labelSmall,
                        color = priorityColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Descripción (si existe)
            if (task.description.isNotEmpty()) {
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 40.dp, top = 2.dp),
                    maxLines = 2
                )
            }

            // Fecha y botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp, top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Fecha de vencimiento
                task.dueAt?.let { dueDate ->
                    val dateFormat = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
                    Text(
                        text = "📅 ${dateFormat.format(Date(dueDate))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botones de acción en fila horizontal
                Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                    // Botón compartir a Calendar (solo si tiene fecha)
                    if (task.dueAt != null) {
                        IconButton(
                            onClick = onShareToCalendar,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = "Compartir a Calendar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Botón editar
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Botón eliminar
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

