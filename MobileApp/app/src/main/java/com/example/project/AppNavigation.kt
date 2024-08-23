package com.example.project

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation(apiService: ApiService, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // State to remember which userRole is selected
    var userRole by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            if (currentRoute != "splash" && currentRoute != "page0") {
                when (userRole) {
                    "Employee" -> BottomNavBar(navController)
                    "Manager" -> ManagerNavBar(navController)
                    "SuperManager" -> SuperManagerNavBar(navController)
                    else -> {}
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = modifier.padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("page0") {
                LoginScreen(navController) { role ->
                    userRole = role
                    Log.d("NAVIGATION" , userRole)
                    navController.navigate("page1") {
                        popUpTo("page0") { inclusive = true }
                    }
                }
            }
            composable("page1") { HomeScreen(navController.context, navController) }
            composable("page2") { ChatScreen(navController) }
            composable("page3") { MyRequests(navController.context, navController) }
            composable("page4") { NotificationPage(navController) }
            composable("page5") { ManagerRequests(navController.context) }
            composable("page6") { ScheduleScreen(navController.context, navController) }
            composable("page7") { AnalyticsScreen(navController.context, navController)}//give navcontroller.
        }
    }
}
