package com.kyotob.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.runApplication

@SpringBootApplication
@Configuration
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
