package com.example.lesxi.data

import com.example.lesxi.data.model.Complaint
import com.example.lesxi.data.model.Reservation
import com.example.lesxi.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

// Get user object using user id
fun fetchUser(uid: String, onComplete: (User?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("User")
        .whereEqualTo("user_id", uid)
        .get()
        .addOnSuccessListener { documents ->
            val user = documents.firstOrNull()?.toObject(User::class.java)
            onComplete(user)
        }
        .addOnFailureListener { exception ->
            println("Error getting AM: $exception")
            onComplete(null)
        }
}

// Get list of reservations and complaints based on user am
fun fetchAllData(am: String, onComplete: (List<Reservation>, List<Complaint>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    var reservations: List<Reservation> = listOf()
    var complaints: List<Complaint> = listOf()

    // Counter to ensure all async operations complete
    var completedTasks = 0
    val totalTasks = 2

    fun checkCompletion() {
        completedTasks++
        if (completedTasks == totalTasks) {
            onComplete(reservations, complaints)
        }
    }

    // Fetch Reservations
    db.collection("Reservation")
        .whereEqualTo("am", am)
        .get()
        .addOnSuccessListener { documents ->
            reservations = documents.map { it.toObject(Reservation::class.java) }
            checkCompletion()
        }
        .addOnFailureListener { exception ->
            println("Error getting reservations: $exception")
            checkCompletion()
        }

    // Fetch Complaints
    db.collection("Complaint")
        .whereEqualTo("am", am)
        .get()
        .addOnSuccessListener { documents ->
            complaints = documents.map { it.toObject(Complaint::class.java) }
            checkCompletion()
        }
        .addOnFailureListener { exception ->
            println("Error getting complaint: $exception")
            checkCompletion()
        }
}

// Update user name and surname
fun updateUser(uid: String, user: User) {
    val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("User")
    val userDocument = userCollection.document(uid)

    val userMap = mapOf(
        "name" to user.name,
        "surname" to user.surname,
    )

    userDocument.update(userMap)
        .addOnSuccessListener {
            println("User updated successfully")
        }
        .addOnFailureListener { e ->
            println("Error updating user: ${e.message}")
        }
}

// Update user profile picture
fun updateUserProfilePicture(uid: String, user: User) {
    val db = FirebaseFirestore.getInstance()
    val userCollection = db.collection("User")
    val userDocument = userCollection.document(uid)

    val userMap = mapOf(
        "avatarPhoto" to user.avatarPhoto
    )

    userDocument.update(userMap)
        .addOnSuccessListener {
            println("User updated successfully")
        }
        .addOnFailureListener { e ->
            println("Error updating user: ${e.message}")
        }
}
