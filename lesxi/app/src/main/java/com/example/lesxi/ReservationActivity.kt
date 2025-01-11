package com.example.lesxi

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lesxi.data.model.ReservationDetails
import com.example.lesxi.data.model.Routes
import com.google.gson.Gson
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


fun isToday(dateString: String): Boolean {
    val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
    val parsedDate = LocalDate.parse(dateString, formatter)
    val today = LocalDate.now()

    return parsedDate.isEqual(today)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReserveTableScreen(navController: NavController) {
    var selectedDate by remember { mutableStateOf("Choose Date") }
    var selectedTime by remember { mutableStateOf("Choose Time") }
    var numberOfPeople by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var currentSelectedTime by remember { mutableStateOf("") }
    var isDisabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF762525),
                    titleContentColor = Color.White),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.add_reservation),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.select_date),
                style = androidx.compose.material.MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(onClick = {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, y, m, d -> selectedDate = "$d/${m + 1}/$y" },
                    year, month, day
                )

                datePickerDialog.datePicker.minDate = calendar.timeInMillis
                datePickerDialog.show()
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF762525),
                    disabledContentColor = Color.White

                ),
                modifier = Modifier.padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(selectedDate)
            }

            val timeSlots = if (selectedDate != "Choose Date") {
                listOf(
                    "08:00 AM", "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM", "03:00 PM", "07:00 PM", "07:30 PM", "08:00 PM"
                )
            } else {
                listOf()
            }

            val unavailableSlots = setOf("01:00 PM")

            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("hh:mm a")
            val oneHourLater = currentTime.plusHours(1)

            val availableSlots = if (selectedDate != "Choose Date" && isToday(selectedDate)) {
                // Filter slots only if it's today
                timeSlots.filter { timeSlot ->
                    val slotTime = LocalTime.parse(timeSlot, formatter)
                    !unavailableSlots.contains(timeSlot) && slotTime.isAfter(oneHourLater)
                }
            } else {
                // If it's not today, filter slots without considering the one-hour constraint
                timeSlots.filter { timeSlot ->
                    !unavailableSlots.contains(timeSlot)
                }
            }


            // Remember selected time state
            val selectedTime = remember { mutableStateOf(availableSlots.firstOrNull() ?: "") }

            @Composable
            fun DropdownMenu(
                availableSlots: List<String>,
                selectedTime: String,
                onTimeSelected: (String) -> Unit
            ) {
                var expanded = remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableSlots.forEach { time -> val isSelected = currentSelectedTime == time
                            Button(
                                onClick = {
                                    currentSelectedTime = time
                                    onTimeSelected(time)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) Color(0xFF962626) else Color(0xFF762525),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(text = time)
                            }
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = stringResource(R.string.select_time),
                    style = androidx.compose.material.MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))


                DropdownMenu(
                    availableSlots = availableSlots,
                    selectedTime = selectedTime.value,
                    onTimeSelected = { selectedTime.value = it }
                )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Take Out?",
                    style = androidx.compose.material.MaterialTheme.typography.h6
                )
                Switch(
                    checked = isDisabled,
                    onCheckedChange = { isDisabled = it })
            }
                val context = LocalContext.current
                val noofpeople = arrayOf(
                    "Select number of people for dining in",
                    "1",
                    "2",
                    "3",
                    "4",
                    "5",
                    "6",
                    "7",
                    "8",
                    "9",
                    "10"
                )
                var expanded by remember { mutableStateOf(false) }
                var selectedText by remember { mutableStateOf(noofpeople[0]) }

                Spacer(modifier = Modifier.height(16.dp))

                Box(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { if (!isDisabled) expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedText,
                            colors = TextFieldDefaults.colors(focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF962626),
                                unfocusedContainerColor = Color(0xFF762525)),
                            onValueChange = {},
                            readOnly = true,
                            enabled = !isDisabled,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
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

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {if (selectedDate != "Choose Date" && selectedTime.value.isNotEmpty()) {
                    val numberOfPeople = if (isDisabled) "0" else selectedText
                    val reservationDetails = ReservationDetails(
                        date = selectedDate, time = selectedTime.toString(),
                        numberOfPeople = numberOfPeople,
                    )

                    val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
                    val date = LocalDate.parse(selectedDate, formatter)
                    val day = date.dayOfWeek
                    val json = Uri.encode(Gson().toJson(reservationDetails))

                    navController.navigate(Routes.showMeals +"/$day/$json")
                } else {
                    Toast.makeText(context, "Please fill in all details.", Toast.LENGTH_SHORT).show()
                }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF762525),
                    contentColor = Color.White
                )
            ) {
                Text("Next")
            }
        }
    }
}












































