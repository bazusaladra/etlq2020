package com.etlq2020.service.query.filter

import com.etlq2020.controller.dto.ConditionDto
import com.etlq2020.service.query.QueryDtoHandler
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

interface FilterPredicateSupplier : QueryDtoHandler<ConditionDto.FilterOperationType> {
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

@Component
class GteFilterPredicateSupplier : FilterPredicateSupplier {

    override fun shouldHandle(operationType: ConditionDto.FilterOperationType): Boolean {
        return operationType == ConditionDto.FilterOperationType.GTE
    }

    override fun createPredicate(field: String, parameter: Any): Bson {
        return Aggregates.match(Filters.gte(field, parameter))
    }
}

@Component
class LteFilterPredicateSupplier : FilterPredicateSupplier {

    override fun shouldHandle(operationType: ConditionDto.FilterOperationType): Boolean {
        return operationType == ConditionDto.FilterOperationType.LTE
    }

    override fun createPredicate(field: String, parameter: Any): Bson {
        return Aggregates.match(Filters.lte(field, parameter))
    }
}
