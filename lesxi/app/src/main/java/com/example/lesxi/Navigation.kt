package com.example.lesxi

// Navigation.kt
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable



// Setup your Bottom Navigation Bar
@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = Color(0xFF6200EE),
        contentColor = Color.White
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = false,
            onClick = { navController.navigate("search") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { navController.navigate("profile") }
        )
    }
}


// Setup Navigation Graph (NavHost) with your different screens
@Composable
fun NavigationGraph(navController: NavController, modifier: Modifier = Modifier) {
    // Ensure startDestination is a String and pass the NavController properly
    NavHost(
        navController = navController,    // Correct NavController
        startDestination = "home",         // The initial screen's route
        modifier = modifier                // Modifier to apply if needed
    ) {
        composable("home") { HomeScreen() }
        composable("search") { SearchScreen() }
        composable("profile") { ProfileScreen() }
    }
}


// Screen Composables for each route
@Composable
fun HomeScreen() {
    ScreenContent("Home Screen")
}

@Composable
fun SearchScreen() {
    ScreenContent("Search Screen")
}

@Composable
fun ProfileScreen() {
    ScreenContent("Profile Screen")
}

@Composable
fun ScreenContent(screenName: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = screenName, style = MaterialTheme.typography.headlineLarge)
    }
}
