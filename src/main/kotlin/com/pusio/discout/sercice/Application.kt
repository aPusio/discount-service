package com.pusio.discout.sercice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.retry.annotation.EnableRetry
import org.springframework.web.client.RestTemplate

@EnableRetry
@SpringBootApplication
class Application {

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}