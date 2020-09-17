package com.etlq2020.service.query

interface QueryDtoHandler<D> {

    fun shouldHandle(element: D): Boolean

    companion object {

        fun <D> checkImplementations(values: Array<D>, suppliers: List<QueryDtoHandler<D>>) {
            values.forEach { type ->
                val size = suppliers.filter { it.shouldHandle(type) }.size
                if (size != 1) {
                    throw IllegalStateException("expected exactly 1 implementation for type $type but found $size")
                }
            }
        }
    }
}