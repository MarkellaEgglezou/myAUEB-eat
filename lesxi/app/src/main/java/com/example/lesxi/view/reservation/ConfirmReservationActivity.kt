package com.example.lesxi.view.reservation


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lesxi.R
import com.example.lesxi.data.model.ReservationDetails
import com.example.lesxi.data.model.Routes
import com.example.lesxi.data.saveReservationToFirebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    navController: NavHostController,
    reservationDetails: ReservationDetails,
    items: List<String>
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    if (reservationDetails == null || items == null) {
        Text("Invalid data passed!")
        return
    }

    val scrollState = rememberScrollState()
    Scaffold(
            topBar = {

                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back),
                                tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF762525),
                        titleContentColor = Color.White),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(
                            text = stringResource(R.string.add_reservation),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                                .padding(end = 30.dp)
                        )
                    }
                )
            },
//        modifier = Modifier.nestedScroll(scrollBehavior.)
    ) { paddingValues ->


        Column(modifier = Modifier.padding(16.dp)
            .verticalScroll(scrollState)) {
            Spacer(modifier = Modifier.height(75.dp))

            Text(
                text = "Reservation Details",
                style = androidx.compose.material.MaterialTheme.typography.h4.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Burgundy Box for form elements
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF762525))
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Date: ${reservationDetails.date}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Time: ${reservationDetails.time.substring(19, 27)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Number of People: ${reservationDetails.numberOfPeople}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Items:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    items.forEach { item ->
                        Text(item, color = Color.White, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            val context = LocalContext.current

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    saveReservationToFirebase(
                        context = context,
                        navController = navController,
                        reservationDetails = reservationDetails,
                        items = items
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF762525),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Confirm Reservation")
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


