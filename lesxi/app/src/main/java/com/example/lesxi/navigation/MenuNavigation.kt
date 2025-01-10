package com.example.lesxi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lesxi.MenuItemDetailsScreen
import com.example.lesxi.MenuLesxi
import com.example.lesxi.data.model.Routes

@Composable
fun MenuNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.main_page) {
        composable(Routes.main_page) {
            MenuLesxi(navController = navController)
        }
        composable(Routes.menuItemDetails+ "/{itemID}") { backStackEntry ->
            val itemID = backStackEntry.arguments?.getString("itemID") ?: ""
            MenuItemDetailsScreen(itemID)
        }
    }
}