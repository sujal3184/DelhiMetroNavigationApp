package com.example.delhimetronavigationapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(3000) // 3-second delay
        navController.navigate("mainScreen") {
            popUpTo("splashScreen") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.metro),
                contentDescription = "Splash Image",
                modifier = Modifier
                    .fillMaxSize(),
            )
        }

        Row(modifier = Modifier.fillMaxWidth().padding(16.dp).height(24.dp), horizontalArrangement = Arrangement.Center) {
            Text("developed by Sujal Singh", fontSize = 24.sp, textAlign = TextAlign.Center,color = Color.Black)

        }


    }
}