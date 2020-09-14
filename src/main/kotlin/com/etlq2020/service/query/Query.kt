package com.etlq2020.service.query

import com.etlq2020.controller.dto.QueryDto
import com.etlq2020.service.query.filter.FilterSubQuery
import com.mongodb.client.model.Aggregates
import org.bson.Document
import org.bson.conversions.Bson

class Query(private val groupBy: List<String>?, private val filterBy: List<FilterSubQuery>?,
            private val aggregate: List<AggregateSubQuery>,
            private val project: List<ProjectSubQuery>?) {

    companion object {
        fun fromDto(queryDto: QueryDto): Query {
            return Query(queryDto.groupBy, queryDto.filterBy, queryDto.aggregate, queryDto.project)
        }
    }

    fun createQueryPipeline(): List<Bson> {
        return listOfNotNull(
                createFilterByPredicates(),
                createGroupByPredicates(),
                createProjectPredicates()
        ).flatten()
    }

    private fun createFilterByPredicates(): List<Bson>? {
        if (filterBy == null || filterBy.isEmpty()) {
            return null
        }
        return filterBy.map { it.getPredicate() }
    }

    private fun createGroupByPredicates(): List<Bson>? {
        val aggregatePredicates = aggregate.map { it.getPredicate() }
        if (groupBy == null || groupBy.isEmpty()) {
            return listOf(Aggregates.group(null, aggregatePredicates))
        }
        val id = Document()
        groupBy.forEach {
            id.append(it, "$${it}")
        }
        return listOf(Aggregates.group(id, aggregatePredicates))
    }

    private fun createProjectPredicates(): List<Bson>? {
        if (project == null || project.isEmpty()) {
            return null
        }
        return project.map { it.getPredicate() }
    }

}

