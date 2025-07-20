package com.ttaaa.flysto.pathsimplifier.simplifier

object SimplifierFactory {
    fun getSimplifier(type: Simplifier.SimplifierType): Simplifier {
        return when (type) {
            Simplifier.SimplifierType.GREEDY -> GreedySimplifier
            Simplifier.SimplifierType.DOUGLAS_PEUCKER -> DouglasPeuckerSimplifier
            else -> throw IllegalArgumentException("Unknown simplifier type: $type")
        }
    }
}
