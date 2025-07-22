package com.ttaaa.flysto.pathsimplifier.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FlightPathTest {
    @Test
    fun `getTotalLength should return 0 when the path has no points`() {
        val flightPath = FlightPath(emptyList(), 6371.0)
        val totalLength = flightPath.getTotalLength()
        assertEquals(0.0, totalLength, 0.001)
    }

    @Test
    fun `getTotalLength should return 0 when the path has only one point`() {
        val points = listOf(SphericalPoint(0.0, 0.0))
        val flightPath = FlightPath(points, 6371.0)
        val totalLength = flightPath.getTotalLength()
        assertEquals(0.0, totalLength, 0.001)
    }

    @Test
    fun `getTotalLength should calculate the correct distance for a simple path`() {
        val points = listOf(
            SphericalPoint(0.0, 0.0),
            SphericalPoint(0.0, 1.0)
        )
        val flightPath = FlightPath(points, 6371.0)
        val totalLength = flightPath.getTotalLength()
        assertEquals(111.195, totalLength, 0.001)
    }

    @Test
    fun `getTotalLength should calculate the correct distance for a more complex path`() {
        val points = listOf(
            SphericalPoint(0.0, 0.0),
            SphericalPoint(0.0, 1.0),
            SphericalPoint(1.0, 1.0)
        )
        val flightPath = FlightPath(points, 6371.0)
        val totalLength = flightPath.getTotalLength()
        assertEquals(222.390, totalLength, 0.001)
    }

    @Test
    fun `getTotalLength should calculate the correct distance with different radius`() {
        val points = listOf(
            SphericalPoint(0.0, 0.0),
            SphericalPoint(0.0, 1.0)
        )
        val flightPath = FlightPath(points, 1000.0)
        val totalLength = flightPath.getTotalLength()
        assertEquals(17.453, totalLength, 0.001)
    }
}
