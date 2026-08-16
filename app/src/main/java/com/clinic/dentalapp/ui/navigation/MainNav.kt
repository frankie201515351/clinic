package com.clinic.dentalapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.clinic.dentalapp.data.ClinicRepository
import com.clinic.dentalapp.data.Session
import com.clinic.dentalapp.ui.screens.*

sealed class Screen(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Patients : Screen("patients", "المرضى", Icons.Default.People)
    object Appointments : Screen("appointments", "المواعيد", Icons.Default.CalendarMonth)
    object Finance : Screen("finance", "الحسابات", Icons.Default.AttachMoney)
    object Inventory : Screen("inventory", "المخزون", Icons.Default.Inventory)
    object Profit : Screen("profit", "الأرباح", Icons.Default.TrendingUp)
    object Settings : Screen("settings", "الإعدادات", Icons.Default.Settings)
}

@Composable
fun MainNav(repository: ClinicRepository, onLogout: () -> Unit) {
    val navController = rememberNavController()

    val bottomItems = if (Session.isAdmin) {
        listOf(Screen.Patients, Screen.Appointments, Screen.Finance, Screen.Inventory, Screen.Profit, Screen.Settings)
    } else {
        listOf(Screen.Patients, Screen.Appointments, Screen.Settings)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Patients.route,
            modifier = androidx.compose.ui.Modifier.padding(padding)
        ) {
            composable(Screen.Patients.route) {
                PatientsScreen(repository) { patientId ->
                    navController.navigate("patient_detail/$patientId")
                }
            }
            composable("patient_detail/{patientId}") { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")?.toLongOrNull() ?: 0
                PatientDetailScreen(repository, patientId) { navController.popBackStack() }
            }
            composable(Screen.Appointments.route) { AppointmentsScreen(repository) }
            if (Session.isAdmin) {
                composable(Screen.Finance.route) { FinanceScreen(repository) }
                composable(Screen.Inventory.route) { InventoryScreen(repository) }
                composable(Screen.Profit.route) { ProfitScreen(repository) }
            }
            composable(Screen.Settings.route) { SettingsScreen(repository, onLogout) }
        }
    }
}
