package com.etlq2020.service

import com.etlq2020.controller.dto.QueryDto
import com.etlq2020.repository.DataRepository
import com.etlq2020.service.query.Query
import org.bson.Document
import org.springframework.stereotype.Component

@Component
class QueryDataService(private val dataRepository: DataRepository) {

    fun query(query: QueryDto): List<Document> {
        return dataRepository.query(Query.fromDto(query))
    }

}