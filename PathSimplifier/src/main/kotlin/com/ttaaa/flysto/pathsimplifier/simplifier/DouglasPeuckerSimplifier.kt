package com.ttaaa.flysto.pathsimplifier.simplifier

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint

object DouglasPeuckerSimplifier : Simplifier(SimplifierType.DOUGLAS_PEUCKER) {
    override fun simplifyProcess(
        path: FlightPath,
        maxDeviationKm: Double
    ): FlightPath {
        if (path.points.size <= 2) return path

        fun douglasPeucker(startIndex: Int, endIndex: Int, maxDeviation: Double): List<SphericalPoint> {
            if (endIndex - startIndex < 2) return (startIndex..endIndex).map { path.points[it] }

            // Find the point with maximum distance from the line segment
            var maxDistance = 0.0
            var maxIndex = -1

            val start = path.points[startIndex]
            val end = path.points[endIndex]

            for (i in startIndex + 1 until endIndex) {
                val distance = path.points[i].perpendicularDistanceToGreatCircleSegment(start, end, path.radius)

                if (distance > maxDistance) {
                    maxDistance = distance
                    maxIndex = i
                }
            }

            // If max distance is greater than a threshold, recursively simplify
            return if (maxDistance > maxDeviation) {
                // Recursive call for first part
                val firstPart = douglasPeucker(startIndex, maxIndex, maxDeviation)
                // Recursive call for second part
                val secondPart = douglasPeucker(maxIndex, endIndex, maxDeviation)

                // Combine results (remove duplicate middle point)
                (firstPart + secondPart.drop(1))
            } else {
                // All points between start and end can be removed
                listOf(start, end)
            }
        }

        return FlightPath(
            douglasPeucker(startIndex = 0, endIndex = path.points.lastIndex, maxDeviationKm),
            path.radius
        )
    }
}
