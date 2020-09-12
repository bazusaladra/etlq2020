package com.etlq2020

import com.fasterxml.jackson.databind.MappingIterator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream
import java.io.StringReader
import java.util.logging.Logger


@RestController
class DataInputController {

    companion object {
        private val logger: Logger = Logger.getLogger(DataInputController::class.java.name)
    }

    @PostMapping("/send", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun getApplicationLiveStatus(@RequestBody requestBody: String): Map<String, Any> {
        val loadObjectList = loadObjectList(DataRow::class.java, requestBody)
        return listOf("loaded" to loadObjectList.size).toMap()
    }

    fun <T> loadObjectList(type: Class<T>?, inputStream: String): List<DataRow> {
        return StringReader(inputStream).readLines().drop(1).map {
            val split = it.split(',')
            DataRow(split[0], split[1], split[2], split[3].toInt(), split[4].toInt())
        }
    }

    data class DataRow(val datasource: String, val campaign: String,
                       val daily: String, val clicks: Int, val impressions: Int) {

    }
}