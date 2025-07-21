package com.ttaaa.flysto.pathsimplifier

import com.ttaaa.flysto.pathsimplifier.PathSimplifyingProcess.ProcessInputData
import com.ttaaa.flysto.pathsimplifier.simplifier.Simplifier.SimplifierType
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
        names = ["-f", "--file"],
        description = ["Path to input csv file."],
    )
    var filePath: String? = null

    @Option(
        names = ["-o", "--outputDir"],
        description = ["Path to output directory."],
    )
    var outputDirectory: String? = null

    @Option(
        names = ["-g", "--generate"],
        arity = "1..2", // allow 1 or 2 integers
        description = ["Amount of major way points and total amount of waypoints to generate."],
    )
    var generationParams: List<Int> = emptyList()

    @Option(
        names = ["-d", "--deviation"],
        description = ["Maximum allowed deviation in kilometers."],
        required = true
    )
    var maxDeviation: Double = 0.0

    @Option(
        names = ["-a", "--algorithms"],
        description = ["List of algorithms to run (comma-separated). Default: all existing algorithms."],
        split = ","
    )
    var algorithms: List<SimplifierType> = SimplifierType.entries.toList() // default: all enum values

    override fun call(): Int {
        if (!((filePath == null) xor (generationParams.isEmpty()))) {
            logger.error("Specify either --file OR --generate (not both)!")
            return 1
        }

        if (maxDeviation < 0) {
            logger.error("Deviation param should not be less than zero!")
            return 1
        }

        try {
            PathSimplifyingProcess.process(
                ProcessInputData(
                    inputFilePath = filePath,
                    majorWayPointsAmount = generationParams.getOrNull(0),
                    totalWayPointsAmount = generationParams.getOrNull(1),
                    outputDirectory = outputDirectory,
                    maxDeviation = maxDeviation,
                    simplifierTypes = algorithms
                )
            )
        } catch (ex: Exception) {
            logger.error("Error during execution", ex)
            return 1
        }
        return 0
    }
}

fun main(args: Array<String>) {
    val exitCode = CommandLine(PathSimplifierApp()).execute(*args)
    exitProcess(exitCode)
}
