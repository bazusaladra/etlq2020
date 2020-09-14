package com.etlq2020.service

import com.etlq2020.controller.dto.DataRowDto
import org.bson.Document
import java.time.LocalDate

data class DataRow(private val datasource: String, private val campaign: String,
                   private val daily: LocalDate, private val clicks: Int, private val impressions: Int) {

    fun toDocument(): Document {
        return Document().append("datasource", datasource)
                .append("campaign", campaign)
                .append("daily", daily)
                .append("clicks", clicks)
                .append("impressions", impressions)
    }

    fun getID(): Document {
        return Document().append("datasource", datasource)
                .append("campaign", campaign)
                .append("daily", daily)
    }

    companion object {
        fun fromDto(dto: DataRowDto): DataRow {
            return DataRow(dto.datasource, dto.campaign, dto.daily, dto.clicks, dto.impressions)
        }
    }
}