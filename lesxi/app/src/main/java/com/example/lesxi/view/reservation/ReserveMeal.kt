package com.example.lesxi.view.reservation

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lesxi.data.model.*
import com.google.gson.Gson
import androidx.compose.ui.platform.LocalContext
import com.example.lesxi.R
import com.example.lesxi.data.fetchDishesForDay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMenuItems(
    day: String?,
    navController: NavHostController,
    reservationDetails: ReservationDetails
) {
    val scrollBehavior = null
    val context = LocalContext.current

    val dayMap = mapOf(
        "MONDAY" to "Mon",
        "TUESDAY" to "Tue",
        "WEDNESDAY" to "Wed",
        "THURSDAY" to "Thu",
        "FRIDAY" to "Fri",
        "SATURDAY" to "Sat",
        "SUNDAY" to "Sun"
    )

    val dayReserve = dayMap[day]
    val type = reservationDetails.type
    val itemsForDay = dayReserve?.let { fetchDishesForDay(dayReserve, type) }
    val scrollState = rememberScrollState()

    val itemCheckedStates = remember { mutableListOf<MutableState<Boolean>>() }

    Log.d("foods", "foods are:$type")

    itemsForDay?.let {
        itemCheckedStates.clear()
        it.forEach { _ ->
            itemCheckedStates.add(remember { mutableStateOf(false) })
        }
    }

    Scaffold(
        topBar = {

            CenterAlignedTopAppBar(
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))


            if (day == null || reservationDetails == null) {
                Text("Invalid arguments")

            }
            Spacer(modifier = Modifier.height(20.dp))

            itemsForDay?.forEachIndexed { index, item ->
                ShowItems(item, itemCheckedStates[index])
                Spacer(modifier = Modifier.height(20.dp))
            }

            Button(
                onClick = {
                    if (itemsForDay?.isNotEmpty() == true) {
                        // Check if at least one item is selected
                        val anyItemSelected = itemCheckedStates.any { it.value }

                        if (anyItemSelected) {
                            val checkedItems = itemsForDay.filterIndexed { index, _ ->
                                itemCheckedStates[index].value
                            }

                            val reserveItems = checkedItems.map { it.title }

                            val reservationJson = Uri.encode(Gson().toJson(reservationDetails))
                            val itemsJson = Uri.encode(Gson().toJson(reserveItems))

                            navController.navigate(
                                Routes.finishReservation + "/$reservationJson/$itemsJson"
                            )
                        } else {
                            // Show a toast if no item is selected
                            Toast.makeText(context, "Pick at least one item of food.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Show a toast if no food items are available
                        Toast.makeText(context, "No food items available for this day.", Toast.LENGTH_SHORT).show()
                    }
                    },

                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                colors = buttonColors(Color(0xFF762525))
            ) {
                Text("Next", color= Color.White)
            }
        }
    }
}



@Composable
fun ShowItems(item: MenuItem, mutableState: MutableState<Boolean>) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF762525),
        ),
        modifier = Modifier
            .size(width = 340.dp, height = 50.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Checkbox(
                checked = mutableState.value, // Use the passed state directly
                onCheckedChange = { mutableState.value = it }, // Update the passed state on change
                colors = CheckboxDefaults.colors(
                    checkedColor = Color.White,
                    uncheckedColor = Color.White,
                    checkmarkColor = Color.Black
                ),
                modifier = Modifier.padding(end = 16.dp),
            )

        }

    }
}