package com.etlq2020.service.query.filter

import com.etlq2020.controller.dto.ConditionDto
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

interface FilterPredicateSupplier {
    fun shouldHandle(operationType: ConditionDto.FilterOperationType): Boolean
    fun createPredicate(field: String, parameter: Any): Bson
}

@Component
class EqualsFilterPredicateSupplier : FilterPredicateSupplier {

    override fun shouldHandle(operationType: ConditionDto.FilterOperationType): Boolean {
        return operationType == ConditionDto.FilterOperationType.EQUALS
    }

    override fun createPredicate(field: String, parameter: Any): Bson {
        return Aggregates.match(Filters.eq(field, parameter))
    }

}