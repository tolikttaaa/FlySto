package com.ttaaa.flysto.pathsimplifier.io

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter

object FlightDataWriter {
    private val logger = LoggerFactory.getLogger(FlightDataWriter::class.java)

    fun writeFlightPath(data: FlightPath, outputDirectory: String?, outputFileName: String) {
        logger.info("Initiate writing flight path to CSV $outputFileName")

        val outputFile = try {
            (outputDirectory?.let { File(it, outputFileName) } ?: File(outputFileName))
                .also { it.parentFile?.mkdirs() }
        } catch (ex: Exception) {
            logger.error("Failed to create output file $outputFileName", ex)
            error("Failed to open output file $outputFileName")
        }

        FileWriter(outputFile).use { writer ->
            CSVPrinter(writer, CSVFormat.DEFAULT.builder().setHeader("lat", "lng").build()).use { csvPrinter ->
                for (point in data.points) {
                    csvPrinter.printRecord(point.latitude, point.longitude)
                }

                csvPrinter.flush()
            }
        }

        logger.info("CSV written to ${outputFile.absolutePath}")
    }
}
