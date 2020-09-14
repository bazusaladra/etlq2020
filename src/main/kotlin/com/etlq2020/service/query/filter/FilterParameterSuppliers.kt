package com.etlq2020.service.query.filter

import com.etlq2020.controller.dto.ConditionDto
import com.etlq2020.controller.dto.DataRowDto.Companion.parseLocalDate
import org.springframework.stereotype.Component

interface FilterParameterParser {
    fun shouldHandle(operation: ConditionDto.ParameterType): Boolean
    fun parseParameter(parameter: String): Any
}

@Component
class StringFilterParameterParser : FilterParameterParser {

    override fun shouldHandle(operation: ConditionDto.ParameterType): Boolean {
        return operation == ConditionDto.ParameterType.STRING
    }

    override fun parseParameter(parameter: String): Any {
        return parameter
    }
}

@Component
class DateFilterParameterParser : FilterParameterParser {

    override fun shouldHandle(operation: ConditionDto.ParameterType): Boolean {
        return operation == ConditionDto.ParameterType.DATE
    }

    override fun parseParameter(parameter: String): Any {
        return parseLocalDate(parameter)
    }
}
