package com.etlq2020

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class EtlqMain {
}

fun main(args: Array<String>) {
    SpringApplication.run(EtlqMain::class.java, *args)
}