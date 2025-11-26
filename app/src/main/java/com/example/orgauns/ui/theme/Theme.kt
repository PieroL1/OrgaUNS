package com.example.orgauns.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================
// ESQUEMA DE COLORES MODO CLARO
// ============================================
private val LightColorScheme = lightColorScheme(
    // Primarios
    primary = Green500,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = Color(0xFF1B5E20),

    // Secundarios
    secondary = Teal500,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = Color(0xFF004D40),

    // Terciarios
    tertiary = Lime600,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = Color(0xFF33691E),

    // Error
    error = ErrorLight,
    onError = Color.White,
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFFB71C1C),

    // Fondos
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1B1B1B),

    // Superficies
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,

    // Outline
    outline = OutlineLight,
    outlineVariant = Color(0xFFE0E0E0)
)

// ============================================
// ESQUEMA DE COLORES MODO OSCURO
// ============================================
private val DarkColorScheme = darkColorScheme(
    // Primarios
    primary = GreenDark500,
    onPrimary = Color(0xFF1B3A1B),
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = Color(0xFFC8E6C9),

    // Secundarios
    secondary = TealDark500,
    onSecondary = Color(0xFF1B2F2C),
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = Color(0xFFB2DFDB),

    // Terciarios
    tertiary = LimeDark600,
    onTertiary = Color(0xFF2C3A1B),
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = Color(0xFFDCEDC8),

    // Error
    error = ErrorDark,
    onError = Color(0xFF3E1414),
    errorContainer = Color(0xFF8B1B1B),
    onErrorContainer = Color(0xFFFFCDD2),

    // Fondos
    background = Color(0xFF1B1B1B),
    onBackground = Color(0xFFE6E1E5),

    // Superficies
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,

    // Outline
    outline = OutlineDark,
    outlineVariant = Color(0xFF2E2E2E)
)

// ============================================
// TEMA PRINCIPAL DE ORGAUNS
// ============================================
@Composable
fun OrgaUNSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Deshabilitado para mantener paleta verde fija
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

