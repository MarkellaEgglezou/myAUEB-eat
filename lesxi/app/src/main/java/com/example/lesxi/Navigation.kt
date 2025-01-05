package com.example.lesxi

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
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
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = false,
            onClick = { navController.navigate(Routes.main_page) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Menu") },
            selected = false,
            onClick = { navController.navigate(Routes.reservation_page) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Call, contentDescription = "Forms") },
            selected = false,
            onClick = { navController.navigate(Routes.form) }
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
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
        composable(Routes.reservation_page) { ReserveTableScreen() }
        composable(Routes.form) {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                Form(user)
            } else {
                Text(
                    "You need to login first",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        composable(Routes.user) { FirebaseAuth.getInstance().currentUser?.let {
            if (currentUser != null) {
                ProfileScreen(currentUser)
            }
        } }
        composable(Routes.random) { DisplayUserDetailsWithAttributes() }

    }
}
