package com.example.project.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.ui.theme.LightGrassGreen

@Composable
fun DropdownList(
    itemList: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = if (selectedIndex >= 0) itemList[selectedIndex] else "Select a team member"

    Box(modifier = modifier) {
        TextButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF6F6F6), RoundedCornerShape(8.dp)) // Background color
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(3.dp)
        ) {
            Text(
                text = selectedItem,
                fontSize = 16.sp,
                color = if (selectedIndex >= 0) Color.Black else Color(0xFFBDBDBD), // Text color based on selection
                modifier = Modifier.weight(1f) // Ensure text takes up available space
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown Arrow",
                tint = LightGrassGreen, // Arrow color
                modifier = Modifier.size(34.dp) // Make the arrow slightly larger
            )
        }

        // Animated visibility for dropdown list
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 300))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Limit the height to show only four rows
                    .background(Color(0xFFF6F6F6)) // Background color of the dropdown list
                    .border(1.dp, Color.Gray)
                    .clip(RoundedCornerShape(8.dp)) // Rounded corners
            ) {
                items(itemList.size) { index ->
                    if (index != 0) {
                        Divider(thickness = 1.dp, color = Color.LightGray)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemClick(index)
                                expanded = false
                            }
                            .padding(vertical = 8.dp) // Increased padding between items
                            .background(Color(0xFFF6F6F6)) // Background color for each item
                    ) {
                        Text(
                            text = itemList[index],
                            color = if (index == selectedIndex) Color.Black else Color(0xFFBDBDBD), // Black color for selected item
                            modifier = Modifier.padding(horizontal = 16.dp) // Padding inside each item
                        )
                    }
                }
            }
        }
    }
}