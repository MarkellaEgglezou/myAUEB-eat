package com.example.lesxi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.lesxi.ui.theme.Bordeaux
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

data class MenuItem(
    val title: String = "",
    val description: String = ""
)

@Composable
fun MenuLesxi() {
    val db = FirebaseFirestore.getInstance()


    var items by remember { mutableStateOf<List<MenuItem>>(emptyList()) }


    LaunchedEffect(Unit) {

        db.collection("menu")
            .get()
            .addOnSuccessListener { snapshot ->
                items = snapshot.documents.mapNotNull { it.toObject<MenuItem>() }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Menu",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
        )


        items.forEach { item ->
            MenuItems(item)
            Spacer(modifier = Modifier.height(20.dp))
        }

    }

}



@Composable
fun MenuItems(item: MenuItem) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF762525),
        ),
        modifier = Modifier
            .size(width = 340.dp, height = 100.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Spacer(modifier = Modifier.width(20.dp))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "see more")
        }
        Text(
            text = item.description,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )


    }
}

