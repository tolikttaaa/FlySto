package com.ttaaa.flysto.pathsimplifier.simplifier

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint
import org.slf4j.LoggerFactory

object GreedySimplifier: Simplifier {
    private val logger = LoggerFactory.getLogger(GreedySimplifier::class.java)

    override fun simplify(
        path: FlightPath,
        maxDeviationKm: Double
    ): FlightPath {
        logger.info("Simplifying path by Greedy algorithm, with max deviation = ${maxDeviationKm}km")

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

        val simplified = FlightPath(result, path.radius)

        logger.info("Path simplification by Greedy algorithm is done")
        logger.info("Original points: {}, Simplified: {}, Reduction: {}%",
            path.points.size,
            simplified.points.size,
            ((path.points.size - simplified.points.size) * 100.0 / path.points.size).toInt()
        )
        logger.info("Original distance: {}, Simplified: {}, Losses: {}%",
            path.getTotalLength(),
            simplified.getTotalLength(),
            ((path.getTotalLength() - simplified.getTotalLength()) * 100.0 / path.getTotalLength()).toInt()
        )
        return simplified
    }
}
