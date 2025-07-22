package com.ttaaa.flysto.pathsimplifier

import com.ttaaa.flysto.pathsimplifier.PathSimplifyingProcess.ProcessInputData
import com.ttaaa.flysto.pathsimplifier.simplifier.Simplifier.SimplifierType
import io.mockk.every
import io.mockk.just
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkObject
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PathSimplifierAppTest {
    private lateinit var app: PathSimplifierApp

    // Mock the singleton object that handles the processing logic
    @BeforeEach
    fun setUp() {
        app = PathSimplifierApp()
        mockkObject(PathSimplifyingProcess)
        // Default stub: assume the process runs successfully without doing anything
        every { PathSimplifyingProcess.process(any()) } just runs
    }

    @AfterEach
    fun tearDown() {
        // Clear the mock after each test to avoid side-effects
        unmockkObject(PathSimplifyingProcess)
    }

    @Nested
    @DisplayName("Input Validation")
    inner class InputValidation {

        @Test
        fun `call should return 1 when both file and generate are provided`() {
            // Arrange
            app.filePath = "some/file.csv"
            app.generationParams = listOf(10, 100)
            app.maxDeviation = 10.0

            // Act
            val exitCode = app.call()

            // Assert
            assertThat(exitCode).isEqualTo(1)
            // Verify the process method was never called
            verify(exactly = 0) { PathSimplifyingProcess.process(any()) }
        }

        @Test
        fun `call should return 1 when neither file nor generate are provided`() {
            // Arrange
            app.filePath = null
            app.generationParams = emptyList()
            app.maxDeviation = 10.0

            // Act
            val exitCode = app.call()

            // Assert
            assertThat(exitCode).isEqualTo(1)
            verify(exactly = 0) { PathSimplifyingProcess.process(any()) }
        }

        @Test
        fun `call should return 1 when deviation is negative`() {
            // Arrange
            app.filePath = "some/file.csv"
            app.maxDeviation = -5.0

            // Act
            val exitCode = app.call()

            // Assert
            assertThat(exitCode).isEqualTo(1)
            verify(exactly = 0) { PathSimplifyingProcess.process(any()) }
        }
    }

    @Nested
    @DisplayName("Successful Execution")
    inner class SuccessfulExecution {

        @Test
        fun `call should return 0 and trigger process when valid file is provided`() {
            // Arrange
            app.filePath = "input/data.csv"
            app.maxDeviation = 15.0
            app.outputDirectory = "output/dir"
            app.algorithms = listOf(SimplifierType.DOUGLAS_PEUCKER)

            // Act
            val exitCode = app.call()

            // Assert
            assertThat(exitCode).isEqualTo(0)

            val expectedInput = ProcessInputData(
                inputFilePath = "input/data.csv",
                majorWayPointsAmount = null,
                totalWayPointsAmount = null,
                outputDirectory = "output/dir",
                maxDeviation = 15.0,
                simplifierTypes = listOf(SimplifierType.DOUGLAS_PEUCKER)
            )
            verify(exactly = 1) { PathSimplifyingProcess.process(expectedInput) }
        }

        @Test
        fun `call should return 0 and trigger process with two generation params`() {
            // Arrange
            app.generationParams = listOf(5, 50)
            app.maxDeviation = 20.0
            // Use the default value for algorithms

            // Act
            val exitCode = app.call()

            // Assert
            assertThat(exitCode).isEqualTo(0)

            val expectedInput = ProcessInputData(
                inputFilePath = null,
                majorWayPointsAmount = 5,
                totalWayPointsAmount = 50,
                outputDirectory = null,
                maxDeviation = 20.0,
                simplifierTypes = SimplifierType.entries.toList() // Verify default
            )
            verify(exactly = 1) { PathSimplifyingProcess.process(expectedInput) }
        }

        @Test
        fun `call should return 0 and trigger process with one generation param`() {
            // Arrange
            app.generationParams = listOf(10)
            app.maxDeviation = 25.0

            // Act
            val exitCode = app.call()

            // Assert
            assertThat(exitCode).isEqualTo(0)

            val expectedInput = ProcessInputData(
                inputFilePath = null,
                majorWayPointsAmount = 10,
                totalWayPointsAmount = null,
                outputDirectory = null,
                maxDeviation = 25.0,
                simplifierTypes = SimplifierType.entries.toList()
            )
            verify(exactly = 1) { PathSimplifyingProcess.process(expectedInput) }
        }
    }

    @Nested
    @DisplayName("Error Handling")
    inner class ErrorHandling {

        @Test
        fun `call should return 1 when process throws an exception`() {
            // Arrange
            val exception = RuntimeException("Processing failed!")
            every { PathSimplifyingProcess.process(any()) } throws exception

            app.filePath = "input/data.csv"
            app.maxDeviation = 10.0

            // Act
            val exitCode = app.call()

            // Assert
            assertThat(exitCode).isEqualTo(1)
            // Verify that the process was still attempted
            verify(exactly = 1) { PathSimplifyingProcess.process(any()) }
        }
    }
}
