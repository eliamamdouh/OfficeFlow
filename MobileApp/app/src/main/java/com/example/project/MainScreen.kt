//package com.example.project
//
//import android.util.Log
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//
//@Composable
//fun MainScreen(apiService: ApiService, userRole: String) {
//    val navController = rememberNavController()
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    Scaffold(
//        bottomBar = {
//            if (currentRoute != "splash" && currentRoute != "page0") {
//                when (userRole) {
//                    "Employee" -> BottomNavBar(navController)
//                    "Manager" -> ManagerNavBar(navController)
//                    else -> {}
//                }
//            }
//        }
//    ) { innerPadding ->
//        AppNavigation(
//            navController = navController,
//            modifier = Modifier.padding(innerPadding)
//        )
//    }
//}
