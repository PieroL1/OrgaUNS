package com.example.orgauns.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.orgauns.R
import com.example.orgauns.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    authRepository: AuthRepositoryImpl,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    val currentUser by authRepository.currentUser.collectAsState(initial = null)
    var isCheckingAuth by remember { mutableStateOf(true) }

    // Esperar un momento mínimo para evitar flicker y verificar auth
    LaunchedEffect(Unit) {
        delay(800) // Mínimo delay para mostrar la splash
        isCheckingAuth = false
    }

    // Navegar cuando el estado esté listo
    LaunchedEffect(isCheckingAuth, currentUser) {
        if (!isCheckingAuth) {
            if (currentUser != null) {
                onNavigateToMain()
            } else {
                onNavigateToLogin()
            }
        }
    }

    // UI de Splash
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Imagen de fondo del splash
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "Splash Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Capa de overlay semitransparente (opcional, para mejorar legibilidad)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
        )

        // Contenido centrado
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Nombre de la app
            Text(
                text = "Simple Agenda",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Indicador de carga
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 4.dp
            )
        }
    }
}

