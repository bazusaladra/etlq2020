package com.etlq2020

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger


@RestController
class QueryDataController(val databaseClient: DatabaseClient) {

    companion object {
        private val logger: Logger = Logger.getLogger(QueryDataController::class.java.name)
    }

    @PostMapping("/query", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun saveInputData(@RequestBody queryDto: QueryDto): Map<String, Any> {
        logger.info("Received a query $queryDto")
        val results = databaseClient.query(queryDto)
        return mapOf("results" to results)
    }

    data class QueryDto(val groupBy: List<String>?, val filterBy: List<FilterByDto>?,
                        val aggregate: AggregateDto)

    data class FilterByDto(val field: String, val condition: ConditionDto)

    data class ConditionDto(val operation: FilterOperation, val parameter: String, val type:Type) {
        enum class FilterOperation {
            EQUALS
        }
        enum class Type {
            LOCAL_DATE
        }
    }

    data class AggregateDto(val field: String, val operation: Operation) {
        enum class Operation {
            SUM
        }
    }
}