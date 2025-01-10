package com.example.lesxi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lesxi.data.model.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginRegisterScreen(navController: NavHostController) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var surname by remember { mutableStateOf("") }
    var am by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (isLoginMode) {
            Text(
                text = "Login",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Left,
                color = Color(0xFF762525)
            )

            email = genericTextField("Email Address")
            Spacer(modifier = Modifier.height(16.dp))

            password = passwordTextField("Password")
            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!isLoginMode) {
            Text(
                text = "Register",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Left,
                color = Color(0xFF762525)
            )
            email = genericTextField("Email Address")

            Spacer(modifier = Modifier.height(16.dp))
            am = genericTextField("AM")
            Spacer(modifier = Modifier.height(16.dp))
            name = genericTextField("First Name")
            Spacer(modifier = Modifier.height(16.dp))
            surname = genericTextField("Last Name")
            Spacer(modifier = Modifier.height(16.dp))
            password = passwordTextField("Password")
            Spacer(modifier = Modifier.height(16.dp))
            confirmPassword = passwordTextField("Confirm Password")

            Spacer(modifier = Modifier.height(16.dp))
        }
        TextButton(
            onClick = {
                isLoginMode = !isLoginMode
            }
        ) {
            Text(text =
            if (isLoginMode)
                "Don't have an account? Register"
            else
                "Already have an account? Login",
                color = Color(0xFF762525))
        }
        if (isLoginMode) {
            LoginButton(email = email, password = password, navController = navController)
        } else {
            RegisterButton(
                email = email,
                name = name,
                surname = surname,
                am = am,
                password = password,
                confirm = confirmPassword
            )
        }

    }

}

@Composable
fun genericTextField(fieldType: String): String {
    var typeTextField by remember { mutableStateOf("") }
    OutlinedTextField(
        value = typeTextField,
        onValueChange = { typeTextField = it },
        label = { Text(fieldType) },
        placeholder = { Text(fieldType) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null)},
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Next
        )
    )
    return typeTextField
}

@Composable
fun passwordTextField(fieldType: String): String {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text(fieldType) },
        placeholder = { Text(fieldType) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            Icon(
                imageVector = icon,
                contentDescription =
                if (passwordVisible)
                    "Hide password"
                else
                    "Show password",
                modifier = Modifier.clickable { passwordVisible = !passwordVisible }
            )
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
    return password
}
@Composable
fun LoginButton(modifier: Modifier = Modifier, email: String, password: String, navController: NavHostController) {
    val context = LocalContext.current
    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        androidx.compose.material3.Button(onClick = {
            loginUser(email, password, appContext = context, navController = navController)
        }, colors = buttonColors(Color(0xFF762525)))
        {
            Text("Login")
        }
    }
}



@Composable
fun RegisterButton(modifier: Modifier = Modifier, email: String, password: String,confirm: String,
                   name: String, surname: String, am:String) {
    val context = LocalContext.current
    Column (
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.material3.Button(onClick = {
            if (password == confirm) {
                registerUser(email = email, name=name, surname=surname, am=am, password = password, appContext = context)
            }
        }, colors = buttonColors(Color(0xFF762525)) ){
            Text("Register")
        }
    }
}

private fun loginUser(email: String, password: String, appContext: Context, navController: NavHostController) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                navController.navigate(Routes.main_page)

                Toast.makeText(appContext, "User Login In Successfully", Toast.LENGTH_SHORT).show()


            } else {
                Toast.makeText(appContext, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}

private fun registerUser(email: String, name: String, surname: String, am: String, password: String, appContext: Context) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val user = hashMapOf(
                    "email" to email,
                    "user_id" to FirebaseAuth.getInstance().currentUser?.uid,
                    "am" to am,
                    "name" to name,
                    "surname" to surname,
                    "avatar_photo" to null
                )

                // Add data to Firestore
                FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                    .set(user)
                    .addOnSuccessListener {
                        val intent = Intent(appContext, MainActivity::class.java)
                        appContext.startActivity(intent)


                        Toast.makeText(appContext, "User Registered Successfully!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(appContext, "Error saving user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Registration failed
                Toast.makeText(appContext, "Registration Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
