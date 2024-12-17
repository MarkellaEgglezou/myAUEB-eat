package com.example.lesxi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen() {
    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        content = { padding ->
            // Pass the padding to NavigationGraph to ensure proper layout
            NavigationGraph(navController = navController, modifier = Modifier.padding(padding))
        }
    )
    // Toggle button to switch between Login and Register
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.aueb_logo), // Replace with your image resource
            contentDescription = "Sample Image", // Provide a description for accessibility
            modifier = Modifier
                .height(150.dp) // Set height for the image
                .fillMaxWidth() // Optionally fill the width of the parent container
        )

        // Title based on Login/Register mode
        Text(
            text = if (isLoginMode) "Login" else "Register",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Username/Email text field
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username or Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    // handle the next action
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password text field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // handle the done action
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // For Register mode, show the Confirm Password field
        if (!isLoginMode) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // handle the done action
                    }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Toggle between Login and Register
        TextButton(
            onClick = {
                isLoginMode = !isLoginMode
            }
        ) {
            Text(text = if (isLoginMode) "Don't have an account? Register" else "Already have an account? Login")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LoginRegisterScreen()
}