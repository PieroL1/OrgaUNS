package com.example.orgauns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.orgauns.data.repository.AuthRepositoryImpl
import com.example.orgauns.data.repository.SettingsRepositoryImpl
import com.example.orgauns.presentation.navigation.AppNavigation
import com.example.orgauns.ui.theme.OrgaUNSTheme
import com.example.orgauns.utils.NotificationHelper
import com.example.orgauns.service.TaskSyncService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear canales de notificación
        NotificationHelper.createNotificationChannels(this)

        // Programar sincronización periódica
        TaskSyncService.schedulePeriodicSync(this)

        setContent {
            OrgaUNSApp()
        }
    }
}

@Composable
fun OrgaUNSApp() {
    val navController = rememberNavController()
    val authRepository = AuthRepositoryImpl()
    val settingsRepository = SettingsRepositoryImpl(navController.context)

    val isDarkMode by settingsRepository.isDarkMode.collectAsState(initial = isSystemInDarkTheme())

    OrgaUNSTheme(darkTheme = isDarkMode) {
        AppNavigation(
            navController = navController,
            authRepository = authRepository
        )
    }
}
