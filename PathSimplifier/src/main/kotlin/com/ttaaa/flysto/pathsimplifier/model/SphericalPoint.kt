package com.ttaaa.flysto.pathsimplifier.model

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

open class SphericalPoint(
    val latitude: Double,  // -90 to 90 degrees
    val longitude: Double, // -180 to 180 degrees
) {
    /**
     * Calculate perpendicular distance from a point to a great circle arc
     * Uses spherical geometry for accurate distance calculation
     */
    fun perpendicularDistanceToGreatCircle(
        lineStart: SphericalPoint,
        lineEnd: SphericalPoint,
        radius: Double,
    ): Double {
        // Convert to radians
        val lat1 = Math.toRadians(this.latitude)
        val lon1 = Math.toRadians(this.longitude)
        val lat2 = Math.toRadians(lineStart.latitude)
        val lon2 = Math.toRadians(lineStart.longitude)
        val lat3 = Math.toRadians(lineEnd.latitude)
        val lon3 = Math.toRadians(lineEnd.longitude)

        // Calculate cross-track distance (perpendicular distance to great circle)
        val dLon13 = lon1 - lon3
        val dLon23 = lon2 - lon3

        val a = sin(lat1) * sin(lat3) + cos(lat1) * cos(lat3) * cos(dLon13)

        val c1 = acos(a.coerceIn(-1.0, 1.0))

        // Calculate bearing from lineEnd to both points
        val bearing13 = atan2(sin(dLon13) * cos(lat1),
            cos(lat3) * sin(lat1) - sin(lat3) * cos(lat1) * cos(dLon13))
        val bearing23 = atan2(sin(dLon23) * cos(lat2),
            cos(lat3) * sin(lat2) - sin(lat3) * cos(lat2) * cos(dLon23))

        val dBearing = bearing13 - bearing23

        // Cross-track distance
        val crossTrackDistance = abs(asin(sin(c1) * sin(dBearing))) * radius

        return crossTrackDistance
    }

    companion object {
        @JvmStatic
        fun haversineDistance(p1: SphericalPoint, p2: SphericalPoint, radius: Double = 1.0): Double {
            val dLat = Math.toRadians(p2.latitude - p1.latitude)
            val dLon = Math.toRadians(p2.longitude - p1.longitude)
            val lat1 = Math.toRadians(p1.latitude)
            val lat2 = Math.toRadians(p2.latitude)

            val a = sin(dLat/2) * sin(dLat/2) + sin(dLon/2) * sin(dLon/2) * cos(lat1) * cos(lat2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return radius * c
        }
    }
}
