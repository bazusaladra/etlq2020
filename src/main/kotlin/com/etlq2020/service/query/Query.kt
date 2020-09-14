package com.etlq2020.service.query

import com.etlq2020.controller.dto.QueryDto
import com.mongodb.client.model.Aggregates
import org.bson.Document
import org.bson.conversions.Bson

class Query(private val groupBy: List<String>?, private val filterBy: List<FilterSubQuery>?,
            private val aggregate: AggregateSubQuery) {

    companion object {
        fun fromDto(queryDto: QueryDto): Query {
            return Query(queryDto.groupBy, queryDto.filterBy, queryDto.aggregate)
        }
    }

    fun createQueryPipeline(): List<Bson> {
        return listOfNotNull(
                createFilterByPredicates(),
                createGroupByPredicates(),
                createAggregatePredicates()
        ).flatten()
    }

    private fun createFilterByPredicates(): List<Bson>? {
        if (filterBy == null || filterBy.isEmpty()) {
            return null
        }
        return filterBy.map { it.getPredicate() }
    }

    private fun createGroupByPredicates(): List<Bson>? {
        if (groupBy == null || groupBy.isEmpty()) {
            return null
        }
        val id = Document()
        groupBy.forEach {
            id.append(it, "$${it.toLowerCase()}")
        }
        return listOf(Aggregates.group(id, aggregate.getPredicate()))
    }

    private fun createAggregatePredicates(): List<Bson>? {
        if (groupBy != null && groupBy.isNotEmpty()) {
            return null
        }
        return null
    }

}

