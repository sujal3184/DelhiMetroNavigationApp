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
import androidx.compose.ui.text.style.TextAlign

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = sourceStation,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = "→",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        modifier = Modifier.weight(1f),
                        text = destStation,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
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



@Composable
fun RouteDisplay(route: Route, stations: List<Station>) {
    // Get connections from the parent component
    val metroData = remember { createMetroData() }
    val connections = metroData.second

    // Calculate total time including walking time for line changes
    val lineChangesCount = if (route.lineChanges.isNotEmpty()) route.lineChanges.size - 1 else 0
    val totalWalkingTime = lineChangesCount * 8 // 8 minutes per line change
    val totalTravelTimeWithWalking = route.totalTime + totalWalkingTime

    // Calculate cumulative time to reach each station
    val timesToStations = remember(route) {
        val times = mutableListOf<Int>()
        var cumulativeTime = 0

        // Add first station with time 0
        times.add(cumulativeTime)

        // Calculate time to reach each subsequent station
        for (i in 0 until route.stations.size - 1) {
            val fromId = route.stations[i]
            val toId = route.stations[i + 1]

            // Get the line for this segment
            val line = if (i < route.lines.size) route.lines[i] else ""

            // Check if there's a line change at the next station
            val nextLine = if (i + 1 < route.lines.size) route.lines[i + 1] else line
            val isLineChange = line != nextLine

            // Add time for the segment (using the appropriate connection)
            val connection = connections.find {
                (it.fromStation == fromId && it.toStation == toId) ||
                        (it.fromStation == toId && it.toStation == fromId)
            }
            val segmentTime = connection?.timeMins ?: 2 // Default 2 minutes if connection not found

            // Add walking time if there's a line change
            val walkingTime = if (isLineChange) 8 else 0

            cumulativeTime += segmentTime
            times.add(cumulativeTime)

            // If there's a line change, add the walking time to the next station's time
            if (isLineChange && i + 1 < times.size) {
                cumulativeTime += walkingTime
                times[i + 1] = cumulativeTime
            }
        }

        times
    }

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
                    text = "Total Travel Time:  ${totalTravelTimeWithWalking} minutes",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.Black
                )

                Text(
                    text = "Line Changes:  ${lineChangesCount}",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider()

                LazyColumn {
                    items(route.stations.indices.toList()) { index ->
                        val stationId = route.stations[index]
                        val station = stations.find { it.id == stationId }

                        // Determine the current line color
                        val currentLine = when {
                            // If we're at the start of the journey
                            index == 0 && route.lines.isNotEmpty() -> route.lines.first()
                            // For stations between start and end, use previous segment's line
                            index > 0 && index - 1 < route.lines.size -> route.lines[index - 1]
                            // Fallback
                            else -> station?.lines?.firstOrNull() ?: ""
                        }

                        // Determine if this is a line change station
                        // We check if the current station is not the last one and
                        // the line after this station is different from the current line
                        val isLineChange = index < route.stations.size - 1 &&
                                index < route.lines.size &&
                                currentLine != route.lines[index]

                        // Get the line we're changing to
                        val nextLine = if (isLineChange) route.lines[index] else null

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

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = station?.name ?: "",
                                    fontWeight = if (isInterchange) FontWeight.ExtraBold else FontWeight.Normal,
                                    fontSize = 21.sp,
                                    color = Color.Black
                                )

                                // Show line change information if this is a line change station
                                if (isLineChange && nextLine != null) {
                                    Text(
                                        text = "Change from $currentLine to $nextLine",
                                        fontSize = 12.sp,
                                        color = Color.Red
                                    )

                                    // Add walking time text
                                    Text(
                                        text = "Walking time: 8 min.",
                                        fontSize = 12.sp,
                                        color = Color.Blue,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Show time to reach on the right side
                            if (index < timesToStations.size) {
                                Text(
                                    text = "~${timesToStations[index]} min",
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.Light,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
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