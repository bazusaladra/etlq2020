package com.etlq2020.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {

    @GetMapping("/")
    fun getApplicationLiveStatus(): Map<String, Any> {
        return listOf("live" to true).toMap()
    }
}