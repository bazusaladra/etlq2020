package com.etlq2020

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MainController {

    @GetMapping("/")
    fun getApplicationLiveStatus(): Map<String, Boolean> {
        return listOf("live" to true).toMap()
    }
}