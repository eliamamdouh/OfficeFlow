package com.example.project

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.project.BottomNavBar
import com.example.project.ChatScreen
import com.example.project.HomeScreen
import com.example.project.LoginScreen
import com.example.project.MyRequests
import com.example.project.NotificationPage


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "page0") {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "page0",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("page0") { LoginScreen(navController) }
            composable("page1/{userId}") { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                HomeScreen(userId = userId)
            }
            composable("page2") { ChatScreen() }
            composable("page3") { MyRequests() }
            composable("page4") { NotificationPage() }
        }
    }
}

