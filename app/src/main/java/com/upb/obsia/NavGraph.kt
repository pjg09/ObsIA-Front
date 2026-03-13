package com.upb.obsia

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.upb.obsia.ui.screens.ChatListScreen
import com.upb.obsia.ui.screens.ChatListTapScreen
import com.upb.obsia.ui.screens.ChatPageScreen
import com.upb.obsia.ui.screens.ChatScreen
import com.upb.obsia.ui.screens.ChatScreenTypingScreen
import com.upb.obsia.ui.screens.ContactsScreen
import com.upb.obsia.ui.screens.EditProfileScreen
import com.upb.obsia.ui.screens.LogOutScreen
import com.upb.obsia.ui.screens.LoginDetailsScreen
import com.upb.obsia.ui.screens.LoginScreen
import com.upb.obsia.ui.screens.OnboardingScreen
import com.upb.obsia.ui.screens.RegisterScreen
import com.upb.obsia.ui.screens.SettingsScreen
import com.upb.obsia.ui.screens.SplashScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavRoutes.SPLASH) {
        composable(NavRoutes.SPLASH) { SplashScreen(navController) }
        composable(NavRoutes.ONBOARDING) { OnboardingScreen(navController) }
        composable(NavRoutes.REGISTER) { RegisterScreen(navController) }
        composable(NavRoutes.LOGIN) { LoginScreen(navController) }
        composable(NavRoutes.LOGIN_DETAILS) { LoginDetailsScreen(navController) }
        composable(NavRoutes.CHAT_PAGE) { ChatPageScreen(navController) }
        composable(NavRoutes.CHAT_LIST) { ChatListScreen(navController) }
        composable(NavRoutes.CHAT_LIST_TAP) { ChatListTapScreen(navController) }
        composable(NavRoutes.CHAT_SCREEN) { ChatScreen(navController) }
        composable(NavRoutes.CHAT_SCREEN_TYPING) { ChatScreenTypingScreen(navController) }
        composable(NavRoutes.EDIT_PROFILE) { EditProfileScreen(navController) }
        composable(NavRoutes.CONTACTS) { ContactsScreen(navController) }
        composable(NavRoutes.SETTINGS) { SettingsScreen(navController) }
        composable(NavRoutes.LOG_OUT) { LogOutScreen(navController) }
    }
}
