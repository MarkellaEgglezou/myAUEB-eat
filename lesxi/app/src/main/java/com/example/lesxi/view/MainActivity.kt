package com.example.lesxi.view


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.lesxi.navigation.BottomNavigationBar
import com.example.lesxi.navigation.NavigationGraph
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val isUserLoggedIn = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

            Scaffold(
                bottomBar = {
                    if (isUserLoggedIn.value) {
                        BottomNavigationBar(navController)
                    }
                }
            ) { innerPadding ->
                NavigationGraph(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding),
                    onLoginStatusChanged = { isLoggedIn ->
                        isUserLoggedIn.value = isLoggedIn
                    }
                )
            }

        }

    }

}
