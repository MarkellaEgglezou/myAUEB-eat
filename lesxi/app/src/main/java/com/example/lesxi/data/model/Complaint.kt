package com.example.lesxi.data.model

import com.google.firebase.Timestamp

data class Complaint(
    val am: String = "",
    val category: String = "",
    val complaint: String = "",
    val timestamp: Timestamp? = null
)
