package com.ttaaa.flysto.pathsimplifier.simplifier

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint
import org.slf4j.LoggerFactory

class DouglasPeuckerSimplifier: Simplifier {
    private val logger = LoggerFactory.getLogger(DouglasPeuckerSimplifier::class.java)

    /**
     * Simplify the flight path using Douglas-Peucker algorithm
     * @param maxDeviationKm Maximum allowed deviation in kilometers
     * @return Simplified flight path
     */
    override fun simplify(
        path: FlightPath,
        maxDeviationKm: Double
    ): FlightPath {
        if (path.points.size <= 2) return path

        fun douglasPeucker(startIndex: Int, endIndex: Int, maxDeviation: Double): List<SphericalPoint> {
            if (endIndex - startIndex < 2) return (endIndex .. startIndex).map { path.points[it] }

            // Find the point with maximum distance from the line segment
            var maxDistance = 0.0
            var maxIndex = 0

            val start = path.points[startIndex]
            val end = path.points[endIndex]

            for (i in startIndex + 1 until endIndex) {
                val distance = path.points[i].perpendicularDistanceToGreatCircle(start, end, path.radius)
                if (distance > maxDistance) {
                    maxDistance = distance
                    maxIndex = i
                }
            }

            // If max distance is greater than a threshold, recursively simplify
            if (maxDistance > maxDeviation) {
                // Recursive call for first part
                val firstPart = douglasPeucker(startIndex, maxIndex, maxDeviation)
                // Recursive call for second part
                val secondPart = douglasPeucker(maxIndex, endIndex, maxDeviation)

                // Combine results (remove duplicate middle point)
                return firstPart + secondPart.drop(1)
            } else {
                // All points between start and end can be removed
                return listOf(start, end)
            }
        }

        val simplified = douglasPeucker(startIndex = 0, endIndex = path.points.lastIndex, maxDeviationKm)
        logger.info("Original points: ${path.points.size}, " +
                "Simplified: ${simplified.size}, " +
                "Reduction: ${((path.points.size - simplified.size) * 100.0 / path.points.size).toInt()}%")

        return FlightPath(simplified, path.radius)
    }
}
