package com.ttaaa.flysto.pathsimplifier.simplifier

import com.ttaaa.flysto.pathsimplifier.model.FlightPath

interface Simplifier {
    fun simplify(path: FlightPath, maxDeviationKm: Double): FlightPath

    enum class SimplifierType {
        GREEDY,
        DOUGLAS_PEUCKER
    }
}
