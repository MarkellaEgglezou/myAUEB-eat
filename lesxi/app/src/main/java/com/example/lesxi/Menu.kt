package com.example.lesxi


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.Calendar
import com.example.lesxi.data.model.*





@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuLesxi(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    var items by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedDay by remember { mutableStateOf("Monday") }
    val scrollState = rememberScrollState()


    if (selectedDay.isEmpty())
        selectedDay = getCurrentDay()

    fun fetchDishesForDay(day: String) {
        isLoading = true
        db.collection("Menu")
            .whereEqualTo("day", day)
            .get()
            .addOnSuccessListener { snapshot ->
                items = snapshot.documents.mapNotNull { it.toObject<MenuItem>() }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                isLoading = false
            }
    }
    LaunchedEffect(selectedDay) {
        fetchDishesForDay(selectedDay)
    }



    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF762525),
                        titleContentColor = Color.White
                    ),
                    title = {
                        Text(
                            "Menu",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center
            ) {

                DaysMenu(selectedDay = selectedDay) { day ->
                    selectedDay = day
                }
                Spacer(modifier = Modifier.height(20.dp))

                if (items.isEmpty()) {
                    Text("No dishes available for this day.")
                }

                items.forEach { item ->
                    MenuItems(item, navController = navController)
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

    }

}

fun getCurrentDay(): String {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

    return when (dayOfWeek) {
        Calendar.SUNDAY -> "Sun"
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        else -> "Unknown"
    }
}


@Composable
fun DaysMenu(selectedDay: String, onDaySelected: (String) -> Unit) {
    val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    LazyRow(
        modifier = Modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        items(daysOfWeek) { day ->
            DayTag(day = day, isSelected = day == selectedDay) {
                onDaySelected(day)
            }
        }
    }
}


@Composable
fun DayTag(day: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable { onClick() }
            .background(
                color = if (isSelected) Color(0xFF762525) else Color(0xFFEEEEEE),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = day,
            color = if (isSelected) Color.White else Color.Black,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun MenuItems(item: MenuItem, navController: NavHostController) {
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF762525),
        ),
        modifier = Modifier
            .size(width = 340.dp, height = 100.dp),
        onClick = {
            navController.navigate(Routes.menuItemDetails+ "/${item.itemID}")
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "see more", tint = Color.White)
        }
        Text(
            text = item.description,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
            color = Color.White
        )



    }
}


