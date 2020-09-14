package com.etlq2020.controller

import com.etlq2020.service.input.DataInputService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.InputStream
import java.util.logging.Logger


@RestController
class DataInputController(private val dataInputService: DataInputService) {

    companion object {
        private val logger: Logger = Logger.getLogger(DataInputController::class.java.name)
    }

    @PostMapping("/send", consumes = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun saveInputData(requestBody: InputStream): Map<String, Any> {
        val processed = dataInputService.persist(requestBody)
        return listOf("loaded" to processed).toMap()
    }

}