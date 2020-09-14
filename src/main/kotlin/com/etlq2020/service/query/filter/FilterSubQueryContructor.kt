package com.etlq2020.service.query.filter

import com.etlq2020.controller.dto.FilterDto
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

@Component
class FilterSubQueryCreator(val filterPredicateSuppliers: List<FilterPredicateSupplier>,
                            val filterParameterParsers: List<FilterParameterParser>) {

    fun createPredicate(filterDto: FilterDto): Bson {
        val field = filterDto.field
        val condition = filterDto.condition
        val parameterSupplier = filterParameterParsers
                .find { it.shouldHandle(condition.type) }!!
        val parameterValue = parameterSupplier.parseParameter(condition.parameter)
        return filterPredicateSuppliers.find { it.shouldHandle(condition.operation) }!!
                .createPredicate(field, parameterValue)
    }
}
