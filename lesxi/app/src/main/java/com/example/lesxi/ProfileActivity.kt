package com.example.lesxi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lesxi.ui.theme.LesxiTheme

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LesxiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Form()
                }
            }
        }
    }
}

class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LesxiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserProfile()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF762525) // Set background color
                )
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
                // User Info Section
                UserInfo()

                Spacer(modifier = Modifier.height(16.dp))

                // Pending Reservations
                SectionTitle("Εκκρεμείς Κρατήσεις")
                ReservationList(listOf("Κράτηση 1", "Κράτηση 2"))

                Spacer(modifier = Modifier.height(16.dp))

                // Last 5 Completed Reservations
                SectionTitle("Τελευταίες 5 Εκτελεσμένες Κρατήσεις")
                ReservationList(listOf("Κράτηση 3", "Κράτηση 4", "Κράτηση 5", "Κράτηση 6", "Κράτηση 7"))

                Spacer(modifier = Modifier.height(16.dp))

                // Last 5 Published Reviews
                SectionTitle("Τελευταίες 5 Κριτικές")
                ReviewList(listOf("Κριτική 1", "Κριτική 2", "Κριτική 3", "Κριτική 4", "Κριτική 5"))

                Spacer(modifier = Modifier.height(16.dp))

                // Last 5 Submitted Complaints
                SectionTitle("Τελευταία 5 Παράπονα")
                ComplaintList(listOf("Παράπονο 1", "Παράπονο 2", "Παράπονο 3", "Παράπονο 4", "Παράπονο 5"))

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
fun UserInfo() {
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
            Text("John Doe")
            Text("123456")
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

@Composable
fun ReservationList(reservations: List<String>) {
    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
        items(reservations.size) { index ->
            Text("- ${reservations[index]}")
        }
    }
}

@Composable
fun ReviewList(reviews: List<String>) {
    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
        items(reviews.size) { index ->
            Text("- ${reviews[index]}")
        }
    }
}

@Composable
fun ComplaintList(complaints: List<String>) {
    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
        items(complaints.size) { index ->
            Text("- ${complaints[index]}")
        }
    }
}

@Composable
fun LogOutButton(modifier: Modifier.Companion = Modifier, onClick: @Composable () -> Unit) {
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
        UserProfile()
    }
}
