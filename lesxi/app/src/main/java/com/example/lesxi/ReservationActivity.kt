package com.example.lesxi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Layout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lesxi.ui.theme.LesxiTheme

import java.util.*

class ReservationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReserveTableScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveTableScreen() {
    // State variables for user input
    var selectedDate by remember { mutableStateOf("Choose Date") }
    var selectedTime by remember { mutableStateOf("Choose Time") }
    var numberOfPeople by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Main UI layout
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF762525),
                    titleContentColor = Color.White),
                title = { Text("Reserve a Table",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())})
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date Picker
            Text("Select Date")
            Button(onClick = {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                DatePickerDialog(
                    context,
                    { _, y, m, d -> selectedDate = "$d/${m + 1}/$y" },
                    year, month, day
                ).show()
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF762525),
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(16.dp)
                ) {
                Text(selectedDate)
            }

            // Time Picker
            Text("Select Time")
            Button(onClick = {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)

                TimePickerDialog(
                    context,
                    { _, h, m -> selectedTime = String.format("%02d:%02d", h, m) },
                    hour, minute, true
                ).show()
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF762525),
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(16.dp)
                ) {
                Text(selectedTime)
            }

            //People Picker
            Text("Number of People")
            val context = LocalContext.current
            val noofpeople = arrayOf("Select number of people", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            var expanded by remember { mutableStateOf(false) }
            var selectedText by remember { mutableStateOf(noofpeople[0]) }

            Box(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {
                    TextField(
                        value = selectedText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        noofpeople.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(text = item) },
                                onClick = {
                                    selectedText = item
                                    expanded = false
                                    Toast.makeText(context, item, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReserveTableScreenPreview() {
    LesxiTheme {
        ReservationActivity()
    }
}











































