package com.etlq2020.repository

import com.etlq2020.service.input.DataRow
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.FindOneAndReplaceOptions
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.stereotype.Component

@Component
class DataRepository(private val databaseClient: DatabaseClient) {

    fun upsertDataRow(dataRow: DataRow) {
        getCollection().findOneAndReplace(dataRow.getID(), dataRow.toDocument(),
                FindOneAndReplaceOptions().upsert(true))
    }

    fun query(pipeline: List<Bson>): List<Document> {
        return getCollection().aggregate(pipeline).allowDiskUse(true).toList()
    }

    private fun getCollection(): MongoCollection<Document> {
        return databaseClient.getCollection()
    }

}