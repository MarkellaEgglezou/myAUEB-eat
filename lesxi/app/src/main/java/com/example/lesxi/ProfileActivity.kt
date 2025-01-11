package com.example.lesxi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.lesxi.ui.theme.LesxiTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.lesxi.data.model.*
import com.example.lesxi.data.*
import com.example.lesxi.view.MainActivity

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LesxiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        ProfileScreen(user)
                    } else {
                        Text(
                            "User not logged in",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )

                    }
                }
            }
        }
    }

    // Override onActivityResult to handle image selection result
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val pickImage = 1000
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            val imageUri = data?.data
            // Upload the image to Firebase Storage
            imageUri?.let { uploadImageToFirebase(it) }
        }
    }

    private fun uploadImageToFirebase(imageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${FirebaseAuth.getInstance().currentUser?.uid}.jpg")
        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("User").document(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                userRef.update("avatar_photo", uri.toString())
                    .addOnSuccessListener {
                        println("Profile Picture Updated")
                    }
                    .addOnFailureListener { e ->
                        println("Failed to Update Profile Picture: ${e.message}")
                    }
            }
        }.addOnFailureListener { e ->
            println("Image Upload Failed: ${e.message}")
        }
    }
}

@Composable
fun ProfileScreen(firebaseUser: FirebaseUser) {
    var am by remember { mutableStateOf<String?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    var reservations by remember { mutableStateOf(listOf<Reservation>()) }
    var complaints by remember { mutableStateOf(listOf<Complaint>()) }

    // Fetch all data
    if (am == null) {
        LaunchedEffect(firebaseUser.uid) {
            fetchUser(firebaseUser.uid) { fetchedUser ->
                user = fetchedUser
                am = user?.am
                if (am != null) {
                    fetchAllData(am!!) {fetchedReservations, fetchedComplaints ->
                        reservations = fetchedReservations
                        complaints = fetchedComplaints
                    }
                }
            }
        }
    }

    if (user != null) {
        UserProfile(firebaseUser.uid, user!!, reservations, complaints)
    }
    else {
        Text(
            "Loading...",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(
    uid: String,
    user: User,
    reservations: List<Reservation>,
    complaints: List<Complaint>
    ) {
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
                        "Profile",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // User Info
                item {
                    UserInfo(uid, user)
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Reservations
                item {
                    SectionTitle("Reservations")
                    if (reservations.isEmpty()) {
                        Text("No reservations found.")
                    } else {
                        ReservationList(reservations.map { it })
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Complaints
                item {
                    SectionTitle("Complaints")
                    if (complaints.isEmpty()) {
                        Text("No complaints registered.")
                    } else {
                        ComplaintList(complaints.map { it })
                    }
                }
            }
        }
    )
}

@Composable
fun ProfilePicture(imageUri: Uri?, onImageClick: () -> Unit) {
    val painter = imageUri?.let {
        rememberImagePainter(data = it)
    } ?: painterResource(id = R.drawable.ic_launcher_foreground) // Default image

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Black, CircleShape)
                .clickable { onImageClick() },
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun UserInfo(uid: String, user: User) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    val imageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImage = 1000

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Profile Picture
        ProfilePicture(imageUri = imageUri) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            (context as Activity).startActivityForResult(intent, pickImage)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // User's Name and AM
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("${user.name} ${user.surname}", style = MaterialTheme.typography.bodyLarge)
            Text(user.am, style = MaterialTheme.typography.bodySmall)
            Text(user.email, style = MaterialTheme.typography.bodySmall)
        }

        // Column to stack the icons vertically
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Edit Icon
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        showEditDialog = true
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))  // Space between icons

            // Logout Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Logout",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
                        // Restart the app
                        val intent = Intent(context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        (context as Activity).finish()
                    }
            )
        }
    }

    if (showEditDialog) {
        EditUserDialog(uid, user = user) {
            showEditDialog = false
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return "No date available"

    // Convert Timestamp to Date
    val date = timestamp.toDate()

    // Format the Date into a user-friendly string
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(date)
}

@Composable
fun ReservationList(reservations: List<Reservation>) {
    // State to track if the list is expanded
    var isExpanded by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
        item {
            // Show only the first reservation by default
            if (reservations.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(formatTimestamp(reservations[0].timestamp))
                        Text("Table: ${reservations[0].table_id}")
                        Text("Dining Option: ${reservations[0].dining_option}")
                    }
                }
            }

            // Show the Expand button if the list has more than one item
            if (reservations.size > 1) {
                Button(
                    onClick = { isExpanded = !isExpanded },
                    colors = buttonColors(Color(0xFF762525)),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(if (isExpanded) "Show Less" else "Show More")
                }

                // Show the remaining reservations if expanded
                if (isExpanded) {
                    reservations.drop(1).forEach { reservation ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(formatTimestamp(reservation.timestamp))
                                Text("Table: ${reservation.table_id}")
                                Text("Dining Option: ${reservation.dining_option}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComplaintList(complaints: List<Complaint>) {
    var isExpanded by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
        item {
            if (complaints.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(formatTimestamp(complaints[0].timestamp))
                        Text("Category: ${complaints[0].category}")
                        Text("Complaint: ${complaints[0].complaint}")
                    }
                }
            }

            if (complaints.size > 1) {
                Button(
                    onClick = { isExpanded = !isExpanded },
                    colors = buttonColors(Color(0xFF762525)),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(if (isExpanded) "Show Less" else "Show More")
                }

                if (isExpanded) {
                    complaints.drop(1).forEach { complaint ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(formatTimestamp(complaint.timestamp))
                                Text("Category: ${complaint.category}")
                                Text("Complaint: ${complaint.complaint}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditUserDialog(uid: String, user: User, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(user.name) }
    var surname by remember { mutableStateOf(user.surname) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = "Edit Profile", style = MaterialTheme.typography.titleMedium)

                // Input fields for name, surname, and email
                androidx.compose.material3.OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                androidx.compose.material3.OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text("Surname") }
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        colors = buttonColors(Color(0xFF762525)),
                        onClick = {
                        // Save the changes
                            val updatedUser = user.copy(name = name, surname = surname)
                            updateUser(uid, updatedUser)
                            onDismiss()
                    }) {
                        Text("Save")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(colors = buttonColors(Color(0xFF762525)),
                        onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

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
