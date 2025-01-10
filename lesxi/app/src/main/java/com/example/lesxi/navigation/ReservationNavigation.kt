package com.example.lesxi.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lesxi.ReserveTableScreen
import com.example.lesxi.ShowMenuItems
import com.example.lesxi.data.model.Routes


@SuppressLint("NewApi")
@Composable
fun ReserveNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.reserveDetails) {
        composable(Routes.reserveDetails) { ReserveTableScreen(navController) }
        composable(Routes.showMeals+"/{day}") { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day") ?: "UNKNOWN"

//            {date}/{time}/{people}
//            val date = backStackEntry.arguments?.getString("date") ?: "UNKNOWN"
//            val time = backStackEntry.arguments?.getString("time") ?: "UNKNOWN"
//            val people = backStackEntry.arguments?.getString("people") ?: "UNKNOWN"

            ShowMenuItems(day)
        }
        composable("confirmation/{items}/{date}/{time}/{people}") { backStackEntry ->
//            val date = backStackEntry.arguments?.getString("date") ?: "UNKNOWN"
//            val time = backStackEntry.arguments?.getString("time") ?: "UNKNOWN"
//            val people = backStackEntry.arguments?.getString("people") ?: "UNKNOWN"
//            val items = backStackEntry.arguments?.getStringArrayList("items")
//            ConfirmationScreen(date,time, people, items)

        }
    }
}