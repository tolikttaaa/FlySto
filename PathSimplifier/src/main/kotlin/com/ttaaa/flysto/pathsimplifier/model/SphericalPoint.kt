package com.ttaaa.flysto.pathsimplifier.model

import com.ttaaa.flysto.pathsimplifier.utils.EARTH_RADIUS
import kotlin.math.PI
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
    fun perpendicularDistanceToGreatCircleSegment(
        segStart: SphericalPoint,
        segEnd: SphericalPoint,
        radius: Double = EARTH_RADIUS,
    ): Double {
        val angDistStartToPoint = angularDistance(segStart, this)
        // If the point is the start of the segment, distance is 0
        if (angDistStartToPoint == 0.0) return 0.0

        val initialBearingStartToPoint = initialBearing(segStart, this)
        val initialBearingStartToEnd = initialBearing(segStart, segEnd)

        // Angular cross-track distance
        val angDistXt = asin(sin(angDistStartToPoint) * sin(initialBearingStartToPoint - initialBearingStartToEnd))

        // Angular along-track distance
        var angDistAt = acos(cos(angDistStartToPoint) / cos(angDistXt))

        // The point's projection is "behind" the start point if the bearing difference is obtuse.
        // In this case, we consider the along-track distance negative.
        if (abs(initialBearingStartToEnd - initialBearingStartToPoint) > PI / 2) {
            angDistAt = -angDistAt
        }

        val dAt = angDistAt * radius
        val segmentLength = haversineDistance(segStart, segEnd, radius)

        return when {
            // The Projection is before the start of the segment
            dAt < 0 -> haversineDistance(this, segStart, radius)
            // The Projection is after the end of the segment
            dAt > segmentLength -> haversineDistance(this, segEnd, radius)
            // The Projection is on the segment; return the cross-track distance
            else -> abs(angDistXt * radius)
        }
    }

    companion object {
        // Angular distance on a sphere (radians)
        fun angularDistance(a: SphericalPoint, b: SphericalPoint): Double {
            val aLat = Math.toRadians(a.latitude)
            val bLat = Math.toRadians(b.latitude)
            val aLong = Math.toRadians(a.longitude)
            val bLong = Math.toRadians(b.longitude)

            val dLat = bLat - aLat
            val dLong = bLong - aLong

            val a = sin(dLat / 2) * sin(dLat / 2) +
                    cos(aLat) * cos(bLat) * sin(dLong / 2) * sin(dLong / 2)
            return 2 * atan2(sqrt(a), sqrt(1 - a))
        }

        // Initial bearing from point A to B in radians
        fun initialBearing(a: SphericalPoint, b: SphericalPoint): Double {
            val aLat = Math.toRadians(a.latitude)
            val aLong = Math.toRadians(a.longitude)
            val bLat = Math.toRadians(b.latitude)
            val bLong = Math.toRadians(b.longitude)

            val y = sin(bLong - aLong) * cos(bLat)
            val x = cos(aLat) * sin(bLat) - sin(aLat) * cos(bLat) * cos(bLong - aLong)
            return atan2(y, x)
        }

        // Haversine formula
        fun haversineDistance(a: SphericalPoint, b: SphericalPoint, radius: Double = EARTH_RADIUS): Double {
            return radius * angularDistance(a, b)
        }
    }
}
