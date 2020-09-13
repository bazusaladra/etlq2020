package com.etlq2020

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoCollection
import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.RetryPolicy
import net.jodah.failsafe.function.CheckedSupplier
import org.bson.Document
import org.bson.conversions.Bson
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.logging.Logger
import javax.annotation.PostConstruct


@Component
class DatabaseClient(@Value("#{environment.MONGODB_HOST}") val mongoHost: String) {

    companion object {
        private val logger: Logger = Logger.getLogger(DatabaseClient::class.java.name)
    }

    val mongoClient = MongoClient(listOf(ServerAddress(mongoHost, 27017)),
            MongoCredential.createScramSha256Credential("local-root", "admin", "local-pass".toCharArray()),
            MongoClientOptions.builder().build())

    val database = mongoClient.getDatabase("etlq2020")

    @PostConstruct
    fun createConstraints() {
        val listIndexes = getCollection().listIndexes()
        getCollection().createIndex(
                Document(mapOf("Datasource" to 1, "Campaign" to 1, "Daily" to 1)))
    }


    fun getCollection(): MongoCollection<Document> {
        return database.getCollection("campains")
    }

    fun <T> withFailsafe(check: CheckedSupplier<T>, maxAttempts: Int = 60): T {
        return Failsafe.with(RetryPolicy<T>()
                .onFailedAttempt { e -> e.lastFailure.printStackTrace() }
                .withDelay(Duration.ofSeconds(2))
                .withMaxAttempts(maxAttempts))
                .onFailure { e -> logger.warning("failsafe execution failed ${e.failure}") }
                .get(check)
    }
}