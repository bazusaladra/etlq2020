package com.etlq2020.service.query

import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.BsonField


data class AggregateSubQuery(val field: String, val operation: Operation) {

    interface AggregateSupplier {
        fun getAggregatePredicate(field: String): BsonField
    }

    enum class Operation(private val supplier: AggregateSupplier) {
        SUM(object : AggregateSupplier {
            override fun getAggregatePredicate(field: String): BsonField {
                return Accumulators.sum(field, "$${field}")
            }
        });

        fun getPredicate(field: String): BsonField {
            return supplier.getAggregatePredicate(field)
        }
    }

    fun getPredicate(): BsonField {
        return operation.getPredicate(field)
    }
}