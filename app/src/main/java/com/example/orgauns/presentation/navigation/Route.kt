package com.example.orgauns.presentation.navigation

sealed class Route(val route: String) {
    // Initial
    data object Splash : Route("splash")

    // Auth
    data object Auth : Route("auth")
    data object Login : Route("login")
    data object Register : Route("register")

    // Main
    data object Main : Route("main")
    data object Tasks : Route("tasks")
    data object Calendar : Route("calendar")
    data object Notes : Route("notes")
    data object Map : Route("map")
    data object Settings : Route("settings")

    // Details/Edit
    data object TaskDetail : Route("task_detail/{taskId}") {
        fun createRoute(taskId: String) = "task_detail/$taskId"
    }
    data object NoteDetail : Route("note_detail/{noteId}") {
        fun createRoute(noteId: String) = "note_detail/$noteId"
    }
}

