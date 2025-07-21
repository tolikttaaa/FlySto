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
        logger.info("Reading flight path from file: $filename")

        var points = listOf<SphericalPoint>()

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
                    points = parseRows(parser)
                }
            }
        } catch (ex: Exception) {
            logger.error("Error reading file: ${ex.message}")
            error("Error reading file: ${ex.message}")
        }

        logger.info("Successfully parsed ${points.size} points")
        return FlightPath(points, EARTH_RADIUS)
    }

    private fun parseRows(parser: CSVParser): List<SphericalPoint> {
        val rows = mutableListOf<SphericalPoint>()
        val (latInd, lngInd) = getLatLngInd(parser.headerMap)
        for (record in parser) {
            try {
                val lat = record.get(latInd).toDouble()
                val lng = record.get(lngInd).toDouble()

                require(isValidCoordinate(lat, lng))

                val point = SphericalPoint(lat.toDouble(), lng.toDouble())
                rows.add(point)
                logger.trace("Processing point: {}", point)
            } catch (_: Exception) {
                logger.warn("Skipping row with invalid params: $record")
            }
        }

        return rows
    }

    private fun getLatLngInd(headerMap: Map<String, Int>): Pair<Int, Int> {
        val latKey = headerMap.keys.firstOrNull { header -> header.lowercase().contains("lat") } ?: {
            logger.error("Failed to find column associated with latitude. Headers: ${headerMap.keys}")
            error("Failed to find column associated with latitude. Add \'lat\' or \'latitude\' to column header!")
        }
        val lngKey = headerMap.keys.firstOrNull { header ->
            header.lowercase().contains("lon") || header.lowercase().contains("lng")
        } ?: {
            logger.error("Failed to find column associated with longitude. Headers: ${headerMap.keys}")
            error(
                "Failed to find column associated with longitude. " +
                        "Add \'lon\', \'lng\' or \'longitude\' to column header!"
            )
        }

        logger.info("Colum headers associated with latitude: \'$latKey\', longitude: \'$lngKey\'")
        return headerMap.let { it[latKey]!! to it[lngKey]!! }
    }

    private fun isValidCoordinate(lat: Double, lng: Double): Boolean {
        return lat >= -90.0 && lat <= 90.0 && lng >= -180.0 && lng <= 180.0
    }
}
