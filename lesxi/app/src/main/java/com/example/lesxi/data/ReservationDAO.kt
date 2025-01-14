package com.example.lesxi.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
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