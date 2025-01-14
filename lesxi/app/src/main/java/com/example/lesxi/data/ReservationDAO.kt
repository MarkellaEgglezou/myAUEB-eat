package com.example.lesxi.data

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.example.lesxi.data.model.MenuItem
import com.example.lesxi.data.model.ReservationDetails
import com.example.lesxi.data.model.Routes
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

suspend fun fetchUnavailableSlots(date: String): List<String> {
    val db = FirebaseFirestore.getInstance()
    val timeSlotss = mutableListOf<String>()

    try {
        val result = db.collection("TimeSlots")
            .whereEqualTo("date", date)
            .whereEqualTo("free", 0)
            .get()
            .await()


        for (document in result) {
            val timeSlott = document.getString("time")
            timeSlott?.let { timeSlotss.add(it) }
        }
    } catch (exception: Exception) {
        println("Error getting documents: $exception")
    }

    return timeSlotss
}

suspend fun fetchAvailableSpots(date: String, time: String): Int {
    val db = FirebaseFirestore.getInstance()
    val snapshot = db.collection("TimeSlots")
        .whereEqualTo("date", date)
        .whereEqualTo("time", time)
        .get()
        .await()
    Log.d("check", "check: $date")
    Log.d("check", "check: $time")

    var spots = 100
    if (!snapshot.isEmpty) {
        val document = snapshot.documents.first()
        spots = document.getLong("free")?.toInt() ?: 100
        Log.d("checkfirst", "check: $spots")
        return spots
    }
    Log.d("check", "check: $spots")
    return spots
}

@Composable
fun fetchDishesForDay(day: String, type: List<String>): List<MenuItem> {
    val db = FirebaseFirestore.getInstance()
    var items by remember { mutableStateOf<List<MenuItem>>(emptyList()) }

    Log.d("foodybefore", "$type")

    LaunchedEffect(day, type) {
        db.collection("Menu")
            .whereEqualTo("day", day)
            .whereIn("type", type)
            .get()
            .addOnSuccessListener { snapshot ->
                items = snapshot.documents.mapNotNull { it.toObject<MenuItem>() }
                Log.d("food", "$items")
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }
    return items
}

fun saveReservationToFirebase(
    context: Context,
    navController: NavHostController,
    reservationDetails: ReservationDetails,
    items: List<String>
) {
    val db = FirebaseFirestore.getInstance()

    // Prepare data to save
    val reservationData = hashMapOf(
        "am" to reservationDetails.am,
        "date" to reservationDetails.date,
        "time" to reservationDetails.time.substring(19, 27),
        "numberOfPeople" to reservationDetails.numberOfPeople,
        "items" to items
    )

    // Save data to FireBase
    db.collection("Reservation")
        .add(reservationData)
        .addOnSuccessListener {

            updateOrCreateDocument("TimeSlots", reservationDetails.date, reservationDetails.time.substring(19, 27), reservationDetails.numberOfPeople.toInt())
            Toast.makeText(context, "Reservation confirmed!", Toast.LENGTH_SHORT).show()
            //navController.popBackStack()
            navController.navigate(Routes.main_page)
        }
        .addOnFailureListener { exception ->
            // Handle error
            println("Error saving reservation: $exception")
        }
}

fun updateOrCreateDocument(
    collectionName: String,
    date: String,
    time: String,
    free: Int
) {
    val db = FirebaseFirestore.getInstance()

    db.collection(collectionName)
        .whereEqualTo("date", date)
        .whereEqualTo("time", time)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                // Update the existing document(s)
                for (document in documents) {
                    db.collection(collectionName)
                        .document(document.id)
                        .update("free", FieldValue.increment(-free.toLong()))
                        .addOnSuccessListener {
                            println("Field updated successfully for document ID: ${document.id}")
                        }
                        .addOnFailureListener { e ->
                            println("Error updating field for document ID: ${document.id}: ${e.message}")
                        }
                }
            } else {
                // No matching document, create a new one
                val newDocument = hashMapOf(
                    "date" to date,
                    "time" to time,
                    "free" to (100 - free)
                )

                db.collection(collectionName)
                    .add(newDocument)
                    .addOnSuccessListener { newDoc ->
                        println("New document created with ID: ${newDoc.id}")
                    }
                    .addOnFailureListener { e ->
                        println("Error creating document: ${e.message}")
                    }
            }
        }
        .addOnFailureListener { e ->
            println("Error finding document: ${e.message}")
        }
}