package com.example.lesxi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp



//Intended menu reservation screen - checking argument passing with this one




class ConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConfirmationScreen()
        }
    }
}

@Composable
fun ConfirmationScreen() {
    val context = LocalContext.current
    val intent = (context as? ComponentActivity)?.intent

    val selectedDate = intent?.getStringExtra("SELECTED_DATE") ?: "Not Available"
    val selectedTime = intent?.getStringExtra("SELECTED_TIME") ?: "Not Available"
    val numberOfPeople = intent?.getStringExtra("NUMBER_OF_PEOPLE") ?: "Not Available"

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Reservation Confirmation", /*style = MaterialTheme.typography.h6*/)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Date: $selectedDate")
        Text(text = "Time: $selectedTime")
        Text(text = "Number of People: $numberOfPeople")

    }

    //Spacer(modifier = Modifier.weight(1f))
    Button(
        onClick = {//TODO//

        },
        modifier = Modifier
            //.align(Alignment.End)
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF762525),
            contentColor = Color.White
        )
    ) {
        Text("Next")
    }
}