package com.example.delhimetronavigationapp


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import com.example.delhimetronavigationapp.R


@Composable
fun currentmap(){
    var scale by remember { mutableStateOf(1f) } // Zoom level
    var offset by remember { mutableStateOf(Offset.Zero) } // Offset for panning

    val density = LocalDensity.current.density
    val screenWidth = LocalConfiguration.current.screenWidthDp * density
    val screenHeight = LocalConfiguration.current.screenHeightDp * density


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Optional background
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale*zoom).coerceAtLeast(1f) // Apply zoom
                    val maxOffsetX = (screenWidth * (scale - 1)) / 2
                    val maxOffsetY = (screenHeight * (scale - 1)) / 2

                    val newOffsetX = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX) // Restrict X movement
                    val newOffsetY = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY) // Restrict Y movement

                    offset = Offset(newOffsetX, newOffsetY)
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.actualmap), // Replace with your image
            contentDescription = "Zoomable Image",
            modifier = Modifier
                .fillMaxWidth() // Image takes full width
                .wrapContentHeight()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit // Keeps the full image visible
        )
    }
}


@Composable
fun upcomingmap(){
    var scale by remember { mutableStateOf(1f) } // Zoom level
    var offset by remember { mutableStateOf(Offset.Zero) } // Offset for panning

    val density = LocalDensity.current.density
    val screenWidth = LocalConfiguration.current.screenWidthDp * density
    val screenHeight = LocalConfiguration.current.screenHeightDp * density


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Optional background
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale*zoom).coerceAtLeast(1f) // Apply zoom
                    val maxOffsetX = (screenWidth * (scale - 1)) / 2
                    val maxOffsetY = (screenHeight * (scale - 1)) / 2

                    val newOffsetX = (offset.x + pan.x).coerceIn(-maxOffsetX, maxOffsetX) // Restrict X movement
                    val newOffsetY = (offset.y + pan.y).coerceIn(-maxOffsetY, maxOffsetY) // Restrict Y movement

                    offset = Offset(newOffsetX, newOffsetY)
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.upcoming_map), // Replace with your image
            contentDescription = "Zoomable Image",
            modifier = Modifier
                .fillMaxWidth() // Image takes full width
                .wrapContentHeight()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y
                ),
            contentScale = ContentScale.Fit // Keeps the full image visible
        )
    }
}

