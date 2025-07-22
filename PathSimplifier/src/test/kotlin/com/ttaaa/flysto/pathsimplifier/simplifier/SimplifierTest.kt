package com.ttaaa.flysto.pathsimplifier.simplifier

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SimplifierTest {
    @ParameterizedTest
    @MethodSource("singleTwoPointPathTestArgumentsProvider")
    fun `Simplification method should return the same path if it has 2 or fewer points`(
        simplifier: Simplifier,
        path: FlightPath
    ) {
        val simplifiedPath = simplifier.simplify(path, 1.0)
        assertEquals(path, simplifiedPath)
    }

    @ParameterizedTest
    @MethodSource("simplifierArgumentsProvider")
    fun `Simplification method should remove points within the maxDeviationKm`(simplifier: Simplifier) {
        val points = listOf(
            EQUATOR_PRIME_MERIDIAN,
            POINT_NEAR_EQUATOR_PRIME_MERIDIAN, // Close to the line
            EQUATOR_90E,
        )
        val path = FlightPath(points, 6371.0)
        val simplifiedPath = simplifier.simplify(path, 1.0) // 1 km max deviation

        assertEquals(2, simplifiedPath.points.size)
        assertEquals(EQUATOR_PRIME_MERIDIAN, simplifiedPath.points.first())
        assertEquals(EQUATOR_90E, simplifiedPath.points.last())
    }

    @ParameterizedTest
    @MethodSource("simplifierArgumentsProvider")
    fun `Simplification method should keep points further than the maxDeviationKm`(simplifier: Simplifier) {
        val points = listOf(
            EQUATOR_PRIME_MERIDIAN,
            EQUATOR_90E, // Far from the line
            NORTH_POLE,
        )
        val path = FlightPath(points, 6371.0)
        val simplifiedPath = simplifier.simplify(path, 0.01) // 0.01 km max deviation

        assertEquals(3, simplifiedPath.points.size)
    }

    @ParameterizedTest
    @MethodSource("simplifierArgumentsProvider")
    fun `Simplification method should handle a more complex path`(simplifier: Simplifier) {
        val points = listOf(
            SOUTH_POLE,
            SOUTH_HALF,
            EQUATOR_PRIME_MERIDIAN,
            EQUATOR_90E,
            EQUATOR_PRIME_MERIDIAN,
            NORTH_HALF,
            NORTH_POLE
        )
        val path = FlightPath(points, 6371.0)
        val simplifiedPath = simplifier.simplify(path, 0.1)

        assertEquals(5, simplifiedPath.points.size)
    }

    @ParameterizedTest
    @MethodSource("simplifierArgumentsProvider")
    fun `Simplification method should handle a straight line path`(simplifier: Simplifier) {
        val points = listOf(
            SOUTH_POLE,
            SOUTH_HALF,
            EQUATOR_PRIME_MERIDIAN,
            NORTH_HALF,
            NORTH_POLE
        )
        val path = FlightPath(points, 6371.0)
        val simplifiedPath = simplifier.simplify(path, 1.0)

        assertEquals(2, simplifiedPath.points.size)
        assertEquals(SOUTH_POLE, simplifiedPath.points.first())
        assertEquals(NORTH_POLE, simplifiedPath.points.last())
    }

    @ParameterizedTest
    @MethodSource("simplifierArgumentsProvider")
    fun `Simplification method should handle a straight line path with shuffled points`(simplifier: Simplifier) {
        val points = listOf(
            SOUTH_POLE,
            NORTH_POLE,
            SOUTH_HALF,
            NORTH_HALF,
            EQUATOR_PRIME_MERIDIAN,
        )
        val path = FlightPath(points, 6371.0)
        val simplifiedPath = simplifier.simplify(path, 1.0)

        assertEquals(5, simplifiedPath.points.size)
    }

    companion object {
        // Test points
        val NORTH_POLE = SphericalPoint(90.0, 0.0)
        val NORTH_HALF = SphericalPoint(45.0, 0.0)
        val SOUTH_POLE = SphericalPoint(-90.0, 0.0)
        val SOUTH_HALF = SphericalPoint(-45.0, 0.0)
        val EQUATOR_PRIME_MERIDIAN = SphericalPoint(0.0, 0.0)
        val POINT_NEAR_EQUATOR_PRIME_MERIDIAN = SphericalPoint(0.0001, 0.0001)
        val EQUATOR_90E = SphericalPoint(0.0, 90.0)

        @JvmStatic
        fun singleTwoPointPathTestArgumentsProvider() =
            Stream.of(
                Arguments.of(DouglasPeuckerSimplifier, FlightPath(listOf(EQUATOR_PRIME_MERIDIAN, EQUATOR_90E), 6371.0)),
                Arguments.of(GreedySimplifier, FlightPath(listOf(EQUATOR_PRIME_MERIDIAN, EQUATOR_90E), 6371.0)),
                Arguments.of(DouglasPeuckerSimplifier, FlightPath(listOf(EQUATOR_PRIME_MERIDIAN), 6371.0)),
                Arguments.of(GreedySimplifier, FlightPath(listOf(EQUATOR_PRIME_MERIDIAN), 6371.0)),
            )

        @JvmStatic
        fun simplifierArgumentsProvider() =
            Stream.of(
                Arguments.of(DouglasPeuckerSimplifier),
                Arguments.of(GreedySimplifier),
            )
    }
}
