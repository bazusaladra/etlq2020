package com.etlq2020.service.query.project

import com.etlq2020.controller.dto.ProjectDto
import org.bson.Document
import org.springframework.stereotype.Component


interface ProjectPredicateSupplier {
    fun shouldHandle(operationType: ProjectDto.ProjectOperationDto.OperationType): Boolean
    fun createPredicate(parameters: List<String>): Document
}

@Component
class DivideProjectPredicateSupplier : ProjectPredicateSupplier {
    override fun shouldHandle(operationType: ProjectDto.ProjectOperationDto.OperationType): Boolean {
        return operationType == ProjectDto.ProjectOperationDto.OperationType.DIVIDE
    }

    override fun createPredicate(parameters: List<String>): Document {
        return Document("\$divide", parameters.map { "\$$it" })
    }
}