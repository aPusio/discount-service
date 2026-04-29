package com.pusio.discout.sercice.service

import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.*

@Service
class IpService(private val restTemplate: RestTemplate) {

    @Retryable(
        retryFor = [Exception::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 50)
    )
    fun getCountryCode(ip: String): String? {
        val url = "http://ip-api.com/json/$ip?fields=countryCode"
        val resp = restTemplate.getForObject(url, Map::class.java)
        val code = resp?.get("countryCode") as? String
        return code?.uppercase(Locale.getDefault())
    }
}