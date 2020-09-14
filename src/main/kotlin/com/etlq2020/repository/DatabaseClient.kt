package com.etlq2020.repository

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.IndexOptions
import org.bson.Document
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.logging.Logger
import javax.annotation.PostConstruct


@Component
class DatabaseClient(@Value("#{environment.MONGODB_HOST}") private val mongoHost: String) {

    companion object {
        private val logger: Logger = Logger.getLogger(DatabaseClient::class.java.name)
    }

    private val mongoClient = MongoClient(listOf(ServerAddress(mongoHost, 27017)),
            MongoCredential.createScramSha256Credential("local-root", "admin", "local-pass".toCharArray()),
            MongoClientOptions.builder().build())

    private val database = mongoClient.getDatabase("etlq2020")

    @PostConstruct
    fun createConstraints() {
        createIndexIfNotExists("DatasourceCampaignDaily")
    }

    private fun createIndexIfNotExists(name: String) {
        val notExists = getCollection().listIndexes().toList()
                .find { it["name"] == name } == null
        if (notExists) {
            getCollection().createIndex(
                    Document(mapOf("Datasource" to 1, "Campaign" to 1, "Daily" to 1)),
                    IndexOptions().unique(true).name(name)
            )
        }
    }

    fun getCollection(): MongoCollection<Document> {
        return database.getCollection("campaigns")
    }

}