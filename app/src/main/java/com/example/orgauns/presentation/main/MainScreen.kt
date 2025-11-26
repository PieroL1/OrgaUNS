package com.example.orgauns.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.orgauns.presentation.calendar.CalendarScreen
import com.example.orgauns.presentation.map.MapScreen
import com.example.orgauns.presentation.navigation.Route
import com.example.orgauns.presentation.notes.NotesScreen
import com.example.orgauns.presentation.settings.SettingsScreen
import com.example.orgauns.presentation.tasks.TasksScreen

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    data object Tasks : BottomNavItem(Route.Tasks.route, Icons.Default.CheckCircle, "Tareas")
    data object Calendar : BottomNavItem(Route.Calendar.route, Icons.Default.DateRange, "Calendario")
    data object Notes : BottomNavItem(Route.Notes.route, Icons.Default.Description, "Notas")
    data object Map : BottomNavItem(Route.Map.route, Icons.Default.LocationOn, "Mapa")
    data object Settings : BottomNavItem(Route.Settings.route, Icons.Default.Settings, "Ajustes")
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val items = listOf(BottomNavItem.Tasks, BottomNavItem.Calendar, BottomNavItem.Notes, BottomNavItem.Map, BottomNavItem.Settings)
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(26.dp)
                            )
                        },
                        label = { Text(item.label, style = MaterialTheme.typography.labelMedium) },
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) { saveState = true }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = Route.Tasks.route, modifier = Modifier.padding(innerPadding)) {
            composable(Route.Tasks.route) { TasksScreen() }
            composable(Route.Calendar.route) { CalendarScreen() }
            composable(Route.Notes.route) { NotesScreen() }
            composable(Route.Map.route) { MapScreen() }
            composable(Route.Settings.route) { SettingsScreen(onLogout = onLogout) }
        }
    }
}

