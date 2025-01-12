package com.example.lesxi

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lesxi.ui.theme.LesxiTheme
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.example.lesxi.data.model.*
import com.example.lesxi.data.*
import com.example.lesxi.navigation.*

class ComplaintFormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LesxiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = FirebaseAuth.getInstance().currentUser
                     if (user != null) {
                        Form(user)
                    } else {
                        Text(
                            "You need to login first",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form(firebaseUser: FirebaseUser) {
    var am by remember { mutableStateOf<String?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(firebaseUser.uid) {
        fetchUser(firebaseUser.uid) { fetchedUser ->
            user = fetchedUser
            am = user?.am
        }
    }

    if (am == null) {
        Text("AM not found",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center)
        return
    }

    val complaintCategories = listOf(
        "Food Quality",
        "Service Issues",
        "Hygiene Concerns",
        "Other"
    )
    val selectedCategory = remember { mutableStateOf("") }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val complaint = remember { mutableStateOf("") }

    val selectedCategoryError = remember { mutableStateOf(false) }
    val complaintError = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF762525),
                    titleContentColor = Color.White
                ),
                title = {
                    Text(
                        "Menu",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(innerPadding)
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.complaint_category),
                    style = androidx.compose.material.MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.Start)
                )
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded.value,
                    onExpandedChange = { isDropdownExpanded.value = !isDropdownExpanded.value }
                ) {
                    TextField(
                        value = selectedCategory.value,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Choose a category") },
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth()
                            .menuAnchor(),
                        isError = selectedCategoryError.value
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded.value,
                        onDismissRequest = { isDropdownExpanded.value = false }
                    ) {
                        complaintCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory.value = category
                                    isDropdownExpanded.value = false
                                    selectedCategoryError.value = false
                                }
                            )
                        }
                    }
                }
                if (selectedCategoryError.value) {
                    Text(
                        text = "Please choose a complaint category",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = stringResource(R.string.complaint),
                    style = androidx.compose.material.MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 40.dp)
                        .align(Alignment.Start)
                )
                EditTextField(
                    value = complaint.value,
                    onValueChange = {
                        complaint.value = it
                        complaintError.value = it.isEmpty()
                    },
                    isError = complaintError.value,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )
                if (complaintError.value) {
                    Text(
                        text = "Please fill in your complaint",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
                val context = LocalContext.current
                SubmitButton (
                    onSubmit = {
                        selectedCategoryError.value = selectedCategory.value.isEmpty()
                        complaintError.value = complaint.value.isEmpty()

                        if (!selectedCategoryError.value && !complaintError.value) {
                            submitComplaintToFirebase(
                                am!!,
                                selectedCategory.value,
                                complaint.value,
                                context
                            )
                        }
                    },
                    selectedCategory = selectedCategory,
                    complaint = complaint
                )
            }
        }
    )
}

@Composable
fun EditTextField(value: String, onValueChange: (String) -> Unit, isError: Boolean,
                  modifier: Modifier = Modifier) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        isError = isError
    )
}

@Composable
fun SubmitButton(
    onSubmit: () -> Unit,
    selectedCategory: MutableState<String>,
    complaint: MutableState<String>,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            onSubmit()
            selectedCategory.value = ""
            complaint.value = ""
        },
        colors = buttonColors(Color(0xFF762525)),
        modifier = modifier
    ) {
        Text("Submit")
    }
}

fun submitComplaintToFirebase(am: String, category: String, complaint: String,  context: Context) {
    val db = FirebaseFirestore.getInstance()

    val complaintRecord = Complaint(
        am = am,
        category = category,
        complaint = complaint,
        timestamp = Timestamp.now()
    )

    db.collection("Complaint")
        .add(complaintRecord)
        .addOnSuccessListener {
            Toast.makeText(context, "Complaint submitted successfully",
                Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error submitting complaint: ${it.message}",
                Toast.LENGTH_SHORT).show()
        }
}
