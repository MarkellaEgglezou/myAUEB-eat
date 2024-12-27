package com.example.lesxi

import android.os.Bundle
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lesxi.ui.theme.LesxiTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class User(
    val am: Int = 0,
    val name: String = "",
    val surname: String = "",
    val email: String = ""
)

data class Reservation(
    val reservation_id: Int = 0,
    val am: Int = 0,
    val dining_option: String = "",
    val table_id: Int = 0,
    val timestamp: com.google.firebase.Timestamp? = null
)

data class Review(
    val review_id: Int = 0,
    val am: Int = 0,
    val rating: Int = 0,
    val food_id: Int = 0,
    val comment: String = "",
    val review_date: com.google.firebase.Timestamp? = null
)

data class Complaint(
    val complaint_id: Int = 0,
    val am: Int = 0,
    val category: String = "",
    val complaint: String = "",
    val timestamp: com.google.firebase.Timestamp? = null
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
                    ProfileScreen()
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    val am = 8210039
    var user by remember { mutableStateOf<User?>(null) }
    var reservations by remember { mutableStateOf(listOf<Reservation>()) }
    var reviews by remember { mutableStateOf(listOf<Review>()) }
    var complaints by remember { mutableStateOf(listOf<Complaint>()) }


    // Fetch all data
    if (user == null) {
        LaunchedEffect(Unit) {
            fetchAllData(am) { fetchedUser, fetchedReservations, fetchedReviews, fetchedComplaints->
                user = fetchedUser
                reservations = fetchedReservations
                reviews = fetchedReviews
                complaints = fetchedComplaints
            }
        }
    }

    UserProfile(user, reservations, reviews, complaints)
}

fun fetchAllData(am: Int, onComplete: (User?, List<Reservation>, List<Review>,
                                          List<Complaint>) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    // Initialize the results
    var user: User? = null
    var reservations: List<Reservation> = listOf()
    var reviews: List<Review> = listOf()
    var complaints: List<Complaint> = listOf()

    // Counter to ensure all async operations complete
    var completedTasks = 0
    val totalTasks = 4

    fun checkCompletion() {
        completedTasks++
        if (completedTasks == totalTasks) {
            onComplete(user, reservations, reviews, complaints)
        }
    }

    // Fetch User
    db.collection("User")
        .whereEqualTo("am", am)
        .get()
        .addOnSuccessListener { documents ->
            user = documents.firstOrNull()?.toObject(User::class.java)
            checkCompletion()
        }
        .addOnFailureListener { exception ->
            println("Error getting user: $exception")
            checkCompletion()
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

    // Fetch Reviews
    db.collection("Review")
        .whereEqualTo("am", am)
        .get()
        .addOnSuccessListener { documents ->
            reviews = documents.map { it.toObject(Review::class.java) }
            checkCompletion()
        }
        .addOnFailureListener { exception ->
            println("Error getting collection: $exception")
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
    user: User?,
    reservations: List<Reservation>,
    reviews: List<Review>,
    complaints: List<Complaint>
    ) {
    if (user == null) {
        Text("Loading user data...", modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center)
        return
    }
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // User Info
                UserInfo(user)

                Spacer(modifier = Modifier.height(16.dp))

                // Reservations
                SectionTitle("Reservations")
                if (reservations.isEmpty()) {
                    Text("No reservations found.")
                } else {
                    ReservationList(reservations.map { it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reviews
                SectionTitle("Reviews")
                if (reviews.isEmpty()) {
                    Text("No reviews available.")
                } else {
                    ReviewList(reviews.map { it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Complaints
                SectionTitle("Complaints")
                if (complaints.isEmpty()) {
                    Text("No complaints registered.")
                } else {
                    ComplaintList(complaints.map { it })
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Logout Button
                LogOutButton {}
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
            Text("${user.am}", style = MaterialTheme.typography.bodySmall)
            Text("${user.email}", style = MaterialTheme.typography.bodySmall)
        }

        // Edit Icon
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Profile",
            modifier = Modifier
                .size(24.dp)
                .clickable { /* TODO */ }
        )
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
    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
        items(reservations) { reservation ->
            // Display the reservation details
            Text("- Time & Date: ${formatTimestamp(reservation.timestamp)}")
            Text("- Table: ${reservation.table_id}")
            Text("- Dining Option: ${reservation.dining_option}")
        }
    }
}

@Composable
fun ReviewList(reviews: List<Review>) {
    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
        items(reviews) { review ->
            // Display the review details
            Text("- Time & Date: ${formatTimestamp(review.review_date)}")
            Text("- Rating: ${review.rating}")
            Text("- Comment: ${review.comment}")
        }
    }
}

@Composable
fun ComplaintList(complaints: List<Complaint>) {
    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
        items(complaints) { complaint ->
            // Display the complaint details
            Text("- Time & Date: ${formatTimestamp(complaint.timestamp)}")
            Text("- Category: ${complaint.category}")
            Text("- Complaint: ${complaint.complaint}")
        }
    }
}

@Composable
fun LogOutButton(modifier: Modifier.Companion = Modifier, onClick: () -> Unit) {
    Column (
        modifier = modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { /*TODO*/ }, colors = buttonColors(Color(0xFF762525)))
        {
            Text("Log Out")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    LesxiTheme {
        ProfileScreen()
    }
}
