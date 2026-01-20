package com.smartdriveassist.navigation

sealed class Screen(val route: String) {
    object Login          : Screen("login")
    object Register       : Screen("register")
    object Dashboard      : Screen("dashboard")
    object Accidents      : Screen("accidents")
    object Navigate       : Screen("navigate")
    object Settings       : Screen("settings")
    object ReportAccident : Screen("report_accident")
}
