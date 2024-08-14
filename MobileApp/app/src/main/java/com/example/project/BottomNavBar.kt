package com.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val navItems = listOf(
        NavItem("page1", R.drawable.graynotes, R.drawable.greennotes),
        NavItem("page2", R.drawable.graycomment, R.drawable.greencomment),
        NavItem("page3", R.drawable.graymessagealertsquare, R.drawable.greenmessagealertsquare),
        NavItem("page4", R.drawable.graybell, R.drawable.greenbell)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8))
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                navItems.forEach { item ->
                    NavBarIcon(
                        iconId = item.iconId,
                        greenIconId = item.greenIconId,
                        isSelected = currentRoute == item.route,
                        onClick = { navController.navigate(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
fun NavBarIcon(
    iconId: Int,
    greenIconId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconSize = if (isSelected) 32.dp else 28.dp
    val spacerHeight = if (isSelected) 2.dp else 10.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = if (isSelected) greenIconId else iconId),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(spacerHeight))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF86BC24))
            )
        }
    }
}

data class NavItem(
    val route: String,
    val iconId: Int,
    val greenIconId: Int
)
