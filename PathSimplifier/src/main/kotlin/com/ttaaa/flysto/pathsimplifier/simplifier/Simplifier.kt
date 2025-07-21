package com.ttaaa.flysto.pathsimplifier.simplifier

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import org.slf4j.LoggerFactory

abstract class Simplifier(
    private val simplifierType: SimplifierType
) {
    private val logger = LoggerFactory.getLogger(Simplifier::class.java)

    fun simplify(path: FlightPath, maxDeviationKm: Double): FlightPath {
        logger.info(
            "Simplifying path by ${simplifierType.stringName} algorithm, " +
                    "with max deviation = ${maxDeviationKm}km"
        )

        val startTime = System.currentTimeMillis()
        val simplified = simplifyProcess(path, maxDeviationKm)
        val endTime = System.currentTimeMillis()

        logger.info("Path simplification by ${simplifierType.stringName} algorithm is done")
        logger.info(
            "Original points: {}, Simplified: {}, Reduction: {}%",
            path.points.size,
            simplified.points.size,
            ((path.points.size - simplified.points.size) * 100.0 / path.points.size).toInt()
        )
        logger.info(
            "Original distance: {}, Simplified: {}, Losses: {}%",
            path.getTotalLength(),
            simplified.getTotalLength(),
            ((path.getTotalLength() - simplified.getTotalLength()) * 100.0 / path.getTotalLength()).toInt()
        )
        logger.info("Total time: ${endTime - startTime}ms")
        return simplified
    }

    protected abstract fun simplifyProcess(path: FlightPath, maxDeviationKm: Double): FlightPath

    enum class SimplifierType(val stringName: String) {
        GREEDY("Greedy"),
        DOUGLAS_PEUCKER("Douglas-Peucker")
    }
}
