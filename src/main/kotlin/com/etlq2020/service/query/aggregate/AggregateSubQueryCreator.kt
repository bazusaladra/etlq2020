package com.etlq2020.service.query.aggregate

import com.etlq2020.controller.dto.AggregateDto
import com.mongodb.client.model.BsonField
import org.springframework.stereotype.Component

@Component
class AggregateSubQueryCreator(val aggregatePredicateSuppliers: List<AggregatePredicateSupplier>) {
    fun createPredicate(aggregateDto: AggregateDto): BsonField {
        val operationType = aggregateDto.operation
        val field = aggregateDto.field
        return aggregatePredicateSuppliers.find { it.shouldHandle(operationType) }!!
                .createPredicate(field)
    }
}
