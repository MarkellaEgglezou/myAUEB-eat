package com.example.lesxi

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lesxi.ui.theme.LesxiTheme

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
                    Form()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form() {
    val complaintCategories = listOf(
        "Ποιότητα φαγητού",
        "Εμπειρία στη λέσχη",
        "Ταχύτητα εξυπηρέτησης",
        "Διαδικαστικά προβλήματα",
        "Άλλο"
    )
    val selectedCategory = remember { mutableStateOf("") }
    val isDropdownExpanded = remember { mutableStateOf(false) }
    val complaint = remember { mutableStateOf("") }
    val photo = remember { mutableStateOf("") }

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
                        label = { Text("Επιλέξτε κατηγορία") },
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
                        text = "Παρακαλώ επιλέξτε κατηγορία",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = stringResource(R.string.complaint),
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
                        text = "Παρακαλώ συμπληρώστε το παράπονό σας",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = stringResource(R.string.upload_photo),
                    modifier = Modifier
                        .padding(bottom = 16.dp, top = 40.dp)
                        .align(Alignment.Start)
                )
                EditTextField(
                    value = photo.value,
                    onValueChange = { photo.value = it },
                    isError = false,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))
                SubmitButton {
                    selectedCategoryError.value = selectedCategory.value.isEmpty()
                    complaintError.value = complaint.value.isEmpty()

                    if (!selectedCategoryError.value && !complaintError.value) {
                        /* TODO */
                    }
                }
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
fun SubmitButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = buttonColors(Color(0xFF762525)),
        modifier = modifier
    ) {
        Text("Submit")
    }
}
