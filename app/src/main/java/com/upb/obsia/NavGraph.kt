package com.upb.obsia

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.upb.obsia.ui.screens.SplashScreen
import com.upb.obsia.ui.screens.OnboardingScreen
import com.upb.obsia.ui.screens.RegisterScreen
import com.upb.obsia.ui.screens.LoginScreen
import com.upb.obsia.ui.screens.ChatListScreen
import com.upb.obsia.ui.screens.ChatScreen
import com.upb.obsia.ui.screens.ContactsScreen
import com.upb.obsia.ui.screens.SettingsScreen
import com.upb.obsia.ui.screens.EditProfileScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(navController)
        }
        composable(NavRoutes.ONBOARDING) {
            OnboardingScreen(navController)
        }
        composable(NavRoutes.REGISTER) {
            RegisterScreen(navController)
        }
        composable(NavRoutes.LOGIN) {
            LoginScreen(navController)
        }
        composable(NavRoutes.CHAT_LIST) {
            ChatListScreen(navController)
        }
        composable(NavRoutes.CHAT_SCREEN) {
            ChatScreen(navController)
        }
        composable(NavRoutes.CONTACTS) {
            ContactsScreen(navController)
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(navController)
        }
        composable(NavRoutes.EDIT_PROFILE) {
            EditProfileScreen(navController)
        }
    }
}