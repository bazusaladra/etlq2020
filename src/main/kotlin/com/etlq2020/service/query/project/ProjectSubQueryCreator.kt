package com.etlq2020.service.query.project

import com.etlq2020.controller.dto.ProjectDto
import com.mongodb.client.model.Aggregates
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component


@Component
class ProjectSubQueryCreator(
        val projectPredicateSuppliers: List<ProjectPredicateSupplier>) {

    fun createPredicate(projectDto: ProjectDto): Bson {
        val operationDto = projectDto.operation
        val field = projectDto.field
        val operationType = operationDto.type
        val parameters = operationDto.parameters
        val predicate = projectPredicateSuppliers
                .find { it.shouldHandle(operationType) }!!
                .createPredicate(parameters)
        return Aggregates.project(Document(field, predicate))
    }
}
