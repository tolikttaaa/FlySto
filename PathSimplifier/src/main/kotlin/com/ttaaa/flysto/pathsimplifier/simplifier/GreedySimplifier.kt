package com.ttaaa.flysto.pathsimplifier.simplifier

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint
import org.slf4j.LoggerFactory

object GreedySimplifier : Simplifier(SimplifierType.GREEDY) {
    private val logger = LoggerFactory.getLogger(GreedySimplifier::class.java)

    override fun simplifyProcess(
        path: FlightPath,
        maxDeviationKm: Double
    ): FlightPath {
        if (path.points.size >= 100_000) {
            logger.warn(
                "Duration of path simplification by Greedy algorithm may be too long, " +
                        "due too large amount of points in initial path: ${path.points.size}"
            )
        }

        if (path.points.size <= 2) return path

        var startIndex = 0
        val result = mutableListOf<SphericalPoint>()
        result.add(path.points[startIndex])

        while (startIndex < path.points.size - 1) {
            val segmentStart = path.points[startIndex]
            var endIndex = startIndex + 1

            while (endIndex < path.points.size) {
                val segmentEnd = path.points[endIndex]

                var deviates = (startIndex + 1 until endIndex)
                    .map { path.points[it].perpendicularDistanceToGreatCircleSegment(segmentStart, segmentEnd) }
                    .any { dist -> dist > maxDeviationKm }

                if (deviates) break
                else endIndex++
            }

            startIndex = endIndex - 1
            result.add(path.points[startIndex])
        }

        return FlightPath(result, path.radius)
    }
}
