package com.etlq2020.controller.dto

import com.etlq2020.service.query.AggregateSubQuery
import com.etlq2020.service.query.FilterSubQuery


data class QueryDto(val groupBy: List<String>?, val filterBy: List<FilterSubQuery>?,
                    val aggregate: AggregateSubQuery)