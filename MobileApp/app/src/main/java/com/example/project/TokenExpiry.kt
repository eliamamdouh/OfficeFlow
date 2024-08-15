// File: AuthUtils.kt
package com.example.project

import androidx.navigation.NavController

fun handleTokenExpiration(navController: NavController) {
    navController.navigate("page0") {
        popUpTo("page0") { inclusive = true }
    }
}
