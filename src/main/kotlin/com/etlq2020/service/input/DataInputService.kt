package com.etlq2020.service.input

import com.etlq2020.controller.dto.DataRowDto
import com.etlq2020.repository.DataRepository
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

@Component
class DataInputService(private val dataRepository: DataRepository) {

    fun persist(inputStream: InputStream): Long {
        val dataRows = checkCsvHeader(inputStream)
        var processed = 0L
        while (true) {
            val line = dataRows.readLine() ?: break
            val dataRow = DataRowDto.fromString(line)
            dataRepository.upsertDataRow(DataRow.fromDto(dataRow))
            ++processed
        }
        return processed
    }

    private fun checkCsvHeader(inputStream: InputStream): BufferedReader {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val headerRow = reader.readLine()
        val headers = headerRow.split(',').toList()
        if (headers != listOf("Datasource", "Campaign", "Daily", "Clicks", "Impressions")) {
            throw IllegalArgumentException("wrong headers format $headers")
        }
        return reader
    }

}