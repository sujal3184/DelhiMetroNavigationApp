package com.example.delhimetronavigationapp.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.delhimetronavigationapp.Station
import com.example.delhimetronavigationapp.getLineColor

@Composable
fun StationSelector(
    label: String,
    stations: List<Station>,
    selectedStation: String,
    onStationSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Filter stations based on search query
    val filteredStations = remember(searchQuery, stations) {
        if (searchQuery.isEmpty()) {
            stations.sortedBy { it.name }
        } else {
            stations.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }.sortedBy { it.name }
        }
    }

    Column {
        Text(
            text = label,
            modifier = Modifier.padding(bottom = 8.dp),
            color = Color.Black
        )

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth().background(Color.LightGray)
                    .clickable {
                        expanded = true
                        searchQuery = "" // Reset search query when opening dropdown
                    }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Show colored boxes for the selected station with interchange logic
                    val selectedStationObj = stations.find { it.name == selectedStation }
                    if (selectedStationObj != null) {
                        Row(
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            // Check if it's an interchange station
                            if (selectedStationObj.lines.size > 1) {
                                // For interchange stations, show a single gray box
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(Color.Gray, RoundedCornerShape(4.dp))
                                        .padding(end = 4.dp)
                                )
                            } else {
                                // For non-interchange stations, show the actual line color
                                selectedStationObj.lines.forEach { line ->
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(getLineColor(line), RoundedCornerShape(4.dp))
                                            .padding(end = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }

                    Text(
                        text = selectedStation.ifEmpty { "Select a station" },color = Color.Black
                    )
                }
            }

            // Full-screen dropdown implementation
            if (expanded) {
                Dialog(
                    onDismissRequest = {
                        expanded = false
                        searchQuery = "" // Reset search query when closing dropdown
                    },
                    properties = DialogProperties(
                        dismissOnBackPress = true,
                        dismissOnClickOutside = true,
                        usePlatformDefaultWidth = false
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.White
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Header with back button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = {
                                    expanded = false
                                    searchQuery = ""
                                }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Close",
                                        tint = Color.Black
                                    )
                                }

                                Text(
                                    text = "Select $label",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(start = 16.dp),
                                    color = Color.Black
                                )
                            }

                            Divider()

                            // Fixed search bar at the top
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textStyle = TextStyle(color = Color.Black, fontSize = 20.sp),
                                placeholder = { Text("Search stations...",color = Color.Black) },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Clear search",
                                                tint = Color.Black,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            )

                            // Scrollable content (takes remaining space)
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp)
                            ) {
                                // Show "No stations found" message if filtered list is empty
                                if (filteredStations.isEmpty()) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("No stations found", color = Color.Gray)
                                        }
                                    }
                                } else {
                                    // List of filtered stations
                                    items(filteredStations) { station ->
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    onStationSelected(station.name)
                                                    expanded = false
                                                    searchQuery = ""
                                                }
                                                .padding(vertical = 16.dp)
                                        ) {
                                            Column {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    // Color indicators with interchange logic
                                                    Row(
                                                        modifier = Modifier.padding(end = 8.dp)
                                                    ) {
                                                        // Apply the same interchange logic in dropdown items
                                                        if (station.lines.size > 1) {
                                                            // For interchange stations, show a single gray box
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(72.dp)
                                                                    .background(Color.Gray, RoundedCornerShape(4.dp))
                                                                    .padding(end = 4.dp)
                                                            )
                                                        } else {
                                                            // For non-interchange stations, show the actual line colors
                                                            station.lines.forEach { line ->
                                                                Box(
                                                                    modifier = Modifier
                                                                        .size(72.dp)
                                                                        .background(getLineColor(line), RoundedCornerShape(4.dp))
                                                                        .padding(end = 4.dp)
                                                                )
                                                            }
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.width(32.dp))

                                                    Column {
                                                        Text(
                                                            station.name,
                                                            fontSize = 21.sp,
                                                            color = Color.Black
                                                        )

                                                        // Add "Interchange" label for stations with multiple lines
                                                        if (station.lines.size > 1) {
                                                            Text(
                                                                "Interchange",
                                                                fontSize = 14.sp,
                                                                color = Color.Gray
                                                            )

                                                            // Show all line colors as small boxes
                                                            Row(
                                                                modifier = Modifier.padding(top = 4.dp)
                                                            ) {
                                                                station.lines.forEach { line ->
                                                                    Box(
                                                                        modifier = Modifier
                                                                            .size(12.dp)
                                                                            .background(getLineColor(line), RoundedCornerShape(2.dp))
                                                                    )
                                                                    Spacer(modifier = Modifier.width(4.dp))
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Add divider between items
                                        if (station != filteredStations.last()) {
                                            Divider(color = Color.LightGray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
