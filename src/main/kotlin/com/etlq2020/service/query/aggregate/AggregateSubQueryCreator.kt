package com.etlq2020.service.query.aggregate

import com.etlq2020.controller.dto.AggregateDto
import com.mongodb.client.model.BsonField
import org.springframework.stereotype.Component

@Component
class AggregateSubQueryCreator(val aggregatePredicateSuppliers: List<AggregatePredicateSupplier>) {

    init {
        AggregateDto.OperationType.values().forEach { type ->
            if (aggregatePredicateSuppliers.filter { it.shouldHandle(type) }.size > 1) {
                throw IllegalStateException("multiple implementations for type $type")
            }
        }
    }

    fun createPredicate(aggregateDto: AggregateDto): BsonField {
        val operationType = aggregateDto.operation
        val field = aggregateDto.field
        return aggregatePredicateSuppliers.find { it.shouldHandle(operationType) }!!
                .createPredicate(field)
    }
}
