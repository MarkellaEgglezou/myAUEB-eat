package com.example.lesxi


import androidx.compose.ui.input.nestedscroll.nestedScroll
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.ArrayList

class ConfirmReservationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reservationDetails = intent.getSerializableExtra("reservationDetails") as? ReservationDetails

        setContent {

        }
    }
}

data class ReservationDetails(
    val date: String,
    val time: String,
    val numberOfPeople: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    date: String,
    time: String,
    people: String,
    items: ArrayList<String>?,
//    onConfirm: () -> Unit,
//    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color(0xFF762525),
                    titleContentColor = Color.White
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        "Confirm Reservation",
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
                text = "Reservation Details",
                style = androidx.compose.material.MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
            val reservationDetails = ReservationDetails(date, time, people)
            reservationDetails?.let {
                DetailRow(label = "Date:", value = it.date)
                Spacer(modifier = Modifier.height(48.dp))
                DetailRow(label = "Time:", value = it.time)
                Spacer(modifier = Modifier.height(48.dp))
                DetailRow(label = "People:", value = it.numberOfPeople)
                Spacer(modifier = Modifier.height(48.dp))
//                DetailRow(label = "Table:", value = it.table)
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { TODO() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF762525),
                        contentColor = Color.White
                    )
                ) {
                    Text("Back")
                }

                Button(
                    onClick = { TODO() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF762525),
                        contentColor = Color.White
                    )
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            style = androidx.compose.material.MaterialTheme.typography.subtitle1
        )
        Text(
            text = value,
            style = androidx.compose.material.MaterialTheme.typography.subtitle1
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun ConfirmationScreenPreview() {
//    LesxiTheme {
//        ConfirmationScreen(
//            reservationDetails = ReservationDetails(
//                date = "12/29/2024",
//                time = "7:00 PM",
//                numberOfPeople = "4",
//            ),
//            onConfirm = {},
//            onBack = {}
//        )
//    }
//}
