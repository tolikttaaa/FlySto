package com.ttaaa.flysto.pathsimplifier.model

import com.ttaaa.flysto.pathsimplifier.utils.EARTH_RADIUS
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
        // Angular distance from start to end
        val angDistStartToPoint = angularDistance(segStart, this)
        val initialBearingStartToPoint = initialBearing(segStart, this)
        val initialBearingStartToEnd = initialBearing(segStart, segEnd)

        // Cross-track distance
        val dXt = asin(sin(angDistStartToPoint) * sin(initialBearingStartToPoint - initialBearingStartToEnd)) * radius

        // Along-track distance (how far along the segment the projection lies)
        val dAt = acos(cos(angDistStartToPoint) / cos(dXt / radius)) * radius

        // Total segment length
        val segmentLength = angularDistance(segStart, segEnd) * radius

        return when {
            dAt < 0 -> haversineDistance(this, segStart)
            dAt > segmentLength -> haversineDistance(this, segEnd)
            else -> abs(dXt)
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
