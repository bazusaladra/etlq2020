package com.etlq2020.service.query.project

import com.etlq2020.controller.dto.ProjectDto.ProjectOperationDto
import com.etlq2020.service.query.QueryDtoHandler
import org.bson.Document
import org.springframework.stereotype.Component


interface ProjectPredicateSupplier : QueryDtoHandler<ProjectOperationDto.OperationType> {
    fun createPredicate(parameters: List<String>): Document
}

@Component
class DivideProjectPredicateSupplier : ProjectPredicateSupplier {
    override fun shouldHandle(operationType: ProjectOperationDto.OperationType): Boolean {
        return operationType == ProjectOperationDto.OperationType.DIVIDE
    }

    override fun createPredicate(parameters: List<String>): Document {
        return Document("\$divide", parameters.map { "\$$it" })
    }
}