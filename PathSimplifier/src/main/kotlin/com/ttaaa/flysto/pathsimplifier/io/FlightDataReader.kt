package com.ttaaa.flysto.pathsimplifier.io

import com.ttaaa.flysto.pathsimplifier.model.FlightPath
import com.ttaaa.flysto.pathsimplifier.model.SphericalPoint
import com.ttaaa.flysto.pathsimplifier.utils.EARTH_RADIUS
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.StandardCharsets

object FlightDataReader {
    private val logger = LoggerFactory.getLogger(FlightDataReader::class.java)

    fun readFlightPath(filename: String): FlightPath {
        val points = mutableListOf<SphericalPoint>()
        var invalidLines = 0

        try {
            val file = File(filename)
            if (!file.exists()) {
                logger.error("File $filename does not exists!")
                error("File $filename does not exists!")
            }

            file.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                CSVParser(
                    reader, CSVFormat.DEFAULT.builder().setHeader()
                        .setSkipHeaderRecord(true).build()
                ).use { parser ->
                    for (record in parser) {
                        try {
                            val lat = record.get("lat").toDouble()
                            val lng = record.get("lng").toDouble()

                            if (!isValidCoordinate(lat, lng)) {
                                throw IllegalArgumentException("Invalid coordinate: $lat, $lng")
                            }

                            val point = SphericalPoint(lat.toDouble(), lng.toDouble())
                            points.add(point)
                            logger.trace("Processing point: {}", point)
                        } catch (_: Exception) {
                            invalidLines++
                            logger.warn("Skipping row with invalid params: $record")
                        }
                    }
                }
            }

        } catch (ex: Exception) {
            logger.error("Error reading file: ${ex.message}")
            error("Error reading file: ${ex.message}")
        }

        logger.info("Successfully parsed ${points.size} points")
        if (invalidLines > 0) {
            logger.warn("Skipped $invalidLines invalid lines")
        }

        return FlightPath(points, EARTH_RADIUS)
    }

    private fun isValidCoordinate(lat: Double, lng: Double): Boolean {
        return lat >= -90.0 && lat <= 90.0 && lng >= -180.0 && lng <= 180.0
    }
}
