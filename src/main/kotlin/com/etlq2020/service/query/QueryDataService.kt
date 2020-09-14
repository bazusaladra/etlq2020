package com.etlq2020.service.query

import com.etlq2020.controller.dto.QueryDto
import com.etlq2020.repository.DataRepository
import com.etlq2020.service.query.aggregate.AggregateSubQueryCreator
import com.etlq2020.service.query.filter.FilterSubQueryCreator
import com.etlq2020.service.query.project.ProjectSubQueryCreator
import com.mongodb.client.model.Aggregates
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

@Component
class QueryDataService(
        private val filterConditionCreator: FilterSubQueryCreator,
        private val aggregateCreator: AggregateSubQueryCreator,
        private val projectOperationCreator: ProjectSubQueryCreator,
        private val dataRepository: DataRepository
) {

    fun query(query: QueryDto): List<Document> {
        return dataRepository.query(createQueryPipeline(query))
    }

    private fun createQueryPipeline(query: QueryDto): List<Bson> {
        return listOfNotNull(
                createFilterByPredicates(query),
                createGroupByPredicates(query),
                createProjectPredicates(query)
        ).flatten()
    }

    private fun createFilterByPredicates(query: QueryDto): List<Bson>? {
        if (query.filterBy == null || query.filterBy.isEmpty()) {
            return null
        }
        return query.filterBy.map { filterConditionCreator.createPredicate(it) }
    }

    private fun createGroupByPredicates(query: QueryDto): List<Bson>? {
        val aggregate = query.aggregate
        val groupBy = query.groupBy
        val aggregatePredicates = aggregate.map { aggregateCreator.createPredicate(it) }
        if (groupBy == null || groupBy.isEmpty()) {
            return listOf(Aggregates.group(null, aggregatePredicates))
        }
        val id = Document()
        groupBy.forEach {
            id.append(it, "$${it}")
        }
        return listOf(Aggregates.group(id, aggregatePredicates))
    }

    private fun createProjectPredicates(query: QueryDto): List<Bson>? {
        val project = query.project
        if (project == null || project.isEmpty()) {
            return null
        }
        return project.map { projectOperationCreator.createPredicate(it) }
    }

}

