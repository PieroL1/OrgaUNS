package com.example.orgauns.presentation.tasks

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.orgauns.domain.model.Task
import com.example.orgauns.utils.AlarmScheduler
import com.example.orgauns.utils.NotificationHelper
import android.content.Intent
import android.provider.CalendarContract
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen() {
    val context = LocalContext.current
    val viewModel: TasksViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) } // Para editar
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Estados de filtros
    var statusFilter by remember { mutableStateOf("Todas") }
    var priorityFilter by remember { mutableStateOf(0) } // 0 = todas, 1 = baja, 2 = media, 3 = alta

    // Filtrar tareas según búsqueda y filtros
    val filteredTasks = remember(searchQuery, uiState.tasks, statusFilter, priorityFilter) {
        var result = uiState.tasks

        // Filtro de búsqueda
        if (searchQuery.isNotBlank()) {
            result = result.filter { task ->
                task.title.contains(searchQuery, ignoreCase = true) ||
                task.description.contains(searchQuery, ignoreCase = true)
            }
        }

        // Filtro de estado
        result = when (statusFilter) {
            "Pendientes" -> result.filter { !it.done }
            "Completadas" -> result.filter { it.done }
            "Con fecha" -> result.filter { it.dueAt != null }
            "Sin fecha" -> result.filter { it.dueAt == null }
            else -> result // "Todas"
        }

        // Filtro de prioridad
        if (priorityFilter != 0) {
            result = result.filter { it.priority == priorityFilter }
        }

        result
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tareas") },
                windowInsets = WindowInsets(0.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp
                )
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar tarea",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                uiState.tasks.isEmpty() -> Text("No hay tareas", modifier = Modifier.align(Alignment.Center))
                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Barra de búsqueda
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            placeholder = { Text("Buscar tareas…") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Limpiar"
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )

                        // Filtros de estado
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val statusOptions = listOf("Todas", "Pendientes", "Completadas", "Con fecha", "Sin fecha")
                            statusOptions.forEach { option ->
                                FilterChip(
                                    selected = statusFilter == option,
                                    onClick = { statusFilter = option },
                                    label = { Text(option) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                )
                            }
                        }

                        // Filtros de prioridad
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = priorityFilter == 0,
                                onClick = { priorityFilter = 0 },
                                label = { Text("Todas las prioridades") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                            FilterChip(
                                selected = priorityFilter == 3,
                                onClick = { priorityFilter = 3 },
                                label = { Text("Alta") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                            FilterChip(
                                selected = priorityFilter == 2,
                                onClick = { priorityFilter = 2 },
                                label = { Text("Media") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                            FilterChip(
                                selected = priorityFilter == 1,
                                onClick = { priorityFilter = 1 },
                                label = { Text("Baja") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Mostrar resultados o empty state
                        if (filteredTasks.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (searchQuery.isNotBlank() || statusFilter != "Todas" || priorityFilter != 0) {
                                        "No hay tareas con estos filtros"
                                    } else {
                                        "No se encontraron resultados"
                                    }
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = filteredTasks.sortedWith(
                                        compareBy(
                                            { it.dueAt == null },  // null al final
                                            { it.dueAt ?: Long.MAX_VALUE }  // por fecha ascendente
                                        )
                                    ),
                                    key = { it.id }
                                ) { task ->
                                    TaskItem(
                                        task = task,
                                        onToggleDone = { viewModel.toggleTaskDone(task) },
                                        onEdit = { taskToEdit = task },
                                        onDelete = { taskToDelete = task },
                                        onShareToCalendar = {
                                            // Compartir a Google Calendar
                                            val intent = Intent(Intent.ACTION_INSERT).apply {
                                                data = CalendarContract.Events.CONTENT_URI
                                                putExtra(CalendarContract.Events.TITLE, task.title)
                                                putExtra(CalendarContract.Events.DESCRIPTION, task.description)
                                                task.dueAt?.let { dueTime ->
                                                    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, dueTime)
                                                    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, dueTime + 3600000) // +1 hora
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
            }
        }

        if (showDialog) {
            CreateTaskDialog(
                onDismiss = { showDialog = false },
                onCreate = { task ->
                    viewModel.createTask(task)

                    // Programar alarma si tiene fecha/hora futura
                    task.dueAt?.let { dueTime ->
                        if (dueTime > System.currentTimeMillis()) {
                            NotificationHelper.createNotificationChannels(context)
                            AlarmScheduler.scheduleTaskReminder(
                                context = context,
                                taskId = task.id,
                                taskTitle = task.title,
                                taskDescription = task.description.takeIf { it.isNotEmpty() },
                                triggerTime = dueTime
                            )
                        }
                    }

                    showDialog = false
                }
            )
        }

        // Diálogo de edición
        taskToEdit?.let { task ->
            EditTaskDialog(
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
}

@Composable
fun TaskItem(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(onDismiss: () -> Unit, onCreate: (Task) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf(1) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    var selectedHour by remember { mutableStateOf<Int?>(null) }
    var selectedMinute by remember { mutableStateOf<Int?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarea") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                Text("Prioridad", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = priority == 1, onClick = { priority = 1 }, label = { Text("Baja") })
                    FilterChip(selected = priority == 2, onClick = { priority = 2 }, label = { Text("Media") })
                    FilterChip(selected = priority == 3, onClick = { priority = 3 }, label = { Text("Alta") })
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                Text("Fecha y hora de vencimiento (opcional)", style = MaterialTheme.typography.labelMedium)

                // Botón para seleccionar fecha
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (selectedDateMillis != null) {
                            // Convertir epoch millis UTC a LocalDate para mostrar correctamente
                            val localDate = Instant.ofEpochMilli(selectedDateMillis!!)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            dateFormat.format(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        } else {
                            "Seleccionar fecha"
                        }
                    )
                }

                // Botón para seleccionar hora (solo si ya se seleccionó fecha)
                if (selectedDateMillis != null) {
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (selectedHour != null && selectedMinute != null) {
                                String.format("%02d:%02d", selectedHour, selectedMinute)
                            } else {
                                "Seleccionar hora (opcional)"
                            }
                        )
                    }
                }

                // Botón para limpiar fecha/hora
                if (selectedDateMillis != null) {
                    TextButton(
                        onClick = {
                            selectedDateMillis = null
                            selectedHour = null
                            selectedMinute = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quitar fecha/hora")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val dueAt = selectedDateMillis?.let { dateMillis ->
                            // Convertir epoch millis UTC del DatePicker a LocalDate en zona local
                            val localDate = Instant.ofEpochMilli(dateMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()

                            // Construir LocalTime según si hay hora seleccionada o no
                            val localTime = if (selectedHour != null && selectedMinute != null) {
                                LocalTime.of(selectedHour!!, selectedMinute!!, 0)
                            } else {
                                LocalTime.of(23, 59, 0)
                            }

                            // Combinar fecha y hora en zona local del dispositivo
                            localDate
                                .atTime(localTime)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                        }

                        onCreate(Task(
                            title = title,
                            description = description,
                            priority = priority,
                            dueAt = dueAt
                        ))
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    // DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePickerDialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(task: Task, onDismiss: () -> Unit, onUpdate: (Task) -> Unit) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    var priority by remember { mutableStateOf(task.priority) }

    // Extraer fecha y hora de la tarea existente
    var selectedDateMillis by remember {
        mutableStateOf(task.dueAt?.let { dueTime ->
            // Convertir timestamp a medianoche UTC para el DatePicker
            val localDate = Instant.ofEpochMilli(dueTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
            localDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        })
    }

    var selectedHour by remember {
        mutableStateOf(task.dueAt?.let { dueTime ->
            Instant.ofEpochMilli(dueTime)
                .atZone(ZoneId.systemDefault())
                .hour
        })
    }

    var selectedMinute by remember {
        mutableStateOf(task.dueAt?.let { dueTime ->
            Instant.ofEpochMilli(dueTime)
                .atZone(ZoneId.systemDefault())
                .minute
        })
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Tarea") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                Text("Prioridad", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = priority == 1, onClick = { priority = 1 }, label = { Text("Baja") })
                    FilterChip(selected = priority == 2, onClick = { priority = 2 }, label = { Text("Media") })
                    FilterChip(selected = priority == 3, onClick = { priority = 3 }, label = { Text("Alta") })
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                Text("Fecha y hora de vencimiento (opcional)", style = MaterialTheme.typography.labelMedium)

                // Botón para seleccionar fecha
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (selectedDateMillis != null) {
                            val localDate = Instant.ofEpochMilli(selectedDateMillis!!)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            dateFormat.format(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()))
                        } else {
                            "Seleccionar fecha"
                        }
                    )
                }

                // Botón para seleccionar hora (solo si ya se seleccionó fecha)
                if (selectedDateMillis != null) {
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (selectedHour != null && selectedMinute != null) {
                                String.format("%02d:%02d", selectedHour, selectedMinute)
                            } else {
                                "Seleccionar hora (opcional)"
                            }
                        )
                    }
                }

                // Botón para limpiar fecha/hora
                if (selectedDateMillis != null) {
                    TextButton(
                        onClick = {
                            selectedDateMillis = null
                            selectedHour = null
                            selectedMinute = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quitar fecha/hora")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        val dueAt = selectedDateMillis?.let { dateMillis ->
                            val localDate = Instant.ofEpochMilli(dateMillis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()

                            val localTime = if (selectedHour != null && selectedMinute != null) {
                                LocalTime.of(selectedHour!!, selectedMinute!!, 0)
                            } else {
                                LocalTime.of(23, 59, 0)
                            }

                            localDate
                                .atTime(localTime)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                                .toEpochMilli()
                        }

                        onUpdate(task.copy(
                            title = title,
                            description = description,
                            priority = priority,
                            dueAt = dueAt
                        ))
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )

    // DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePickerDialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedHour = timePickerState.hour
                    selectedMinute = timePickerState.minute
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

