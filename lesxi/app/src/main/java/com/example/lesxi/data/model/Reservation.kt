package com.example.lesxi.data.model

import com.google.firebase.Timestamp

data class Reservation(
    val reservation_id: Int = 0,
    val am: String = "",
    val dining_option: String = "",
    val table_id: Int = 0,
    val timestamp: Timestamp? = null
)