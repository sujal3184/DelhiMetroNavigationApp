package com.example.delhimetronavigationapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.delhimetronavigationapp.ui.theme.StationSelector

// Data classes
data class Station(
    val id: String,
    val name: String,
    val lines: List<String>
)

data class Connection(
    val fromStation: String,
    val toStation: String,
    val line: String,
    val timeMins: Int
)

data class Route(
    val stations: List<String>,
    val totalTime: Int,
    val lineChanges: List<String>,
    val lines: List<String> // Store the line for each segment of the journey
)

@Composable
fun MetroRouteFinder(navController: NavController) {
    // Metro network data
    val metroData = remember { createMetroData() }
    val stations = metroData.first
    val connections = metroData.second

    // Use rememberSaveable instead of remember to persist state across recompositions
    // and configuration changes (like screen rotations)
    var sourceStation by rememberSaveable { mutableStateOf("") }
    var destinationStation by rememberSaveable { mutableStateOf("") }

    Box(modifier = Modifier
        .padding(0.1.dp)
        .background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Metro Route Finder",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            // Source station dropdown
            StationSelector(
                label = "Source Station",
                stations = stations,
                selectedStation = sourceStation,
                onStationSelected = { sourceStation = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Destination station dropdown
            StationSelector(
                label = "Destination Station",
                stations = stations,
                selectedStation = destinationStation,
                onStationSelected = { destinationStation = it }
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Find Route Button
            val context = LocalContext.current
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Button(
                    onClick = {
                        if (sourceStation.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please select source station",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        } else if (destinationStation.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Please select destination station",
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (sourceStation == destinationStation) {
                            Toast.makeText(
                                context,
                                "Both Source and Destination are same",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            val sourceId = stations.find { it.name == sourceStation }?.id
                            val destId = stations.find { it.name == destinationStation }?.id

                            if (sourceId != null && destId != null) {
                                // Navigate to route display screen with sourceId and destId as parameters
                                navController.navigate("routeDisplay/$sourceId/$destId")
                            }
                        }
                    },
                    modifier = Modifier.size(120.dp, 60.dp),
                    shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Show Route", fontSize = 16.sp)
                }
            }
        }
    }
}

// Corrected spelling of "Magenta"
fun getLineColor(line: String): Color {
    return when (line) {
        "Red" -> Color(0xFFF94449)
        "Blue" -> Color(0xFF0041C2)
        "Green" -> Color(0xFF48A860)
        "Yellow" -> Color(0xFFFFD700)
        "Violet" -> Color(0xFF743089)
        "Mergenta" -> Color(0XFFFF00FF)
        "Pink" -> Color(0XFFFFC0CB)
        else -> Color.Gray
    }
}