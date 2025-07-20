package com.ttaaa.flysto.pathsimplifier.io

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint
import com.ttaaa.flysto.pathsimplifier.utils.EARTH_RADIUS
import org.slf4j.LoggerFactory
import kotlin.math.max
import kotlin.random.Random

object FlightDataGenerator {
    private val logger = LoggerFactory.getLogger(FlightDataGenerator::class.java)

    fun createFlightPath(majorWayPointsCount: Int = 10, totalPointsCount: Int? = null): FlightPath {
        logger.info("Creating sample flight path with $majorWayPointsCount major points " +
                "and total amount ${totalPointsCount ?: majorWayPointsCount}")

        val points = mutableListOf<SphericalPoint>()

        val majorWaypoints = List(majorWayPointsCount) { generateSphericalPoint() }
        var waypointsLeft = max(majorWayPointsCount, totalPointsCount ?: majorWayPointsCount)

        // Create long straight segments with high sampling rate
        for (i in 0 until majorWaypoints.size - 1) {
            val current = majorWaypoints[i]
            val next = majorWaypoints[i + 1]

            val intervalSize = (waypointsLeft - 1) / (majorWaypoints.size - i - 1)

            for (j in 0 until intervalSize) {
                val t = j.toDouble() / intervalSize
                val lat = current.latitude + t * (next.latitude - current.latitude)
                val lng = current.longitude + t * (next.longitude - current.longitude)

                // Add realistic GPS noise
                val noiseLat = lat + (Math.random() - 0.5) * 0.0008
                val noiseLng = lng + (Math.random() - 0.5) * 0.0008

                points.add(SphericalPoint(noiseLat, noiseLng))
                waypointsLeft--
            }
        }
        points.add(majorWaypoints.last())

        logger.info("Generated ${points.size} sample points")
        return FlightPath(points, EARTH_RADIUS)
    }

    private fun generateSphericalPoint() =
        SphericalPoint(Random.nextDouble(-90.0, 90.0), Random.nextDouble(-180.0, 180.0))
}
