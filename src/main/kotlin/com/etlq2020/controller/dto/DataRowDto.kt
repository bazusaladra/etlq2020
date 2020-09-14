package com.etlq2020.controller.dto

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DataRowDto(val datasource: String, val campaign: String,
                      val daily: LocalDate, val clicks: Int, val impressions: Int) {

    companion object {
        fun fromString(it: String): DataRowDto {
            val split = it.split(',')
            val daily = parseLocalDate(split[2])
            return DataRowDto(split[0], split[1], daily, split[3].toInt(), split[4].toInt())
        }

        fun parseLocalDate(text: String): LocalDate =
                LocalDate.parse(text, DateTimeFormatter.ofPattern("MM/dd/uu"))

    }
}
