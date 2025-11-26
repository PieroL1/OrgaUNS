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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orgauns.domain.model.Task
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
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
                        CompactTaskItem(task = task)
                    }
                }
            }
        }
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
fun CompactTaskItem(task: Task) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp,
            pressedElevation = 6.dp
        ),
        shape = MaterialTheme.shapes.large, // Esquinas más redondeadas
        colors = CardDefaults.cardColors(
            containerColor = if (task.done)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de prioridad (barra lateral mejorada)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(
                        color = when (task.priority) {
                            3 -> Color(0xFFD32F2F) // Rojo suave
                            2 -> Color(0xFFFFA726) // Amarillo/Naranja
                            else -> MaterialTheme.colorScheme.primary // Verde
                        },
                        shape = MaterialTheme.shapes.small
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (task.done)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else
                            MaterialTheme.colorScheme.onSurface
                    )

                    if (task.done) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Completada",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (task.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                task.dueAt?.let { dueAt ->
                    Spacer(modifier = Modifier.height(4.dp))
                    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    Text(
                        text = timeFormat.format(Date(dueAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

