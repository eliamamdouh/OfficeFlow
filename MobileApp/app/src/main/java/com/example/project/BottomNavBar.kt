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
    //ask if it should be passed as a string.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8F8)) // Set the background color to blue
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = Color.White, // Set the navigation bar color to black
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
                NavBarIcon(
                    iconId = R.drawable.graynotes,
                    greenIconId = R.drawable.greennotes,
                    isSelected = currentRoute == "page1",
                    onClick = { navController.navigate("page1") }
                )
                NavBarIcon(
                    iconId = R.drawable.graycomment,
                    greenIconId = R.drawable.greencomment,
                    isSelected = currentRoute == "page2",
                    onClick = { navController.navigate("page2") }
                )
                NavBarIcon(
                    iconId = R.drawable.graymessagealertsquare,
                    greenIconId = R.drawable.greenmessagealertsquare,
                    isSelected = currentRoute == "page3",
                    onClick = { navController.navigate("page3") }
                )
                NavBarIcon(
                    iconId = R.drawable.graybell,
                    greenIconId = R.drawable.greenbell,
                    isSelected = currentRoute == "page4",
                    onClick = { navController.navigate("page4") }
                )
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        if (isSelected) {
            Image(
                painter = painterResource(id = greenIconId),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF86BC24))
            )
        } else {
            Image(
                painter = painterResource(id = iconId),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
