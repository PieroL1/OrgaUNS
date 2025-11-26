package com.example.orgauns.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.orgauns.data.repository.AuthRepositoryImpl
import com.example.orgauns.presentation.auth.LoginScreen
import com.example.orgauns.presentation.auth.RegisterScreen
import com.example.orgauns.presentation.main.MainScreen
import com.example.orgauns.presentation.splash.SplashScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    authRepository: AuthRepositoryImpl
) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash.route // Iniciar siempre en Splash
    ) {
        // Splash Screen (pantalla inicial)
        composable(Route.Splash.route) {
            SplashScreen(
                authRepository = authRepository,
                onNavigateToLogin = {
                    navController.navigate(Route.Auth.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Route.Main.route) {
                        popUpTo(Route.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Auth Flow
        navigation(
            startDestination = Route.Login.route,
            route = Route.Auth.route
        ) {
            composable(Route.Login.route) {
                LoginScreen(
                    onNavigateToRegister = {
                        navController.navigate(Route.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(Route.Main.route) {
                            popUpTo(Route.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Route.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(Route.Main.route) {
                            popUpTo(Route.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        // Main Flow (con BottomBar)
        composable(Route.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Route.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

