package com.example.delhimetronavigationapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.navigation.NavController
import com.example.delhimetronavigationapp.Route
import com.example.delhimetronavigationapp.Station
import com.example.delhimetronavigationapp.createMetroData
import com.example.delhimetronavigationapp.findShortestRoute
import com.example.delhimetronavigationapp.getLineColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDisplayScreen(sourceId: String, destId: String, navController: NavController) {
    // Metro network data
    val metroData = remember { createMetroData() }
    val stations = metroData.first
    val connections = metroData.second

    // Get source and destination station names for display
    val sourceStation = stations.find { it.id == sourceId }?.name ?: "Unknown"
    val destStation = stations.find { it.id == destId }?.name ?: "Unknown"

    // Calculate route
    val calculatedRoute = remember(sourceId, destId) {
        findShortestRoute(sourceId, destId, stations, connections)
    }
    Scaffold(
        topBar = {
            // Use the current TopAppBar from Material 3
            TopAppBar(
                title = {
                    Text("Route Details", color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.DarkGray
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.background(Color.LightGray)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = sourceStation,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.Black
                    )
                    Text(
                        text = "    ->    ",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.Black
                    )
                    Text(
                        text = destStation,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.Black
                    )
                }


                // Display route
                calculatedRoute?.let { route ->
                    RouteDisplay(route = route, stations = stations)
                }
            }
        }

    }
}



// Fixed RouteDisplay to handle index bounds correctly
@Composable
fun RouteDisplay(route: Route, stations: List<Station>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.background(Color.White)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = "Total Travel Time: ${route.totalTime} minutes",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.Black
                )

                Text(
                    text = "Line Changes: ${if (route.lineChanges.isNotEmpty()) route.lineChanges.size - 1 else 0}",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Color.Black
                )

                Divider()

                LazyColumn {
                    items(route.stations.indices.toList()) { index ->
                        val stationId = route.stations[index]
                        val station = stations.find { it.id == stationId }

                        // Determine the current line color and next line color
                        val currentLine = when {
                            // If we're at the start of the journey
                            index == 0 && route.lines.isNotEmpty() -> route.lines.first()
                            // For stations between start and end, use previous segment's line
                            index > 0 && index - 1 < route.lines.size -> route.lines[index - 1]
                            // Fallback
                            else -> station?.lines?.firstOrNull() ?: ""
                        }

                        // Determine if this station is where a line change happens
                        val isLineChange = index < route.stations.size - 1 &&
                                index < route.lines.size - 1 &&
                                index >= 0 &&
                                route.lines[index] != currentLine

                        // Get the next line if there's a line change
                        val nextLine = if (isLineChange && index < route.lines.size) {
                            route.lines[index]
                        } else {
                            null
                        }

                        val isInterchange = (station?.lines?.size ?: 0) > 1

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Line indicator with correct color
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .background(
                                        getLineColor(currentLine),
                                        RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$index",
                                    color = Color.Black,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(32.dp))

                            Column {
                                Text(
                                    text = station?.name ?: "",
                                    fontWeight = if (isInterchange) FontWeight.ExtraBold else FontWeight.Normal,
                                    fontSize = 21.sp,
                                    color = Color.Black
                                )

                                // Show line change information at the current station, not the next one
                                if (isLineChange && nextLine != null) {
                                    Text(
                                        text = "Change from $currentLine to $nextLine",
                                        fontSize = 12.sp,
                                        color = Color.Red
                                    )
                                }
                            }
                        }
                        if (index < route.stations.size - 1) {
                            // Use the next segment's line color for the connecting line
                            val lineColorForSegment = if (index < route.lines.size) {
                                route.lines[index]
                            } else {
                                currentLine
                            }

                            Box(
                                modifier = Modifier
                                    .padding(start = 29.dp)
                                    .width(2.dp)
                                    .height(40.dp)
                                    .background(getLineColor(lineColorForSegment))
                            )
                        }
                    }
                }
            }
        }
    }
}
