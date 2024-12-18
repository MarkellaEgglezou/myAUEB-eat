package com.example.lesxi

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
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
            TopAppBar(title = { Text("Reserve a Table") })
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
            }) {
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
            }) {
                Text(selectedTime)
            }

            // Number of People Input
            Text("Number of People")
            TextField(
                value = numberOfPeople,
                onValueChange = { numberOfPeople = it },
                placeholder = { Text("Enter number of people") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Confirm Button
            Button(
                onClick = {
//                    if (selectedDate == "Choose Date" || selectedTime == "Choose Time" || numberOfPeople.isEmpty()) {
//                        showToast("Please fill all fields")
//                    } else {
//                        showToast("Reservation confirmed for $numberOfPeople people on $selectedDate at $selectedTime.")
//                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Reservation")
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








































