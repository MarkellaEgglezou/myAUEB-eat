package com.example.lesxi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


@Composable
fun MenuItemDetailsScreen(itemID: String) {

    val db = FirebaseFirestore.getInstance()


    var item by remember { mutableStateOf<MenuItem?>(null) }

    LaunchedEffect(itemID) {
        // Query the database for a MenuItem with the specified ID
        db.collection("menu")
            .document(itemID) // Assuming itemId is the document ID in Firestore
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Create a MenuItem object from the fetched data
                    item = documentSnapshot.toObject(MenuItem::class.java)
                } else {
                    println("Document not found")
                }
            }
            .addOnFailureListener { exception ->
                println("Error fetching document: $exception")
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = item?.title ?: "No Title",
            fontSize = 24.sp,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
        )

        item?.description?.let {
            Text(
                text = it,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp, top = 40.dp)
            )
        }
        Text(
            text = "Αλλεργιογόνα",
            fontSize = 14.sp,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
        )
        Text(
            text = "Αλλεργιογόνα.....",
            fontSize = 14.sp,
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
        )



    }
}

