package com.example.project

import androidx.navigation.NavController

fun handleTokenExpiration(navController: NavController) {
    navController.navigate("splash") {
        popUpTo("page0") { inclusive = true }
    }
}