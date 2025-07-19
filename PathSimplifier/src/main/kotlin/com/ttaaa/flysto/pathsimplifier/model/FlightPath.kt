package com.ttaaa.flysto.pathsimplifier.model

class FlightPath(
    val points: List<SphericalPoint>,
    val radius: Double,             // radius of the sphere in km
) {
    fun getTotalLength(): Double {
        var totalDistance = 0.0
        for (i in 1 until points.size) {
            totalDistance += SphericalPoint.Companion.haversineDistance(points[i - 1], points[i], radius)
        }
        return totalDistance
    }
}
