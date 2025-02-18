package com.example.lesxi.view

import android.os.Bundle
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
import com.example.lesxi.R
import com.example.lesxi.ui.theme.LesxiTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.lesxi.data.model.*
import com.example.lesxi.data.*

class ComplaintFormActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LesxiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = FirebaseAuth.getInstance().currentUser
                     if (user != null) {
                        Form(user)
                    } else {
                        Text(
                            stringResource(R.string.login_needed),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

// Show complaint form
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
        Text(
            stringResource(R.string.am_not_found),
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center)
        return
    }

    val complaintCategories = listOf(
        stringResource(R.string.complaint_category_1),
        stringResource(R.string.complaint_category_2),
        stringResource(R.string.complaint_category_3),
        stringResource(R.string.complaint_category_4)
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
                        stringResource(R.string.form_title),
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
                Spacer(modifier = Modifier.height(40.dp))

                // Complaint category dropdown menu
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
                        label = { Text(stringResource(R.string.choose_category)) },
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
                        text = stringResource(R.string.category_error),
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Complaint text box
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
                        text = stringResource(R.string.complaint_error),
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))

                // Submit button
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

// Text field functionality
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

// Submit button functionality
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
        Text(stringResource(R.string.submit))
    }
}
