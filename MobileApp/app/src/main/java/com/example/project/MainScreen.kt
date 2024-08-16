package com.example.project

import android.annotation.SuppressLint
import android.util.Log
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
import kotlinx.coroutines.delay

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(apiService: ApiService) {  // Accept apiService as a parameter
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var userRole by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val roleFromApi = PreferencesManager.getUserRoleFromPreferences(context)
            userRole = roleFromApi
            userRole?.let { Log.d("MAINSCREEN", it) }
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != "splash" && currentRoute != "page0") {
                when (userRole) {
                    "Employee" -> BottomNavBar(navController)
                    "Manager" -> ManagerNavBar(navController)
                    else -> {}
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
            composable("page1") { HomeScreen(context) }
            composable("page2") { ChatScreen() }
            composable("page3") { MyRequests(context) }
            composable("page4") { NotificationPage() }
            composable("page5") { ManagerRequests(context) }
            composable("page6") { ScheduleScreen() }
        }
    }
}
