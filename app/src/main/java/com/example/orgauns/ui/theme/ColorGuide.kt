package com.example.orgauns.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * GUÍA RÁPIDA DE USO DE COLORES - Simple Agenda
 *
 * Esta guía muestra cómo usar correctamente los colores del tema
 * en diferentes componentes de la app.
 */

// ============================================
// 📌 EJEMPLO 1: Usar colores del tema
// ============================================

/*
@Composable
fun MiComponente() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(
            "Hola",
            color = MaterialTheme.colorScheme.primary // ✅ Siempre usa esto
        )
    }
}
*/

// ============================================
// 📌 EJEMPLO 2: Chips de prioridad personalizados
// ============================================

/*
@Composable
fun PriorityBadge(priority: Int) {
    val (bgColor, textColor) = when (priority) {
        3 -> Color(0xFFFFEBEE) to Color(0xFFC62828) // Alta
        2 -> Color(0xFFFFF8E1) to Color(0xFFF57C00) // Media
        else -> Color(0xFFE8F5E9) to Color(0xFF2E7D32) // Baja
    }

    Surface(color = bgColor) {
        Text("Prioridad", color = textColor)
    }
}
*/

// ============================================
// 📌 EJEMPLO 3: Botones con tema verde
// ============================================

/*
@Composable
fun MyButtons() {
    // Botón principal (verde)
    Button(onClick = { }) {
        Text("Guardar") // Automáticamente usa onPrimary (blanco)
    }

    // Botón secundario
    OutlinedButton(onClick = { }) {
        Text("Cancelar")
    }

    // Botón de error/eliminar
    Button(
        onClick = { },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text("Eliminar")
    }
}
*/

// ============================================
// 📌 EJEMPLO 4: Cards con elevation correcta
// ============================================

/*
@Composable
fun MyCard() {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Título", style = MaterialTheme.typography.titleMedium)
            Text("Contenido", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
*/

// ============================================
// 📌 EJEMPLO 5: Iconos con tint verde
// ============================================

/*
@Composable
fun MyIcons() {
    Icon(
        Icons.Default.CheckCircle,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary // ✅ Verde
    )

    Icon(
        Icons.Default.Error,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error // ❌ Rojo
    )
}
*/

// ============================================
// 📌 EJEMPLO 6: TopAppBar con fondo verde
// ============================================

/*
@Composable
fun MyScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Pantalla") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        // Contenido
    }
}
*/

// ============================================
// 📌 EJEMPLO 7: Switch y Checkbox verdes
// ============================================

/*
@Composable
fun MyControls() {
    Switch(
        checked = true,
        onCheckedChange = { },
        colors = SwitchDefaults.colors(
            checkedThumbColor = MaterialTheme.colorScheme.primary,
            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
        )
    )

    Checkbox(
        checked = true,
        onCheckedChange = { },
        colors = CheckboxDefaults.colors(
            checkedColor = MaterialTheme.colorScheme.primary
        )
    )
}
*/

// ============================================
// ⚠️ LO QUE NO DEBES HACER
// ============================================

/*
// ❌ NO uses colores hardcodeados
Text("Hola", color = Color(0xFF43A047)) // MAL

// ✅ Usa el tema
Text("Hola", color = MaterialTheme.colorScheme.primary) // BIEN

// ❌ NO uses colors genéricos
Card(colors = CardDefaults.cardColors(containerColor = Color.White)) // MAL

// ✅ Usa colores del tema
Card(colors = CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.surface
)) // BIEN
*/

// ============================================
// 🎨 COLORES DISPONIBLES
// ============================================

/*
MaterialTheme.colorScheme.primary           // Verde principal
MaterialTheme.colorScheme.onPrimary         // Texto sobre verde (blanco)
MaterialTheme.colorScheme.primaryContainer  // Verde claro para fondos
MaterialTheme.colorScheme.onPrimaryContainer // Texto sobre verde claro

MaterialTheme.colorScheme.secondary         // Teal (verde azulado)
MaterialTheme.colorScheme.tertiary          // Lima (amarillo verdoso)

MaterialTheme.colorScheme.error             // Rojo para errores
MaterialTheme.colorScheme.surface           // Fondo de cards
MaterialTheme.colorScheme.background        // Fondo de pantalla

MaterialTheme.colorScheme.onSurface         // Texto principal
MaterialTheme.colorScheme.onSurfaceVariant  // Texto secundario
*/

