package com.etlq2020

import com.etlq2020.DataInputController.DataRow.Companion.parseLocalDate
import com.etlq2020.QueryDataController.*
import com.etlq2020.QueryDataController.AggregateDto.Operation.SUM
import com.etlq2020.QueryDataController.ConditionDto.FilterOperation.EQUALS
import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.*
import com.mongodb.client.model.Aggregates.match
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
        val listIndexes = getCollection().listIndexes()
        getCollection().createIndex(
                Document(mapOf("Datasource" to 1, "Campaign" to 1, "Daily" to 1)))
    }

    private fun getCollection(): MongoCollection<Document> {
        return database.getCollection("campains")
    }

    fun upsertDataRow(dataRow: DataInputController.DataRow) {
        getCollection()
                .findOneAndReplace(dataRow.getID(), dataRow.toDocument(),
                        FindOneAndReplaceOptions().upsert(true))
    }

    fun query(queryDto: QueryDto): List<Document> {
        val pipeline = listOfNotNull(
                createFilterByPredicates(queryDto.filterBy),
                createGroupByPredicates(queryDto.groupBy, queryDto.aggregate),
                createAggregatePredicates(queryDto.groupBy, queryDto.aggregate)
        ).flatten()
        return getCollection().aggregate(pipeline).allowDiskUse(true).toList()
    }

    private fun createFilterByPredicates(filterBy: List<FilterByDto>?): List<Bson>? {
        if (filterBy == null || filterBy.isEmpty()) {
            return null
        }
        return filterBy.map {
            when (it.condition.operation) {
                EQUALS -> match(Filters.eq(it.field.toLowerCase(), getFilterParameter(it.condition)))
            }
        }
    }

    private fun getFilterParameter(it: ConditionDto): Any {
        return when (it.type) {
            ConditionDto.Type.LOCAL_DATE -> parseLocalDate(it.parameter)
        }
    }

    private fun createGroupByPredicates(groupBy: List<String>?, aggregate: AggregateDto): List<Bson>? {
        if (groupBy == null || groupBy.isEmpty()) {
            return null
        }
        val id = Document()
        groupBy!!.forEach {
            id.append("$it", "$${it.toLowerCase()}")
        }
        val accumulator = when (aggregate.operation) {
            SUM -> Accumulators.sum("result", "$${aggregate.field.toLowerCase()}")
        }
        return listOf(Aggregates.group(id, accumulator))
    }

    private fun createAggregatePredicates(groupBy: List<String>?, aggregate: AggregateDto): List<Bson>? {
        if (groupBy != null && groupBy.isNotEmpty()) {
            return null
        }
        return null
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