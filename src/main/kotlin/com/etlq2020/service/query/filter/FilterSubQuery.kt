package com.etlq2020.service.query.filter

import com.etlq2020.controller.dto.DataRowDto.Companion.parseLocalDate
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson

data class FilterSubQuery(val field: String, val condition: Condition) {

    fun getPredicate(): Bson {
        return condition.getPredicate(field)
    }

}

data class Condition(val operation: FilterOperation, val parameter: String, val type: Type) {
    private interface FilterPredicateSupplier {
        fun getPredicate(field: String, parameter: Any): Bson
    }

    enum class FilterOperation(private val predicateSupplier: FilterPredicateSupplier) {
        EQUALS(object : FilterPredicateSupplier {
            override fun getPredicate(field: String, parameter: Any): Bson {
                return Aggregates.match(Filters.eq(field, parameter))
            }
        });

        fun getPredicate(field: String, parameter: Any): Bson {
            return predicateSupplier.getPredicate(field, parameter)
        }
    }

    fun getPredicate(field: String): Bson {
        return operation.getPredicate(field, type.getFilterParameter(parameter))
    }

    private interface FilterParameterSupplier {
        fun getFilterParameter(parameter: String): Any
    }

    enum class Type(private val supplier: FilterParameterSupplier) {
        STRING(object : FilterParameterSupplier {
            override fun getFilterParameter(parameter: String): Any {
                return parameter
            }
        }),
        DATE(object : FilterParameterSupplier {
            override fun getFilterParameter(parameter: String): Any {
                return parseLocalDate(parameter)
            }
        });

        fun getFilterParameter(parameter: String): Any {
            return supplier.getFilterParameter(parameter)
        }
    }
}