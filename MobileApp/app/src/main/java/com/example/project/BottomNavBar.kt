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

data class NavBarItem(
    val route: String,
    val iconId: Int,
    val selectedIconId: Int
)

@Composable
fun BottomNavBar(navController: NavHostController) {
    val navItems = listOf(
        NavBarItem("page1", R.drawable.graynotes, R.drawable.greennotes),
        NavBarItem("page2", R.drawable.graycomment, R.drawable.greencomment),
        NavBarItem("page3", R.drawable.graymessagealertsquare, R.drawable.greenmessagealertsquare),
        NavBarItem("page4", R.drawable.graybell, R.drawable.greenbell)
    )

    CommonNavBar(navController = navController, navItems = navItems)
}

@Composable
fun ManagerNavBar(navController: NavHostController) {
    val navItems = listOf(
        NavBarItem("page1", R.drawable.graynotes, R.drawable.greennotes),
        NavBarItem("page2", R.drawable.graycomment, R.drawable.greencomment),
        NavBarItem("page6", R.drawable.mgrgray, R.drawable.mgrgreen),
        NavBarItem("page5", R.drawable.graymessagealertsquare, R.drawable.greenmessagealertsquare),
        NavBarItem("page4", R.drawable.graybell, R.drawable.greenbell)
    )

    CommonNavBar(navController = navController, navItems = navItems)
}

@Composable
fun SuperManagerNavBar(navController: NavHostController) {
    val navItems = listOf(
        NavBarItem("page1", R.drawable.graynotes, R.drawable.greennotes),
        NavBarItem("page2", R.drawable.graycomment, R.drawable.greencomment),
        NavBarItem("page6", R.drawable.mgrgray, R.drawable.mgrgreen),
        NavBarItem("page5", R.drawable.graymessagealertsquare, R.drawable.greenmessagealertsquare),
        NavBarItem("page4", R.drawable.graybell, R.drawable.greenbell),
        NavBarItem("page7", R.drawable.analyticsgray, R.drawable.analyticsgreen)
    )

    CommonNavBar(navController = navController, navItems = navItems)
}


@Composable
fun CommonNavBar(navController: NavHostController, navItems: List<NavBarItem>) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

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
                        selectedIconId = item.selectedIconId,
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
    selectedIconId: Int,
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
            painter = painterResource(id = if (isSelected) selectedIconId else iconId),
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
