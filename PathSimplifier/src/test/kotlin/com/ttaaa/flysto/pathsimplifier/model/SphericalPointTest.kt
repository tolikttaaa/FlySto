package com.ttaaa.flysto.pathsimplifier.model

import com.ttaaa.flysto.pathsimplifier.utils.EARTH_RADIUS
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.math.PI

class SphericalPointTest {
    @Nested
    inner class CompanionObjectMethodTests {
        @Test
        fun `angularDistance between identical points should be zero`() {
            val distance = SphericalPoint.angularDistance(LONDON, LONDON)
            assertEquals(0.0, distance, DELTA)
        }

        @Test
        fun `angularDistance between North Pole and Equator should be PI divided by 2`() {
            val distance = SphericalPoint.angularDistance(NORTH_POLE, EQUATOR_PRIME_MERIDIAN)
            assertEquals(PI / 2, distance, DELTA)
        }

        @Test
        fun `angularDistance between antipodal points should be PI`() {
            val distance = SphericalPoint.angularDistance(NORTH_POLE, SOUTH_POLE)
            assertEquals(PI, distance, DELTA)
        }

        @Test
        fun `haversineDistance should correctly multiply angular distance by radius`() {
            val angularDistance = SphericalPoint.angularDistance(LONDON, PARIS)
            val expected = angularDistance * EARTH_RADIUS
            val actual = SphericalPoint.haversineDistance(LONDON, PARIS)
            assertEquals(expected, actual, DELTA)
            // Known distance is ~344 km
            assertEquals(343.934, actual, 1.0)
        }

        @Test
        fun `initialBearing from Equator to North Pole should be 0 (North)`() {
            val bearing = SphericalPoint.initialBearing(EQUATOR_PRIME_MERIDIAN, NORTH_POLE)
            assertEquals(0.0, bearing, DELTA)
        }

        @Test
        fun `initialBearing from North Pole to Equator should be PI (South)`() {
            val bearing = SphericalPoint.initialBearing(NORTH_POLE, EQUATOR_PRIME_MERIDIAN)
            assertEquals(PI, bearing, DELTA)
        }

        @Test
        fun `initialBearing from Equator eastwards should be PI divided by 2 (East)`() {
            val bearing = SphericalPoint.initialBearing(EQUATOR_PRIME_MERIDIAN, EQUATOR_90E)
            assertEquals(PI / 2, bearing, DELTA)
        }
    }

    @Nested
    inner class PerpendicularDistanceTests {

        // Segment along the equator from 0 to 10 degrees longitude
        private val segStart = SphericalPoint(0.0, 0.0)
        private val segEnd = SphericalPoint(0.0, 10.0)

        @Test
        fun `distance for a point whose projection is on the segment`() {
            // Point is at (1, 5), "above" the middle of the segment
            val point = SphericalPoint(1.0, 5.0)
            // The shortest distance is the cross-track distance, which is ~1 degree of latitude
            val expected = SphericalPoint.haversineDistance(point, SphericalPoint(0.0, 5.0)) // ~111.3km
            val actual = point.perpendicularDistanceToGreatCircleSegment(segStart, segEnd)
            assertEquals(expected, actual, 1.0)
        }

        @Test
        fun `distance for a point whose projection is after the segment end`() {
            // Point is at (1, 15), its projection on the equator is at (0, 15)
            val pointAfterEnd = SphericalPoint(1.0, 15.0)
            // The shortest distance should be to the end of the segment
            val expected = SphericalPoint.haversineDistance(pointAfterEnd, segEnd)
            val actual = pointAfterEnd.perpendicularDistanceToGreatCircleSegment(segStart, segEnd)
            assertEquals(expected, actual, DELTA)
        }

        @Test
        fun `distance for a point whose projection is before the segment start`() {
            // Point is at (1, -5), its projection on the equator is at (0, -5)
            val pointBeforeStart = SphericalPoint(1.0, -5.0)
            // The shortest distance should be to the start of the segment
            val expected = SphericalPoint.haversineDistance(pointBeforeStart, segStart)
            val actual = pointBeforeStart.perpendicularDistanceToGreatCircleSegment(segStart, segEnd)
            assertEquals(expected, actual, DELTA)
        }

        @Test
        fun `distance for a point that is the segment start point`() {
            val actual = segStart.perpendicularDistanceToGreatCircleSegment(segStart, segEnd)
            assertEquals(0.0, actual, DELTA)
        }

        @Test
        fun `distance for a point that is the segment end point`() {
            val actual = segEnd.perpendicularDistanceToGreatCircleSegment(segStart, segEnd)
            assertEquals(0.0, actual, DELTA)
        }
    }

    companion object {
        // A small delta for comparing floating-point numbers
        private const val DELTA = 1E-6

        // Test points
        val NORTH_POLE = SphericalPoint(90.0, 0.0)
        val SOUTH_POLE = SphericalPoint(-90.0, 0.0)
        val EQUATOR_PRIME_MERIDIAN = SphericalPoint(0.0, 0.0)
        val EQUATOR_90E = SphericalPoint(0.0, 90.0)
        val LONDON = SphericalPoint(51.5072, -0.1276)
        val PARIS = SphericalPoint(48.8566, 2.3522)
    }
}
