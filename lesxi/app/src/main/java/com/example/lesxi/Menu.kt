package com.example.lesxi


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

data class MenuItem(
    val itemID: String = " ",
    val title: String = "",
    val description: String = ""
)

@Composable
fun MenuNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "menu_items") {
        composable("menu_items") {
            MenuLesxi(navController = navController)
        }
        composable("menu_item_details/{item.itemID}") { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemID") ?: ""
            val item = MenuItem(itemId, "title", "description")
            MenuItemDetailsScreen(item.itemID)
        }
    }
}
@Composable
fun MenuLesxi(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    var items by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        db.collection("menu")
            .get()
            .addOnSuccessListener { snapshot ->
                items = snapshot.documents.mapNotNull { it.toObject<MenuItem>() }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                isLoading = false
            }
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Μενού",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 40.dp)
            )


            items.forEach { item ->
                MenuItems(item, navController = navController)
                Spacer(modifier = Modifier.height(20.dp))
            }

        }

    }

}



@Composable
fun MenuItems(item: MenuItem, navController: NavHostController) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF762525),
        ),
        modifier = Modifier
            .size(width = 340.dp, height = 100.dp),
        onClick = {
            navController.navigate("menu_item_details/${item.itemID}")
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "see more")
        }
        Text(
            text = item.itemID,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )



    }
}


