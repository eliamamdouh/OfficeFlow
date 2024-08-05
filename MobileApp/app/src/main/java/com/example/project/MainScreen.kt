package com.example.project

import ManagerRequests
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var userRole by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            // Replace this with your actual API call
            val roleFromApi = fetchUserRoleFromApi()
            userRole = roleFromApi
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != "splash") {
                if (currentRoute != "page0") {
                    when (userRole) {
                        "employee" -> BottomNavBar(navController)
                        "manager" -> ManagerNavBar(navController)
                        else -> {} // You can handle other cases or leave it empty
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("page0") { LoginScreen(navController) }
            composable("page1") { HomeScreen() }
            composable("page2") { ChatScreen() }
            composable("page3") { MyRequests() }
            composable("page4") { NotificationPage() }
            composable("page5") { ManagerRequests() }
            composable("page6") { ScheduleScreen()}
        }
    }
}

// Dummy function to simulate fetching user role from API
suspend fun fetchUserRoleFromApi(): String {
    // Replace with actual API call logic
    // For example:
    // val response = apiService.getUserRole()
    // return response.role

    // Simulated delay for API call
    kotlinx.coroutines.delay(1000)
    return "manager" // or "manager"
}
