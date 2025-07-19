package com.ttaaa.flysto.pathsimplifier

import com.ttaaa.flysto.pathsimplifier.io.FlightDataGenerator
import com.ttaaa.flysto.pathsimplifier.io.FlightDataReader
import com.ttaaa.flysto.pathsimplifier.io.FlightDataWriter
import com.ttaaa.flysto.pathsimplifier.simplifier.DouglasPeuckerSimplifier
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.Command
import picocli.CommandLine.Option
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@Command(
    name = "PathSimplifierApp",
    mixinStandardHelpOptions = true,
    description = ["A Kotlin CLI app for simplifying flight paths (lat/lng, WGS84)"],
)
class PathSimplifierApp : Callable<Int> {
    private val logger = LoggerFactory.getLogger(PathSimplifierApp::class.java)

    @Option(
        names = ["-file"],
        description = ["Path to input csv file"],
        required = false
    )
    var filePath: String? = null

    @Option(
        names = ["-output"],
        description = ["Path to output directory"],
        required = false
    )
    var outputDirectory: String? = null

    @Option(
        names = ["-generate"],
        arity = "1..2", // allow 1 or 2 integers
        description = ["Amount of major way points and total amount of waypoints"],
        required = false
    )
    var generationParams: List<Int>? = null

    @Option(
        names = ["-deviation"],
        description = ["Maximum allowed deviation in kilometers"],
        required = true
    )
    var maxDeviation: Double = 0.0

    override fun call(): Int {
        if ((filePath == null && generationParams == null) || (filePath != null && generationParams != null)) {
            logger.error("Specify either -file OR -generate (not both)")
            return 1
        }

        if (maxDeviation <= 0.0) {
            logger.error("Deviation param should not be less than zero")
            return 1
        }

        try {
            process()
        } catch (ex: Exception) {
            logger.error("Error during execution", ex)
            return 1
        }
        return 0
    }

    private fun process() {
        val inputFlightPath = if (filePath != null) {
            FlightDataReader.readFlightPath(filePath!!)
        } else if (generationParams != null) {
            when (generationParams!!.size) {
                1 -> FlightDataGenerator.createFlightPath(generationParams!![0])
                2 -> FlightDataGenerator.createFlightPath(generationParams!![0], generationParams!![1])
                else -> {
                    error("Generate params should be specified correctly")
                }
            }
        } else {
            error("Application params should be specified correctly")
        }

        FlightDataWriter.writeFlightPath(inputFlightPath, outputDirectory, "filtered_input.csv")

        val simplifiedPath = DouglasPeuckerSimplifier.simplify(inputFlightPath, maxDeviation)

        FlightDataWriter.writeFlightPath(simplifiedPath, outputDirectory, "simplified_path.csv")
    }
}

fun main(args: Array<String>) {
    val exitCode = CommandLine(PathSimplifierApp()).execute(*args)
    exitProcess(exitCode)
}
