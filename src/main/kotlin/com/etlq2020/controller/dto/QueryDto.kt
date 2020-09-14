package com.etlq2020.controller.dto

data class QueryDto(val groupBy: List<String>?, val filterBy: List<FilterDto>?,
                    val aggregate: List<AggregateDto>, val project: List<ProjectDto>?)

data class FilterDto(val field: String, val condition: ConditionDto)

data class ConditionDto(val operation: FilterOperationType,
                        val parameter: String, val type: ParameterType) {

    enum class FilterOperationType {
        EQUALS
    }

    enum class ParameterType {
        STRING, DATE
    }
}

data class AggregateDto(val field: String, val operation: OperationType) {

    enum class OperationType {
        SUM
    }
}

data class ProjectDto(val field: String, val operation: ProjectOperationDto)

data class ProjectOperationDto(val type: OperationType,
                               val parameters: List<String>) {

    enum class OperationType() {
        DIVIDE
    }
}