package com.example.lesxi.data.model

data class MenuItem(
    val itemID: String = "",
    val title: String = "",
    val description: String = "",
    val allergens: List<String> = emptyList(),
    val imageUrl: String = "",
    val day: String = "",
    val type: String = ""
)
