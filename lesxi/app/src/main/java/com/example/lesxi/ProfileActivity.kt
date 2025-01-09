package com.example.lesxi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lesxi.ui.theme.LesxiTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Locale

data class User(
    val am: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = ""
)

data class Reservation(
    val reservation_id: Int = 0,
    val am: String = "",
    val dining_option: String = "",
    val table_id: Int = 0,
    val timestamp: Timestamp? = null
)

data class Complaint(
    val am: String = "",
    val category: String = "",
    val complaint: String = "",
    val timestamp: Timestamp? = null
)

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
        UserProfile(user!!, reservations, complaints)
    }
    else {
        Text(
            "Loading...",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
}

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

fun fetchAllData(am: String, onComplete: (List<Reservation>, List<Complaint>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    // Initialize the results
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(
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
                    UserInfo(user)
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
fun ProfilePicture() {
    // Replace with actual profile picture logic
    val painter = painterResource(id = R.drawable.ic_launcher_foreground)

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile Picture",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun UserInfo(user: User) {
    val context = LocalContext.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Profile Picture
        ProfilePicture()

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
                        /* TODO */
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
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
                    }
            )
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
