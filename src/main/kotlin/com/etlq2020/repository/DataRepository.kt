package com.etlq2020.repository

import com.etlq2020.service.DataRow
import com.etlq2020.service.query.Query
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.FindOneAndReplaceOptions
import org.bson.Document
import org.springframework.stereotype.Component


@Component
class DataRepository(private val databaseClient: DatabaseClient) {

    fun upsertDataRow(dataRow: DataRow) {
        getCollection().findOneAndReplace(dataRow.getID(), dataRow.toDocument(),
                FindOneAndReplaceOptions().upsert(true))
    }

    fun query(query: Query): List<Document> {
        val pipeline = query.createQueryPipeline()
        return getCollection().aggregate(pipeline).allowDiskUse(true).toList()
    }

    private fun getCollection(): MongoCollection<Document> {
        return databaseClient.getCollection()
    }


    fun ctr(): List<Document> {
        val id = Document().append("Datasource", "\$Datasource").append("Campaign", "\$Campaign")
        val pipeline = listOf(
                Aggregates.group(id, Accumulators.sum("Clicks", "\$Clicks"),
                        Accumulators.sum("Impressions", "\$Impressions")),
                Aggregates.project(Document("CTR",
                        Document("\$divide", listOf("\$Clicks", "\$Impressions"))))
        )
        return getCollection().aggregate(pipeline).allowDiskUse(true).toList()
    }

}