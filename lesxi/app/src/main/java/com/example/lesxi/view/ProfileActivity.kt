package com.example.lesxi.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lesxi.R
import com.example.lesxi.data.fetchAllData
import com.example.lesxi.data.fetchUser
import com.example.lesxi.data.model.Complaint
import com.example.lesxi.data.model.Reservation
import com.example.lesxi.data.model.User
import com.example.lesxi.data.updateUser
import com.example.lesxi.data.updateUserProfilePicture
import com.example.lesxi.ui.theme.LesxiTheme
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LesxiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        ProfileScreen(user)
                    } else {
                        Text(
                            stringResource(R.string.login_needed),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxSize()
                        )

                    }
                }
            }
        }
    }
}

// Get all data and call UserProfile
@Composable
fun ProfileScreen(firebaseUser: FirebaseUser) {
    var am by remember { mutableStateOf<String?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    var reservations by remember { mutableStateOf(listOf<Reservation>()) }
    var complaints by remember { mutableStateOf(listOf<Complaint>()) }

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
            stringResource(R.string.loading),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// User profile page
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
                        stringResource(R.string.profile_title),
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
                    SectionTitle(stringResource(R.string.reservations))
                    if (reservations.isEmpty()) {
                        Text(stringResource(R.string.empty_reservations))
                    } else {
                        ReservationList(reservations.map { it })
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Complaints
                item {
                    SectionTitle(stringResource(R.string.complaints))
                    if (complaints.isEmpty()) {
                        Text(stringResource(R.string.empty_complaints))
                    } else {
                        ComplaintList(complaints.map { it })
                    }
                }
            }
        }
    )
}

