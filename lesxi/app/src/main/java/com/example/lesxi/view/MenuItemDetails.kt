package com.example.lesxi.view

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.lesxi.R
import com.example.lesxi.data.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDetailsScreen(itemID: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var item by remember { mutableStateOf<MenuItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(itemID) {
        db.collection("Menu")
            .document(itemID)
            .get()
            .addOnSuccessListener { document ->
                item = document.toObject(MenuItem::class.java)
                isLoading = false
                println(item)
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
                isLoading = false
            }
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.go_back),
                                tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF762525),
                        titleContentColor = Color.White
                    ),
                    title = {
                        Text(
                            text = item?.title ?: stringResource(R.string.no_avail),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                                .padding(end = 30.dp)
                        )
                    })
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top
            ) {
                item?.let { it1 -> ItemDetails(it1) }
            }
        }
    }
}

data class ImageData(val imageUrl: String)



@SuppressLint("InlinedApi")
@Composable
fun ItemDetails(item: MenuItem){
    Spacer(modifier = Modifier.height(60.dp))
    val imageData = ImageData(item.imageUrl)
    val painter = rememberAsyncImagePainter(imageData.imageUrl)
    Image(
        painter = painter,
        contentDescription = item.title,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
    )
    Text(
        text = item.description,
        fontSize = 16.sp,
        modifier = Modifier
            .padding(bottom = 16.dp, top = 40.dp)
    )
    Text(
        text = stringResource(R.string.allergens),
        fontSize = 16.sp,
        fontStyle = FontStyle.Italic,
        modifier = Modifier
            .padding(bottom = 16.dp, top = 40.dp)
    )

    if (item.allergens.isNotEmpty()) {
        AllergensList(item.allergens)
    } else {
        Text(text = stringResource(R.string.not_included))
    }


}

@Composable
fun AllergensList(items: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items.size) { index ->
            AnimatedVisibility(visible = true) {
                Card(
                    shape = RoundedCornerShape(6.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF762525),
                    ),
                ) {
                    Text(
                        text = items[index],
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

