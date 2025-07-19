package com.ttaaa.flysto.pathsimplifier.io

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint
import com.ttaaa.flysto.pathsimplifier.utils.EARTH_RADIUS

object FlightDataGenerator {
    /**
     * Create a sample flight path for demonstration when no CSV is provided
     */
    fun createComplexSampleFlightPath(): FlightPath {
        println("Creating sample flight path (Transcontinental US)")

        val points = mutableListOf<SphericalPoint>()

        // Define major waypoints for a complex route (Los Angeles to New York)
        val majorWaypoints = listOf(
            SphericalPoint(34.0522, -118.2437), // Los Angeles
            SphericalPoint(35.0, -115.0),       // Over Nevada
            SphericalPoint(37.0, -110.0),       // Over Utah
            SphericalPoint(39.0, -105.0),       // Over Colorado
            SphericalPoint(41.0, -100.0),       // Over Nebraska
            SphericalPoint(42.0, -95.0),        // Over Iowa
            SphericalPoint(42.0, -87.0),        // Over Illinois
            SphericalPoint(42.0, -83.0),        // Over Michigan
            SphericalPoint(43.0, -79.0),        // Over Ontario
            SphericalPoint(43.0, -75.0),        // Over New York State
            SphericalPoint(40.7128, -74.0060)   // New York City
        )

        // Create long straight segments with high sampling rate
        for (i in 0 until majorWaypoints.size - 1) {
            val current = majorWaypoints[i]
            val next = majorWaypoints[i + 1]

            val intervalSize = 240

            for (j in 0..intervalSize) {
                val t = j.toDouble() / intervalSize
                val lat = current.latitude + t * (next.latitude - current.latitude)
                val lng = current.longitude + t * (next.longitude - current.longitude)

                // Add realistic GPS noise
                val noiseLat = lat + (Math.random() - 0.5) * 0.0008
                val noiseLng = lng + (Math.random() - 0.5) * 0.0008

                points.add(SphericalPoint(noiseLat, noiseLng))
            }
        }

        println("Generated ${points.size} sample points")
        return FlightPath(points, EARTH_RADIUS)
    }
}
