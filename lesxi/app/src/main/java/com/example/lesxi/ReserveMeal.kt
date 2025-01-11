package com.example.lesxi

import android.net.Uri
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.example.lesxi.data.model.*
import com.google.gson.Gson


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMenuItems(
    day: String?,
    navController: NavHostController,
    reservationDetails: ReservationDetails
) {
    val scrollBehavior = null

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
    val itemsForDay = dayReserve?.let { fetchDishesForDay(it) }
    val scrollState = rememberScrollState()

    val itemCheckedStates = remember { mutableListOf<MutableState<Boolean>>() }


    itemsForDay?.let {
        itemCheckedStates.clear()
        it.forEach { _ ->
            itemCheckedStates.add(remember { mutableStateOf(false) })
        }
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
                    if (itemsForDay?.size == itemCheckedStates.size) {
                        // Filter checked items
                        val checkedItems = itemsForDay.filterIndexed { index, item ->
                            itemCheckedStates[index].value // Filter based on checked state
                        }

                        val reserveItems = mutableListOf<String>()
                        if (checkedItems.isNotEmpty()) {
                            checkedItems.forEach { item ->
                                reserveItems.add(item.title)  // Add the title to reserveItems
                            }
                        } else {
                            println("checkedItems is empty or null")
                        }

                        // Serialize data into JSON
                        val reservationJson = Gson().toJson(reservationDetails)
                        val itemsJson = Gson().toJson(reserveItems)

                        // Encode the JSON strings
                        val encodedReservationJson = Uri.encode(reservationJson)
                        val encodedItemsJson = Uri.encode(itemsJson)

                        // Navigate with the encoded data
                        navController.navigate(Routes.finishReservation + "/$encodedReservationJson/$encodedItemsJson")
                    } else {
                        println("Error: Mismatched sizes between itemsForDay and itemCheckedStates")
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
fun fetchDishesForDay(day: String): List<MenuItem> {
    val db = FirebaseFirestore.getInstance()
    var items by remember { mutableStateOf<List<MenuItem>>(emptyList()) }

    db.collection("Menu")
        .whereEqualTo("day", day)
        .get()
        .addOnSuccessListener { snapshot ->
            items = snapshot.documents.mapNotNull { it.toObject<MenuItem>() }
        }
        .addOnFailureListener { exception ->
            println("Error getting documents: $exception")
        }
    return items
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