package com.example.lesxi.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lesxi.ConfirmationScreen
import com.example.lesxi.ReserveTableScreen
import com.example.lesxi.ShowMenuItems
import com.example.lesxi.data.model.ReservationDetails
import com.example.lesxi.data.model.Routes
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson


@SuppressLint("NewApi")
@Composable
fun ReserveNavigation() {
    val navController = rememberNavController()
    val currentUser = FirebaseAuth.getInstance().currentUser

    NavHost(navController = navController, startDestination = Routes.reserveDetails) {
        composable(Routes.reserveDetails) {
            if (currentUser != null) {
                ReserveTableScreen(navController, currentUser)
            }
        }
        composable(
            route = Routes.showMeals+"/{day}/{reservationDetails}",
            arguments = listOf(
                navArgument("day") { type = NavType.StringType },
                navArgument("reservationDetails") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val day = backStackEntry.arguments?.getString("day")
            val json = backStackEntry.arguments?.getString("reservationDetails")
            val reservationDetails = Gson().fromJson(json, ReservationDetails::class.java)

            if (day != null) {
                ShowMenuItems(day = day, navController, reservationDetails)
            }
        }

        composable(
            route = Routes.finishReservation+"/{reservationDetails}/{itemsList}",
            arguments = listOf(
                navArgument("reservationDetails") { type = NavType.StringType },
                navArgument("itemsList") { type = NavType.StringType }
            )

        ) { backStackEntry ->
            val reservationJson = backStackEntry.arguments?.getString("reservationDetails")
            val itemsJson = backStackEntry.arguments?.getString("itemsList")

            val reservationDetails = Gson().fromJson(reservationJson, ReservationDetails::class.java)
            val itemsList = Gson().fromJson<List<String>>(itemsJson, object : TypeToken<List<String>>() {}.type)

            ConfirmationScreen(
                navController, reservationDetails, itemsList
            )
            
        }
    }
}