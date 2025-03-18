package com.example.delhimetronavigationapp

import java.util.PriorityQueue

fun findShortestRoute(
    sourceId: String,
    destId: String,
    stations: List<Station>,
    connections: List<Connection>
): Route {
    // Create adjacency map for the graph
    val graph = mutableMapOf<String, MutableList<Pair<String, Pair<String, Int>>>>()

    for (station in stations) {
        graph[station.id] = mutableListOf()
    }

    for (connection in connections) {
        graph[connection.fromStation]?.add(
            Pair(connection.toStation, Pair(connection.line, connection.timeMins))
        )
        graph[connection.toStation]?.add(
            Pair(connection.fromStation, Pair(connection.line, connection.timeMins))
        )
    }

    // Dijkstra's algorithm
    val distances = mutableMapOf<String, Int>()
    val previous = mutableMapOf<String, String?>()
    val lines = mutableMapOf<String, String>()  // Track the line used to reach each station
    val visited = mutableSetOf<String>()

    // Initialize distances
    for (station in stations) {
        distances[station.id] = Int.MAX_VALUE
        previous[station.id] = null
    }

    distances[sourceId] = 0

    val priorityQueue = PriorityQueue<Pair<String, Int>>(compareBy { it.second })
    priorityQueue.add(Pair(sourceId, 0))

    while (priorityQueue.isNotEmpty()) {
        val (currentId, _) = priorityQueue.poll()

        if (currentId == destId) {
            break
        }

        if (currentId in visited) {
            continue
        }

        visited.add(currentId)

        val neighbors = graph[currentId] ?: continue

        for ((neighborId, connection) in neighbors) {
            val (line, time) = connection
            val currentLine = lines[currentId]

            // Add penalty for line changes (8 minutes instead of 3)
            var lineChangePenalty = 0
            if (currentLine != null && currentLine != line) {
                lineChangePenalty = 8  // Changed from 3 to 8 minutes
            }

            val distance = distances[currentId]!! + time + lineChangePenalty

            if (distance < distances[neighborId]!!) {
                distances[neighborId] = distance
                previous[neighborId] = currentId
                lines[neighborId] = line  // Store which line we used to reach this station
                priorityQueue.add(Pair(neighborId, distance))
            }
        }
    }

    // Reconstruct the route
    val routeStations = mutableListOf<String>()
    val routeLines = mutableListOf<String>()
    var current: String? = destId

    while (current != null) {
        routeStations.add(0, current)
        current = previous[current]
    }

    // Build the lines list for each segment
    if (routeStations.size > 1) {
        for (i in 0 until routeStations.size - 1) {
            val from = routeStations[i]
            val to = routeStations[i + 1]

            // Find the connection between these stations
            val connection = connections.find {
                (it.fromStation == from && it.toStation == to) ||
                        (it.fromStation == to && it.toStation == from)
            }

            if (connection != null) {
                routeLines.add(connection.line)
            } else {
                // Fallback - use the line from the station if connection not found
                val fromStation = stations.find { it.id == from }
                routeLines.add(fromStation?.lines?.firstOrNull() ?: "")
            }
        }
    }

    // Generate the line changes list correctly
    val lineChanges = mutableListOf<String>()
    var prevLine: String? = null

    for (line in routeLines) {
        if (prevLine == null || line != prevLine) {
            lineChanges.add(line)
            prevLine = line
        }
    }

    return Route(
        stations = routeStations,
        totalTime = distances[destId] ?: 0,
        lineChanges = lineChanges,
        lines = routeLines
    )
}