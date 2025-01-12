package com.example.lesxi


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lesxi.data.model.ReservationDetails


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    navController: NavHostController,
    reservationDetails: ReservationDetails,
    items: List<String>,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    if (reservationDetails == null || items == null) {
        Text("Invalid data passed!")
        return
    }



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


        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(60.dp))
            Text("Reservation Details:")
            Text("Date: ${reservationDetails.date}")
            Text("Time: ${reservationDetails.time.substring(19, 27)}")
            Text("Number of People: ${reservationDetails.numberOfPeople}")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Items:")
            items.forEach { item ->
                Text(item)
            }
        }
    }
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.Start
//        ) {
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text(
//                text = "Reservation Details",
//                style = androidx.compose.material.MaterialTheme.typography.h6,
//                modifier = Modifier.padding(bottom = 16.dp)
//            )
//
//            Spacer(modifier = Modifier.height(48.dp))
//            val reservationDetails = ReservationDetails(date, time, people)
//            reservationDetails?.let {
//                DetailRow(label = "Date:", value = it.date)
//                Spacer(modifier = Modifier.height(48.dp))
//                DetailRow(label = "Time:", value = it.time)
//                Spacer(modifier = Modifier.height(48.dp))
//                DetailRow(label = "People:", value = it.numberOfPeople)
//                Spacer(modifier = Modifier.height(48.dp))
////                DetailRow(label = "Table:", value = it.table)
//            }
//
//            Spacer(modifier = Modifier.weight(1f))
//
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Button(
//                    onClick = { TODO() },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF762525),
//                        contentColor = Color.White
//                    )
//                ) {
//                    Text("Back")
//                }
//
//                Button(
//                    onClick = { TODO() },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = Color(0xFF762525),
//                        contentColor = Color.White
//                    )
//                ) {
//                    Text("Confirm")
//                }
//            }
//        }
//    }
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


