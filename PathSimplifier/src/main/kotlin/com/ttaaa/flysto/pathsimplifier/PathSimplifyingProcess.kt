package com.ttaaa.flysto.pathsimplifier

import com.ttaaa.flysto.pathsimplifier.io.FlightDataGenerator
import com.ttaaa.flysto.pathsimplifier.io.FlightDataReader
import com.ttaaa.flysto.pathsimplifier.io.FlightDataWriter
import com.ttaaa.flysto.pathsimplifier.simplifier.Simplifier
import com.ttaaa.flysto.pathsimplifier.simplifier.SimplifierFactory

object PathSimplifyingProcess {
    fun process(inputParams: ProcessInputData) {
        require(inputParams.maxDeviation >= 0)
        require((inputParams.inputFilePath != null) xor (inputParams.majorWayPointsAmount != null))

        // get initial flight path
        val inputFlightPath = if (inputParams.inputFilePath != null) {
            FlightDataReader.readFlightPath(inputParams.inputFilePath)
        } else { // if (inputParams.majorWayPointsAmount != null)
            FlightDataGenerator.createFlightPath(
                inputParams.majorWayPointsAmount!!,
                inputParams.totalWayPointsAmount
            )
        }
        // save filtered flight path for analysis
        FlightDataWriter.writeFlightPath(inputFlightPath, inputParams.outputDirectory, "filtered_input.csv")

        inputParams.simplifierTypes
            .map { simplifierType -> SimplifierFactory.getSimplifier(simplifierType) to simplifierType }
            .forEach { (simplifier, simplifierType) ->
                // simplifying a flight path by multiple algorithms
                val simplifiedPath = simplifier.simplify(inputFlightPath, inputParams.maxDeviation)

                // save simplified flight path
                FlightDataWriter.writeFlightPath(
                    simplifiedPath,
                    inputParams.outputDirectory,
                    "simplified_path_${simplifierType.stringName}.csv"
                )
            }
    }

    data class ProcessInputData(
        val inputFilePath: String?,
        val majorWayPointsAmount: Int?,
        val totalWayPointsAmount: Int?,
        val outputDirectory: String?,
        val maxDeviation: Double,
        val simplifierTypes: List<Simplifier.SimplifierType>
    )
}
