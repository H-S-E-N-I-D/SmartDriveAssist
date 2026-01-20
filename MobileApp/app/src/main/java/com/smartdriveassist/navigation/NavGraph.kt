package com.smartdriveassist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.smartdriveassist.screens.*

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController  = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                navController = navController,
                onReportAccident = { navController.navigate(Screen.ReportAccident.route) }
            )
        }

        composable(Screen.Accidents.route) {
            AccidentsScreen(navController = navController)
        }

        composable(Screen.Navigate.route) {
            NavigateScreen(navController = navController)
        }

        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(Screen.ReportAccident.route) {
            ReportAccidentScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
