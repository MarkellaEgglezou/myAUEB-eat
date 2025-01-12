package com.example.lesxi.data

import android.content.Context
import android.widget.Toast
import com.example.lesxi.data.model.Complaint
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

// Add complaint to Firebase
fun submitComplaintToFirebase(am: String, category: String, complaint: String,  context: Context) {
    val db = FirebaseFirestore.getInstance()

    val complaintRecord = Complaint(
        am = am,
        category = category,
        complaint = complaint,
        timestamp = Timestamp.now()
    )

    db.collection("Complaint")
        .add(complaintRecord)
        .addOnSuccessListener {
            Toast.makeText(context, "Complaint submitted successfully",
                Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error submitting complaint: ${it.message}",
                Toast.LENGTH_SHORT).show()
        }
}
