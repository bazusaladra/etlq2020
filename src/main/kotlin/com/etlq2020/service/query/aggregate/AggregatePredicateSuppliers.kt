package com.etlq2020.service.query.aggregate

import com.etlq2020.controller.dto.AggregateDto
import com.etlq2020.service.query.QueryDtoHandler
import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.BsonField
import org.springframework.stereotype.Component

interface AggregatePredicateSupplier : QueryDtoHandler<AggregateDto.OperationType> {
    fun createPredicate(field: String): BsonField
}

@Component
class SumAggregatePredicateSupplier : AggregatePredicateSupplier {
    override fun shouldHandle(operation: AggregateDto.OperationType): Boolean {
        return operation == AggregateDto.OperationType.SUM
    }

    override fun createPredicate(field: String): BsonField {
        return Accumulators.sum(field, "$${field}")
    }

}
