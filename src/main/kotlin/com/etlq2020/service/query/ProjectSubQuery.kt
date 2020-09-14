package com.etlq2020.service.query

import com.mongodb.client.model.Aggregates
import org.bson.Document
import org.bson.conversions.Bson

data class ProjectSubQuery(val field: String, val operation: ProjectOperation) {

    fun getPredicate(): Bson {
        return Aggregates.project(Document(field, operation.getPredicate()))
    }
}

data class ProjectOperation(val type: ProjectOperationType, val parameters: List<String>) {

    interface ProjectOperationPredicateSupplier {
        fun getPredicate(parameters: List<String>): Document
    }

    enum class ProjectOperationType(private val supplier: ProjectOperationPredicateSupplier) {
        DIVIDE(object : ProjectOperationPredicateSupplier {
            override fun getPredicate(parameters: List<String>): Document {
                return Document("\$divide", parameters.map { "\$$it" })
            }
        });

        fun getPredicate(parameters: List<String>): Document {
            return supplier.getPredicate(parameters)
        }
    }

    fun getPredicate(): Document {
        return type.getPredicate(parameters)
    }
}
