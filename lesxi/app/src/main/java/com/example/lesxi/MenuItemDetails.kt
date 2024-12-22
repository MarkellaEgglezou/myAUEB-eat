package com.example.lesxi

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import coil.compose.rememberImagePainter
import com.google.firebase.firestore.toObject
import com.google.firebase.inappmessaging.model.ImageData


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuItemDetailsScreen(itemID: String) {
    val db = FirebaseFirestore.getInstance()
    var item by remember { mutableStateOf<MenuItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(itemID) {
        db.collection("menu")
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
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color(0xFF762525),
                        titleContentColor = Color.White
                    ),
                    title = {
                        Text(
                            text = item?.title ?: "Null",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
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
        contentDescription = "Mousakas",
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
        text = "Αλλεργιογόνα",
        fontSize = 16.sp,
        fontStyle = FontStyle.Italic,
        modifier = Modifier
            .padding(bottom = 16.dp, top = 40.dp)
    )
    if (item.allergens.isNotEmpty()) {
        for (allergen in item.allergens) {
            Text(text = "- $allergen")  // Display each allergen
        }
    } else {
        Text(text = "Δεν περιλαμβάνονται.")
    }


}
