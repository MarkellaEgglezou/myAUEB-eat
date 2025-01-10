package com.example.lesxi

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject


@Composable
fun ShowMenuItems(day: String) {
    println(day)
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
    val navController = rememberNavController()


    val itemCheckedStates = remember { mutableListOf<MutableState<Boolean>>() }

    itemsForDay?.let {
        itemCheckedStates.clear()
        it.forEach { _ ->
            itemCheckedStates.add(remember { mutableStateOf(false) })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState),
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Display the menu items with checkboxes
        itemsForDay?.forEachIndexed { index, item ->
            ShowItems(item, itemCheckedStates[index])
            Spacer(modifier = Modifier.height(20.dp))
        }

        Button(
            onClick = {
                val checkedItems = itemsForDay?.filterIndexed { index, _ ->
                    itemCheckedStates[index].value
                }

                checkedItems?.forEach {
                    println("Checked item: ${it.title}")
                }
//                navController.navigate(Routes.finishReservation +"/${checkedItems}")
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            colors = buttonColors(Color(0xFF762525))
        ) {
            Text("Next", color= Color.White)
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
    var isChecked by remember { mutableStateOf(false) }
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
                checked = isChecked,
                onCheckedChange = { isChecked = it },
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