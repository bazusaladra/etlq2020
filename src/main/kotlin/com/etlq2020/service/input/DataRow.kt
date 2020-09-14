package com.etlq2020.service.input

import com.etlq2020.controller.dto.DataRowDto
import org.bson.Document
import java.time.LocalDate

data class DataRow(private val datasource: String, private val campaign: String,
                   private val daily: LocalDate, private val clicks: Int, private val impressions: Int) {

    fun toDocument(): Document {
        return Document().append("Datasource", datasource)
                .append("Campaign", campaign)
                .append("Daily", daily)
                .append("Clicks", clicks)
                .append("Impressions", impressions)
    }

    fun getID(): Document {
        return Document().append("Datasource", datasource)
                .append("Campaign", campaign)
                .append("Daily", daily)
    }

    companion object {
        fun fromDto(dto: DataRowDto): DataRow {
            return DataRow(dto.datasource, dto.campaign, dto.daily, dto.clicks, dto.impressions)
        }
    }
}