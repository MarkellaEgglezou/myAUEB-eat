package com.example.lesxi.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lesxi.view.Form
import com.example.lesxi.view.user.LoginRegisterScreen
import com.example.lesxi.view.ProfileScreen
import com.example.lesxi.data.model.Routes
import com.google.firebase.auth.FirebaseAuth

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFF762525),
        contentColor = Color.White,
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {


        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White) },
            selected = false,
            onClick = { navController.navigate(Routes.main_page) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add Reservation", tint = Color.White) },
            selected = false,
            onClick = { navController.navigate(Routes.reservation_page) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.BookmarkAdd, contentDescription = "Complaint Form", tint = Color.White) },
            selected = false,
            onClick = { navController.navigate(Routes.form) }
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)},
            selected = false,
            onClick = { navController.navigate(Routes.user) }
        )
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) Routes.login else Routes.main_page,
        modifier = modifier
    ) {
        composable(Routes.login) { LoginRegisterScreen(navController) }
        composable(Routes.main_page) { MenuNavigation() }
        composable(Routes.reservation_page) { ReserveNavigation() }
        composable(Routes.form) {
            FirebaseAuth.getInstance().currentUser?.let {
                if (currentUser != null) {
                    Form(currentUser)
                } else {
                    androidx.compose.material3.Text(
                        "User not found",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        composable(Routes.user) {
            FirebaseAuth.getInstance().currentUser?.let {
                if (currentUser != null) {
                    ProfileScreen(currentUser)
                } else {
                    androidx.compose.material3.Text(
                        "User not found",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
