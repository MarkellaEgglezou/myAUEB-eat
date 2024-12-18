package com.example.lesxi

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.oAuthProvider
import com.google.firebase.firestore.FirebaseFirestore



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRegisterScreen() {
    var isLoginMode by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
//    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()



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


        Text(
            text = if (isLoginMode) "Login" else "Register",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Toggle between Login and Register
        TextButton(
            onClick = {
                isLoginMode = !isLoginMode
            }
        ) {
            Text(text = if (isLoginMode) "Don't have an account? Register" else "Already have an account? Login")
        }

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
                visualTransformation = PasswordVisualTransformation()
            )


            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoginMode) {
            // Login button
            LoginButton(username = username, password = password)
        } else {
            // Register button
            RegisterButton(email=username, password = password, confirm = confirmPassword)
        }
        Spacer(modifier = Modifier.height(16.dp))


    }
}

@Composable
fun LoginButton(modifier: Modifier = Modifier, username: String, password: String) {
    val context = LocalContext.current
    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.material3.Button(onClick = {
            loginUser(username, password, appContext = context)
        }, colors = buttonColors(Color(0xFF762525)))
        {
            Text("Login")
        }
    }
}



@Composable
fun RegisterButton(modifier: Modifier = Modifier, email: String, password: String,confirm: String) {
    val context = LocalContext.current
    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Button(onClick = {
            if (password == confirm) {
                registerUser(email = email, password = password, appContext = context)
            }
        } ){
            Text("Register")
        }
    }
}

private fun loginUser(email: String, password: String, appContext: Context) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(appContext, MainActivity::class.java)
                appContext.startActivity(intent)

                if (appContext is Activity) {
                    appContext.finish()  // Close the current activity
                }

                Toast.makeText(appContext, "User Login In Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(appContext, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}


private fun registerUser(email: String, password: String, appContext: Context) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Optionally add user data to Firestore
                val user = hashMapOf(
                    "email" to email,
                    "user_id" to FirebaseAuth.getInstance().currentUser?.uid
                )

                // Add data to Firestore
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                    .set(user)
                    .addOnSuccessListener {
                        // Registration and Firestore save successful
                        val intent = Intent(appContext, MainActivity::class.java)
                        appContext.startActivity(intent)

                        if (appContext is Activity) {
                            appContext.finish()  // Close the current activity
                        }

                        Toast.makeText(appContext, "User Registered Successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        // Firestore save failed
                        Toast.makeText(appContext, "Error saving user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Registration failed
                Toast.makeText(appContext, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

