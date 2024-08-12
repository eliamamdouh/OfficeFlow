package com.example.project

import ManagerRequests
import android.annotation.SuppressLint
import android.window.SplashScreen
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
import androidx.compose.ui.platform.LocalContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var userRole by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            // Replace this with your actual API call
            val roleFromApi = PreferencesManager.getUserRoleFromPreferences(context)
            println(roleFromApi)
            userRole = roleFromApi
            println(userRole)
        }
    }


    Scaffold(
        bottomBar = {
            if (currentRoute != "splash") {
                if (currentRoute != "page0") {
                    when (userRole) {
                        "Employee" -> BottomNavBar(navController)
                        "Manager" -> ManagerNavBar(navController)
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

            composable("page1") {HomeScreen(context) }

            composable("page2") { ChatScreen() }
            composable("page3") { MyRequests() }
            composable("page4") { NotificationPage() }
            composable("page5") { ManagerRequests() }
            composable("page6") { ScheduleScreen(context)}
        }
    }
}

// Dummy function to simulate fetching user role from API