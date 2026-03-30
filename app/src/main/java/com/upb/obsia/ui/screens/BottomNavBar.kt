package com.upb.obsia.ui.screens

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.upb.obsia.NavRoutes
import com.upb.obsia.ui.theme.FondoPrincipal
import com.upb.obsia.ui.theme.LetrasNegras50

@Composable
fun BottomNavBar(navController: NavController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == NavRoutes.CHAT_PAGE,
            onClick = {
                navController.navigate(NavRoutes.CHAT_PAGE) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Chat,
                    contentDescription = "Chats",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Chats", fontSize = 13.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FondoPrincipal,
                selectedTextColor = FondoPrincipal,
                unselectedIconColor = LetrasNegras50,
                unselectedTextColor = LetrasNegras50,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            selected = currentRoute == NavRoutes.SETTINGS,
            onClick = {
                navController.navigate(NavRoutes.SETTINGS) {
                    launchSingleTop = true
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Configuración",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Configuración", fontSize = 13.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FondoPrincipal,
                selectedTextColor = FondoPrincipal,
                unselectedIconColor = LetrasNegras50,
                unselectedTextColor = LetrasNegras50,
                indicatorColor = Color.Transparent
            )
        )
    }
}