package com.example.lesxi


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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

        // Create a collection and add some data
        addDataToFirestore()
    }

    private fun addDataToFirestore() {
        // Reference to a collection and document
        val user = hashMapOf(
            "first_name" to "John",
            "last_name" to "Doe",
            "age" to 30
        )

        // Add data to Firestore with a specific collection and document ID
        firestore.collection("users")
            .document("user_id_1") // You can set a custom document ID here
            .set(user, SetOptions.merge()) // Merge if you want to preserve existing data
            .addOnSuccessListener {
                // Success listener
                println("Document successfully written!")
            }
            .addOnFailureListener { exception ->
                // Failure listener
                println("Error writing document: $exception")
            }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginRegisterScreen()
}