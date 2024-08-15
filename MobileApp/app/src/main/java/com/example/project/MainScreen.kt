package com.example.project

import ManagerRequests
import android.annotation.SuppressLint
import android.util.Log
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
import com.example.project.PreferencesManager.getTokenFromPreferences
//import isTokenExpired


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    var userRole by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val token = getTokenFromPreferences(context)
    Log.d("fein tokenn","token: $token")
     fun handleTokenExpiration() {
        // Redirect to login screen
        navController.navigate("page0") {
            popUpTo("page0") { inclusive = true }
        }
    }
    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val roleFromApi = PreferencesManager.getUserRoleFromPreferences(context)
            println(roleFromApi)
            userRole = roleFromApi
            println("test user role" + userRole)
          //   Check if the token is expired
            if (token.isNullOrEmpty()) {
                // Token is expired, redirect to login
                navController.navigate("page0") {
                    popUpTo("page0") { inclusive = true }
                }
            }
        }
    }
    Scaffold(
        bottomBar = {
            if (currentRoute != "splash") {
                if (currentRoute != "page0") {
                    when (userRole) {
                        "Employee" -> BottomNavBar(navController)
                        "Manager" -> ManagerNavBar(navController)
                        else -> {}
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

            composable("page1") {HomeScreen(context,navController) }

            composable("page2") { ChatScreen() }
            composable("page3") { MyRequests(context,navController) }
            composable("page4") { NotificationPage() }
            composable("page5") { ManagerRequests() }
            composable("page6") { ScheduleScreen()}
        }
    }
}