// Show user profile picture and modify it
@Composable
fun ProfilePicture(
    user: User,
    onAvatarSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    // Trigger avatar selection dialog by clicking the profile picture
    if (showDialog) {
        AvatarSelectionDialog(
            onAvatarSelected = { avatarName ->
                onAvatarSelected(avatarName)
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    // Determine which image to show based on user.avatar_photo
    val painter = if (user.avatarPhoto.isNotEmpty()) {
        val avatarResId = when (user.avatarPhoto) {
            stringResource(R.string.avatar_1) -> R.drawable.bear
            stringResource(R.string.avatar_2) -> R.drawable.cat
            stringResource(R.string.avatar_3) -> R.drawable.koala
            stringResource(R.string.avatar_4) -> R.drawable.lion
            stringResource(R.string.avatar_5) -> R.drawable.meerkat
            stringResource(R.string.avatar_6) -> R.drawable.panda
            stringResource(R.string.avatar_7) -> R.drawable.polar_bear
            stringResource(R.string.avatar_8) -> R.drawable.puffer_fish
            stringResource(R.string.avatar_9) -> R.drawable.sea_lion
            else -> R.drawable.ic_launcher_foreground // Default image in case of unexpected value
        }
        painterResource(id = avatarResId)
    } else {
        painterResource(id = R.drawable.ic_launcher_foreground) // Default image if empty
    }

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        modifier = Modifier
            .size(80.dp)
            .clickable { showDialog = true }
    ) {
        Image(
            painter = painter,
            contentDescription = stringResource(R.string.profile_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Show user information, edit and logout icons
@Composable
fun UserInfo(uid: String, user: User) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var newUser by remember { mutableStateOf(User()) }
    var selectedAvatar by remember { mutableStateOf<String?>(null) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        ProfilePicture(
            user,
            onAvatarSelected = { avatarName ->
                newUser = user.copy(avatarPhoto = avatarName)
                selectedAvatar = avatarName
                updateUserProfilePicture(uid, newUser)
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text("${user.name} ${user.surname}", style = MaterialTheme.typography.bodyLarge)
            Text(user.am, style = MaterialTheme.typography.bodySmall)
            Text(user.email, style = MaterialTheme.typography.bodySmall)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Edit icon
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.edit_profile),
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        showEditDialog = true
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Logout icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = stringResource(R.string.logout),
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
                        FirebaseAuth.getInstance().signOut()

                        val sharedPreferences = context.getSharedPreferences("app_prefs",
                            Context.MODE_PRIVATE)
                        sharedPreferences.edit().clear().apply()

                        val intent = Intent(context, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                                or Intent.FLAG_ACTIVITY_NEW_TASK)
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

// Show section titles
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

// Format timestamp to be user-friendly
fun formatTimestamp(timestamp: Timestamp?): String {
    if (timestamp == null) return "No date available"
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return formatter.format(date)
}

// Show user reservation list
@Composable
fun ReservationList(reservations: List<Reservation>) {
    // Sort the reservations by date
    val sortedReservations = reservations.sortedByDescending { it.date }
    // State to track if the list is expanded
    var isExpanded by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
        item {
            // Show only the first reservation by default
            if (sortedReservations.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Date: ${sortedReservations[0].date}")
                        Text("Time: ${sortedReservations[0].time}")
                        Text("Number of People: ${sortedReservations[0].numberOfPeople}")
                    }
                }
            }

            // Show the Expand button if the list has more than one item
            if (sortedReservations.size > 1) {
                Button(
                    onClick = { isExpanded = !isExpanded },
                    colors = buttonColors(Color(0xFF762525)),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(if (isExpanded) stringResource(R.string.show_less)
                    else stringResource(R.string.show_more))
                }

                // Show the remaining reservations if expanded
                if (isExpanded) {
                    sortedReservations.drop(1).forEach { reservation ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Date: ${reservation.date}")
                                Text("Time: ${reservation.time}")
                                Text("Number of People: ${reservation.numberOfPeople}")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Show user complaint list
@Composable
fun ComplaintList(complaints: List<Complaint>) {
    // Sort the complaints by timestamp
    val sortedComplaints = complaints.sortedByDescending { it.timestamp }
    // State to track if the list is expanded
    var isExpanded by remember { mutableStateOf(false) }

    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
        item {
            // Show only the first complaint by default
            if (sortedComplaints.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(formatTimestamp(sortedComplaints[0].timestamp))
                        Text("Category: ${sortedComplaints[0].category}")
                        Text("Complaint: ${sortedComplaints[0].complaint}")
                    }
                }
            }

            // Show the Expand button if the list has more than one item
            if (sortedComplaints.size > 1) {
                Button(
                    onClick = { isExpanded = !isExpanded },
                    colors = buttonColors(Color(0xFF762525)),
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(if (isExpanded) stringResource(R.string.show_less)
                    else stringResource(R.string.show_more))
                }

                // Show the remaining complaints if expanded
                if (isExpanded) {
                    sortedComplaints.drop(1).forEach { complaint ->
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

// Edit user name and surname dialog
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
                Text(text = stringResource(R.string.edit_profile),
                    style = MaterialTheme.typography.titleMedium)

                androidx.compose.material3.OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.name)) }
                )
                androidx.compose.material3.OutlinedTextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = { Text(stringResource(R.string.surname)) }
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(colors = buttonColors(Color(0xFF762525)),
                        onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        colors = buttonColors(Color(0xFF762525)),
                        onClick = {
                        // Save the changes
                            val updatedUser = user.copy(name = name, surname = surname)
                            updateUser(uid, updatedUser)
                            onDismiss()
                    }) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

// Edit user avatar dialog
@Composable
fun AvatarSelectionDialog(
    onAvatarSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // List of avatar names
    val avatars = listOf(
        stringResource(R.string.avatar_1),
        stringResource(R.string.avatar_2),
        stringResource(R.string.avatar_3),
        stringResource(R.string.avatar_4),
        stringResource(R.string.avatar_5),
        stringResource(R.string.avatar_6),
        stringResource(R.string.avatar_7),
        stringResource(R.string.avatar_8),
        stringResource(R.string.avatar_9)
    )

    var selectedAvatar by remember { mutableStateOf<String?>(null) }

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
                Text(text = stringResource(R.string.avatar_select),
                    style = MaterialTheme.typography.titleMedium)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(avatars) { avatar ->
                        AvatarItem(
                            avatar = avatar,
                            isSelected = avatar == selectedAvatar,
                            onClick = {
                                selectedAvatar = avatar
                            }
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        colors = buttonColors(Color(0xFF762525)),
                        onClick = onDismiss
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        colors = buttonColors(Color(0xFF762525)),
                        onClick = {
                            // Only proceed if an avatar is selected
                            if (selectedAvatar != null) {
                                onAvatarSelected(selectedAvatar!!)
                            }
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}

// Show available avatars
@Composable
fun AvatarItem(avatar: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        shape = CircleShape,
        border = if (isSelected) BorderStroke(2.dp, Color.Blue) else null,
        modifier = Modifier
            .padding(8.dp)
            .size(80.dp)
            .clickable { onClick() }
    ) {
        val imageId = when (avatar) {
            stringResource(R.string.avatar_1) -> R.drawable.bear
            stringResource(R.string.avatar_2) -> R.drawable.cat
            stringResource(R.string.avatar_3) -> R.drawable.koala
            stringResource(R.string.avatar_4) -> R.drawable.lion
            stringResource(R.string.avatar_5) -> R.drawable.meerkat
            stringResource(R.string.avatar_6) -> R.drawable.panda
            stringResource(R.string.avatar_7) -> R.drawable.polar_bear
            stringResource(R.string.avatar_8) -> R.drawable.puffer_fish
            stringResource(R.string.avatar_9) -> R.drawable.sea_lion
            else -> R.drawable.bear
        }

        Image(
            painter = painterResource(id = imageId),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
