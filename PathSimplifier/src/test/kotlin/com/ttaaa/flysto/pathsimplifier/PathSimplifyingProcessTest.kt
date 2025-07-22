package com.ttaaa.flysto.pathsimplifier

import com.ttaaa.flysto.pathsimplifier.io.FlightDataGenerator
import com.ttaaa.flysto.pathsimplifier.io.FlightDataReader
import com.ttaaa.flysto.pathsimplifier.io.FlightDataWriter
import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint
import com.ttaaa.flysto.pathsimplifier.simplifier.Simplifier
import com.ttaaa.flysto.pathsimplifier.simplifier.SimplifierFactory
import com.ttaaa.flysto.pathsimplifier.utils.EARTH_RADIUS
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class PathSimplifyingProcessTest {
    // JUnit 5 will create a temporary directory for us before each test
    @TempDir
    lateinit var tempDir: Path

    // A mock simplifier to be returned by our mocked factory
    private val mockSimplifier = mockk<Simplifier>()

    @BeforeEach
    fun setUp() {
        // Mock all the static objects that PathSimplifyingProcess depends on
        mockkObject(FlightDataReader, FlightDataGenerator, FlightDataWriter, SimplifierFactory)

        // Define default behavior for the mocked objects to avoid boilerplate in every test
        every { FlightDataReader.readFlightPath(any()) } returns sampleInputPath
        every { FlightDataGenerator.createFlightPath(any(), any()) } returns sampleInputPath
        every { SimplifierFactory.getSimplifier(any()) } returns mockSimplifier
        every { mockSimplifier.simplify(any(), any()) } returns sampleSimplifiedPath
        // `just runs` is used for methods that return Unit
        every { FlightDataWriter.writeFlightPath(any(), any(), any()) } just runs
    }

    @AfterEach
    fun tearDown() {
        // Clear the mocks after each test to ensure test isolation
        unmockkObject(FlightDataReader, FlightDataGenerator, FlightDataWriter, SimplifierFactory)
    }

    @Test
    fun `process should throw IllegalArgumentException when maxDeviation is negative`() {
        val invalidInput = PathSimplifyingProcess.ProcessInputData(
            inputFilePath = "some/path",
            majorWayPointsAmount = null,
            totalWayPointsAmount = null,
            outputDirectory = tempDir.toString(),
            maxDeviation = -1.0,
            simplifierTypes = listOf(Simplifier.SimplifierType.DOUGLAS_PEUCKER)
        )

        assertThrows<IllegalArgumentException> {
            PathSimplifyingProcess.process(invalidInput)
        }
    }

    @Test
    fun `process should throw IllegalArgumentException when no input source is provided`() {
        val invalidInput = PathSimplifyingProcess.ProcessInputData(
            inputFilePath = null,
            majorWayPointsAmount = null,
            totalWayPointsAmount = null,
            outputDirectory = tempDir.toString(),
            maxDeviation = 10.0,
            simplifierTypes = listOf(Simplifier.SimplifierType.DOUGLAS_PEUCKER)
        )

        assertThrows<IllegalArgumentException> {
            PathSimplifyingProcess.process(invalidInput)
        }
    }

    @Test
    fun `process should throw IllegalArgumentException when both input sources are provided`() {
        val invalidInput = PathSimplifyingProcess.ProcessInputData(
            inputFilePath = "some/path",
            majorWayPointsAmount = 10,
            totalWayPointsAmount = 100,
            outputDirectory = tempDir.toString(),
            maxDeviation = 10.0,
            simplifierTypes = listOf(Simplifier.SimplifierType.DOUGLAS_PEUCKER)
        )

        assertThrows<IllegalArgumentException> {
            PathSimplifyingProcess.process(invalidInput)
        }
    }

    @Test
    fun `process should read from file, simplify, and write results when inputFilePath is provided`() {
        val inputFilePath = "input/flight.csv"
        val outputDir = tempDir.toString()
        val maxDeviation = 50.0
        val simplifierType = Simplifier.SimplifierType.DOUGLAS_PEUCKER

        val validInput = PathSimplifyingProcess.ProcessInputData(
            inputFilePath = inputFilePath,
            majorWayPointsAmount = null,
            totalWayPointsAmount = null,
            outputDirectory = outputDir,
            maxDeviation = maxDeviation,
            simplifierTypes = listOf(simplifierType)
        )

        // Act
        PathSimplifyingProcess.process(validInput)

        // Assert / Verify
        verify(exactly = 1) { FlightDataReader.readFlightPath(inputFilePath) }
        verify(exactly = 0) { FlightDataGenerator.createFlightPath(any(), any()) }
        verify(exactly = 1) { SimplifierFactory.getSimplifier(simplifierType) }
        verify(exactly = 1) { mockSimplifier.simplify(sampleInputPath, maxDeviation) }

        // Verify that the initial and simplified paths are written correctly
        verify(exactly = 1) { FlightDataWriter.writeFlightPath(sampleInputPath, outputDir, "filtered_input.csv") }
        verify(exactly = 1) {
            FlightDataWriter.writeFlightPath(
                sampleSimplifiedPath,
                outputDir,
                "simplified_path_${simplifierType.stringName}.csv"
            )
        }
    }

    @Test
    fun `process should generate data, simplify, and write results when majorWayPointsAmount is provided`() {
        val majorPoints = 10
        val totalPoints = 1000
        val outputDir = tempDir.toString()
        val maxDeviation = 50.0
        val simplifierType = Simplifier.SimplifierType.DOUGLAS_PEUCKER

        val validInput = PathSimplifyingProcess.ProcessInputData(
            inputFilePath = null,
            majorWayPointsAmount = majorPoints,
            totalWayPointsAmount = totalPoints,
            outputDirectory = outputDir,
            maxDeviation = maxDeviation,
            simplifierTypes = listOf(simplifierType)
        )

        // Act
        PathSimplifyingProcess.process(validInput)

        // Assert / Verify
        verify(exactly = 0) { FlightDataReader.readFlightPath(any()) }
        verify(exactly = 1) { FlightDataGenerator.createFlightPath(majorPoints, totalPoints) }
        verify(exactly = 1) { SimplifierFactory.getSimplifier(simplifierType) }
        verify(exactly = 1) { mockSimplifier.simplify(sampleInputPath, maxDeviation) }

        // Verify that the initial and simplified paths are written correctly
        verify(exactly = 1) { FlightDataWriter.writeFlightPath(sampleInputPath, outputDir, "filtered_input.csv") }
        verify(exactly = 1) {
            FlightDataWriter.writeFlightPath(
                sampleSimplifiedPath,
                outputDir,
                "simplified_path_${simplifierType.stringName}.csv"
            )
        }
    }

    @Test
    fun `process should handle multiple simplifier types`() {
        val outputDir = tempDir.toString()
        val simplifierTypes = listOf(
            Simplifier.SimplifierType.DOUGLAS_PEUCKER,
            Simplifier.SimplifierType.GREEDY
        )

        val validInput = PathSimplifyingProcess.ProcessInputData(
            inputFilePath = "input/flight.csv",
            majorWayPointsAmount = null,
            totalWayPointsAmount = null,
            outputDirectory = outputDir,
            maxDeviation = 50.0,
            simplifierTypes = simplifierTypes
        )

        // Act
        PathSimplifyingProcess.process(validInput)

        // Assert / Verify
        verify(exactly = 1) { FlightDataWriter.writeFlightPath(sampleInputPath, outputDir, "filtered_input.csv") }

        // Verify that simplify and write are called for each type
        for (type in simplifierTypes) {
            verify(exactly = 1) { SimplifierFactory.getSimplifier(type) }
            verify(exactly = 1) {
                FlightDataWriter.writeFlightPath(
                    sampleSimplifiedPath,
                    outputDir,
                    "simplified_path_${type.stringName}.csv"
                )
            }
        }
        verify(exactly = 2) { mockSimplifier.simplify(sampleInputPath, 50.0) }
    }

    companion object {
        // Test points
        private val NORTH_POLE = SphericalPoint(90.0, 0.0)
        private val NORTH_HALF = SphericalPoint(45.0, 0.0)
        private val EQUATOR_PRIME_MERIDIAN = SphericalPoint(0.0, 0.0)

        // Sample data for tests
        val sampleInputPath = FlightPath(listOf(EQUATOR_PRIME_MERIDIAN, NORTH_HALF, NORTH_POLE), EARTH_RADIUS)
        val sampleSimplifiedPath = FlightPath(listOf(EQUATOR_PRIME_MERIDIAN, NORTH_POLE), EARTH_RADIUS)
    }
}
