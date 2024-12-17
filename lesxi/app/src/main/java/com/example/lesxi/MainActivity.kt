package com.example.lesxi


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MainActivity : ComponentActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LoginRegisterScreen()
        }

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

    }


}

@Composable
fun AppContent() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavigationGraph(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginRegisterScreen()
}