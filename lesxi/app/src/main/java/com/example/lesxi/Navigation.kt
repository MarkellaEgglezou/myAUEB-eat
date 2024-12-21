package com.example.lesxi

// Navigation.kt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
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

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lesxi.ui.theme.LesxiTheme



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
            label = { R.string.nav_item_homepage },
            selected = false,
            onClick = { navController.navigate(Routes.main_page) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Menu") },
            label = { R.string.nav_item_menu },
            selected = false,
            onClick = { navController.navigate(Routes.reservation_page) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Call, contentDescription = "Forms") },
            label = { R.string.nav_item_reservations },
            selected = false,
            onClick = { navController.navigate(Routes.form) }
        )

        BottomNavigationItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { R.string.nav_item_profile },
            selected = false,
            onClick = { navController.navigate(Routes.user) }
        )
    }
}



@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable(Routes.main_page) { MenuNavigation() }
        composable(Routes.reservation_page) { ReserveTableScreen() }
        composable(Routes.form) { Form() }
        composable(Routes.user) { LoginRegisterScreen() }
    }
}




