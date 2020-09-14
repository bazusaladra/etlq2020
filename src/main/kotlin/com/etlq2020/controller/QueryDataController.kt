package com.etlq2020.controller

import com.etlq2020.controller.dto.QueryDto
import com.etlq2020.service.QueryDataService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger


@RestController
class QueryDataController(val queryDataService: QueryDataService) {

    companion object {
        private val logger: Logger = Logger.getLogger(QueryDataController::class.java.name)
    }

    @PostMapping("/query", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun saveInputData(@RequestBody query: QueryDto): Map<String, Any> {
        logger.info("Received a query $query")
        val results = queryDataService.query(query)
        return mapOf("results" to results)
    }

}