// Ruta: app/src/main/java/com/upb/obsia/NavGraph.kt

package com.upb.obsia

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.upb.obsia.ui.screens.ChatPageScreen
import com.upb.obsia.ui.screens.ChatScreen
import com.upb.obsia.ui.screens.EditProfileScreen
import com.upb.obsia.ui.screens.LogOutScreen
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
        composable(NavRoutes.CHAT_PAGE) { ChatPageScreen(navController) }
        composable(NavRoutes.EDIT_PROFILE) { EditProfileScreen(navController) }
        composable(NavRoutes.SETTINGS) { SettingsScreen(navController) }
        composable(NavRoutes.LOG_OUT) { LogOutScreen(navController) }

        // Ruta del chat con parámetros: /chat_screen/{userId}/{sessionId}/{sessionName}
        composable(
                route = "${NavRoutes.CHAT_SCREEN}/{userId}/{sessionId}/{sessionName}",
                arguments =
                        listOf(
                                navArgument("userId") { type = NavType.IntType },
                                navArgument("sessionId") { type = NavType.IntType },
                                navArgument("sessionName") { type = NavType.StringType }
                        )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
            val sessionName = backStackEntry.arguments?.getString("sessionName") ?: "Chat"
            ChatScreen(
                    sessionName = sessionName,
                    userId = userId,
                    sessionId = sessionId,
                    onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
