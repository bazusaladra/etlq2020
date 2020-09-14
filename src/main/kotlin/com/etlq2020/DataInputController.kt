package com.etlq2020

import org.bson.Document
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.logging.Logger


@RestController
class DataInputController(val databaseClient: DatabaseClient) {

    companion object {
        private val logger: Logger = Logger.getLogger(DataInputController::class.java.name)
    }

    @PostMapping("/send", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun saveInputData(requestBody: InputStream): Map<String, Any> {
        val dataRows = checkCsvHeader(requestBody)
        var processed = 0L
        while (true) {
            val line = dataRows.readLine() ?: break
            val dataRow = DataRow.fromString(line)
            databaseClient.upsertDataRow(dataRow)
            ++processed
        }
        return listOf("loaded" to processed).toMap()
    }

    fun checkCsvHeader(inputStream: InputStream): BufferedReader {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val headerRow = reader.readLine()
        val headers = headerRow.split(',').toList()
        if (headers != listOf("Datasource", "Campaign", "Daily", "Clicks", "Impressions")) {
            throw IllegalArgumentException("wrong headers format $headers")
        }
        return reader
    }

    data class DataRow(val datasource: String, val campaign: String,
                       val daily: LocalDate, val clicks: Int, val impressions: Int) {

        fun toDocument(): Document {
            return Document().append("datasource", datasource)
                    .append("campain", campaign)
                    .append("daily", daily)
                    .append("clicks", clicks)
                    .append("impressions", impressions)
        }


        fun getID(): Document {
            return Document().append("datasource", datasource)
                    .append("campain", campaign)
                    .append("daily", daily)
        }

        companion object {
            fun fromString(it: String): DataRow {
                val split = it.split(',')
                val daily = parseLocalDate(split[2])
                return DataRow(split[0], split[1], daily, split[3].toInt(), split[4].toInt())
            }

            fun parseLocalDate(text: String) =
                    LocalDate.parse(text, DateTimeFormatter.ofPattern("MM/dd/uu"))

        }
    }
}